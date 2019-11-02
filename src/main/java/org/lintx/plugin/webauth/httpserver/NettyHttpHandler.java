package org.lintx.plugin.webauth.httpserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.lintx.plugin.webauth.Config;
import org.lintx.plugin.webauth.WebAuth;
import org.lintx.plugin.webauth.models.PlayerModel;
import org.lintx.plugin.webauth.utils.Utils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

public class NettyHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private AsciiString htmlType = AsciiString.cached("text/html");
    private AsciiString jsonType = HttpHeaderValues.APPLICATION_JSON;
    private AsciiString jsType = AsciiString.cached("text/javascript");
    private AsciiString cssType = AsciiString.cached("text/css");
    private AsciiString jpegType = AsciiString.cached("image/jpeg");
    private final WebAuth plugin;
    private final File rootFolder;
    private final String rootPath = "/bungeewebauth";
    private final String apiPath = "/api";
    private final String resourcesPath = "/static";
    private final Gson gson = new Gson();

    NettyHttpHandler(WebAuth plugin,File rootFolder){
        this.plugin = plugin;
        this.rootFolder = rootFolder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        try {
            URI uri = new URI(request.uri());
            String path = uri.getPath();
            if (path.equals("/")){
                writeIndex(ctx);
                return;
            }
            if (path.startsWith(rootPath)){
                path = path.substring(rootPath.length());
                if (path.equals("/")){
                    writeIndex(ctx);
                    return;
                }
                if (path.startsWith(apiPath)){
                    if (request.method()!=HttpMethod.POST){
                        write404(ctx);
                        return;
                    }
                    path = path.substring(apiPath.length());
                    InputModel inputModel = null;
                    try {
                        ByteBuf buf = request.content();
                        String body = buf.toString(StandardCharsets.UTF_8);
                        inputModel = gson.fromJson(body,new TypeToken<InputModel>(){}.getType());
                    }catch (Exception ignored){
                    }

                    InetAddress address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress();
                    String ip = address.getHostAddress();

                    if (!Caches.canLogin(ip)){
                        writeError(ctx,Messages.ipLoginFail);
                        return;
                    }
                    PlayerModel model;
                    if (path.equals("/index")){
                        //返回用户id、token过期时间、玩家名
                        OutputModel outputModel = new OutputModel();
                        outputModel.setCode(1);
                        outputModel.setOpenRegister(Config.getInstance().isOpenRegister());
                        outputModel.setOpenChangePlayerName(Config.getInstance().isOpenChangePlayername());
                        if (inputModel!=null){
                            model = Caches.getLogin(inputModel.getToken());
                            if (model!=null){
                                outputModel.setUserId(model.getId());
                                outputModel.setPlayerName(model.getName());
                                outputModel.setUserTokenTime(model.getToken_expiredTimeString());

                                if (!model.tokenIsEffective()){
                                    refreshPlayerToken(ctx,model,outputModel);
                                    model = WebAuth.plugin.getModel().getPlayerWithId(model.getId());
                                    Caches.refreshLogin(inputModel.getToken(),model);
                                }else {
                                    writeModel(ctx,outputModel);
                                }
                                return;
                            }
                        }
                        writeModel(ctx,outputModel);
                        return;
                    }

                    if (inputModel==null){
                        writeError(ctx,Messages.validRequest);
                        return;
                    }
                    if (path.equals("/register")){
                        //注册,需要判断是否开启注册
                        if (!Config.getInstance().isOpenRegister()){
                            writeError(ctx,Messages.registerIsClose);
                            return;
                        }
                        if (!Caches.canRegister(ip)){
                            writeError(ctx,Messages.repeatRegister);
                            return;
                        }
                        if (inputModel.getUsername().length()<4){
                            writeError(ctx,Messages.usernameShort);
                            return;
                        }
                        if (inputModel.getUsername().getBytes().length>16){
                            writeError(ctx,Messages.usernameLong);
                            return;
                        }
                        if (!WebAuth.plugin.getModel().checkPlayerUsername(inputModel.getUsername())){
                            writeError(ctx,Messages.usernameRepeat);
                            Caches.loginFail(ip);
                            return;
                        }
                        model = new PlayerModel(inputModel.getUsername(),inputModel.getPassword());
                        if (WebAuth.plugin.getModel().insertPlayer(model)){
                            writeSuccess(ctx,Messages.registerSuccess);
                            Caches.registerLog(ip);
                            return;
                        }else {
                            writeError(ctx,Messages.registerFail);
                            return;
                        }
                    }else if (path.equals("/login")){
                        //登录，取得用户名密码后登录，返回登录token
                        model = WebAuth.plugin.getModel().getPlayerWithUsername(inputModel.getUsername());
                        if (model==null || !model.checkPassword(inputModel.getPassword())){
                            Caches.loginFail(ip);
                            writeError(ctx,Messages.loginFail);
                        }else {
                            String token = Caches.login(model);
                            OutputModel outputModel = new OutputModel();
                            outputModel.setToken(token);
                            outputModel.setCode(1);
                            outputModel.setMessage(Messages.loginSuccess);
                            outputModel.setPlayerName(model.getName());
                            outputModel.setUserId(model.getId());
                            if (!model.tokenIsEffective()){
                                refreshPlayerToken(ctx,model,outputModel);
                                model = WebAuth.plugin.getModel().getPlayerWithId(model.getId());
                                Caches.refreshLogin(token,model);
                            }else {
                                outputModel.setUserTokenTime(model.getToken_expiredTimeString());
                                writeModel(ctx,outputModel);
                            }
                            return;
                        }
                    }else{
                        //这里都是需要先登录的，所以需要先检验登录状态
                        model = Caches.getLogin(inputModel.getToken());
                        if (model==null || model.getId()==0){
                            writeError(ctx,Messages.notLogin);
                            Caches.loginFail(ip);
                            return;
                        }
                        Caches.refreshLogin(inputModel.getToken(),model);
                        if (path.equals("/changeplayername")){
                            //接收新用户名，并修改
                            if (!Config.getInstance().isOpenChangePlayername()){
                                writeError(ctx,Messages.changePlayerNameIsClose);
                                return;
                            }
                            if (inputModel.getPlayerName().getBytes().length<4){
                                writeError(ctx,Messages.playerNameShort);
                                return;
                            }
                            if (inputModel.getPlayerName().getBytes().length>16){
                                writeError(ctx,Messages.playerNameLong);
                                return;
                            }
                            if (!Pattern.matches(Config.getInstance().getPlayerNameRegexp(),inputModel.getPlayerName())){
                                writeError(ctx,Messages.notMatchPlayerNameRegexp);
                                return;
                            }
                            if (!WebAuth.plugin.getModel().checkPlayerName(inputModel.getPlayerName())){
                                writeError(ctx,Messages.playerNameRepeat);
                                return;
                            }
                            model.setName(inputModel.getPlayerName());
                            if (WebAuth.plugin.getModel().updatePlayer(model)){
                                writeSuccess(ctx,Messages.changePlayerNameSuccess);
                                model = WebAuth.plugin.getModel().getPlayerWithId(model.getId());
                                Caches.refreshLogin(inputModel.getToken(),model);
                                return;
                            }else {
                                writeError(ctx,Messages.changePlayerNameFail);
                                return;
                            }
                        }else if (path.equals("/changepassword")){
                            //接收新密码，并修改
                            model.updatePassword(inputModel.getPassword());
                            if (WebAuth.plugin.getModel().updatePlayer(model)){
                                writeSuccess(ctx,Messages.changePasswordSuccess);
                                model = WebAuth.plugin.getModel().getPlayerWithId(model.getId());
                                Caches.refreshLogin(inputModel.getToken(),model);
                                return;
                            }else {
                                writeError(ctx,Messages.changePasswordFail);
                                return;
                            }
                        }else if (path.equals("/refresh")){
                            //刷新token，返回新的token和过期时间
                            OutputModel outputModel = new OutputModel();
                            String playerToken = Utils.newToken();
                            model.updateToken(playerToken);
                            if (WebAuth.plugin.getModel().updatePlayer(model)){
                                outputModel.setUserToken(playerToken);
                                outputModel.setUserTokenTime(model.getToken_expiredTimeString());
                                outputModel.setCode(1);
                                outputModel.setMessage(Messages.refreshTokenSuccess);
                                model = WebAuth.plugin.getModel().getPlayerWithId(model.getId());
                                Caches.refreshLogin(inputModel.getToken(),model);
                            }else {
                                outputModel.setCode(0);
                                outputModel.setMessage(Messages.refreshTokenFail);
                            }
                            writeModel(ctx,outputModel);
                        }else if (path.equals("/loginout")){
                            Caches.loginOut(inputModel.getToken());
                            writeSuccess(ctx,Messages.loginOut);
                            return;
                        }
                    }
                }else if (path.startsWith(resourcesPath)){
                    if (request.method()!=HttpMethod.GET){
                        write404(ctx);
                        return;
                    }
                    writeFile(ctx,path);
                    return;
                }
            }
            write404(ctx);
        }
        catch (Exception e) {
            e.printStackTrace();
            write404(ctx);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != cause) cause.printStackTrace();
        if (null != ctx) ctx.close();
    }

    private void writeIndex(ChannelHandlerContext ctx){
        writeFile(ctx,"/index.html");
    }

    private void writeFile(ChannelHandlerContext ctx,String path){
        try {
            File file = new File(rootFolder,path);
            String canonical = file.getCanonicalPath();
            if (canonical.startsWith(rootFolder.getCanonicalPath()) && file.exists() && file.isFile()){
                AsciiString mime;
                String cs = canonical.toLowerCase(Locale.ROOT);
                if (cs.endsWith(".jpg") || cs.endsWith(".jpeg")){
                    mime = jpegType;
                }else if (cs.endsWith(".html")){
                    mime = htmlType;
                }else if (cs.endsWith(".js")){
                    mime = jsType;
                }else if (cs.endsWith(".css")){
                    mime = cssType;
                }else {
                    write404(ctx);
                    return;
                }

                final RandomAccessFile raf = new RandomAccessFile(file,"r");
                long fileLength = raf.length();
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
                HttpHeaders heads = response.headers();
                heads.add(HttpHeaderNames.CONTENT_TYPE, mime + "; charset=UTF-8");
                heads.add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.write(response);

                ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(),0,fileLength),ctx.newProgressivePromise());

                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {

                    }

                    @Override
                    public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                        raf.close();
                    }
                });
            }else {
                write404(ctx);
            }
        } catch (IOException ignored) {
            write404(ctx);
        }
    }

    private void refreshPlayerToken(ChannelHandlerContext ctx,PlayerModel model, OutputModel outputModel){
        String playerToken = Utils.newToken();
        model.updateToken(playerToken);
        if (plugin.getModel().updatePlayer(model)){
            outputModel.setUserToken(playerToken);
            outputModel.setUserTokenTime(model.getToken_expiredTimeString());
        }
        writeModel(ctx,outputModel);
    }

    private void write404(ChannelHandlerContext ctx){
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        String string = "<h1>File Not Found</h1>";
        response.content().writeBytes(Unpooled.wrappedBuffer(string.getBytes()));
        write(ctx,response,htmlType);
    }

    private void writeModel(ChannelHandlerContext ctx,OutputModel model){
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        String string = gson.toJson(model);
        response.content().writeBytes(Unpooled.wrappedBuffer(string.getBytes(StandardCharsets.UTF_8)));
        write(ctx,response,jsonType);
    }

    private void writeError(ChannelHandlerContext ctx,String message){
        OutputModel model = new OutputModel();
        model.setMessage(message);
        writeModel(ctx,model);
    }

    private void writeSuccess(ChannelHandlerContext ctx,String message){
        OutputModel model = new OutputModel();
        model.setCode(1);
        model.setMessage(message);
        writeModel(ctx,model);
    }

    private void write(ChannelHandlerContext ctx,DefaultFullHttpResponse response,AsciiString type){
        HttpHeaders heads = response.headers();
        heads.add(HttpHeaderNames.CONTENT_TYPE, type + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.write(response);
    }
}
