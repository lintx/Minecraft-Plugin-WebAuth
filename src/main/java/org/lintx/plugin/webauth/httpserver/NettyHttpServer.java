package org.lintx.plugin.webauth.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.lintx.plugin.webauth.WebAuth;

import java.io.File;

public class NettyHttpServer {
    private final int port;
    private ServerBootstrap bootstrap;
    private NioEventLoopGroup group;
    private final WebAuth plugin;
    private final File rootFolder;

    public NettyHttpServer(int port,WebAuth plugin,File rootFolder) {
        this.port = port;
        this.plugin = plugin;
        this.rootFolder = rootFolder;
    }

    public void start(){
        bootstrap = new ServerBootstrap();
        this.group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("decoder",new HttpRequestDecoder())
                                .addLast("encoder",new HttpResponseEncoder())
                                .addLast("aggregator",new HttpObjectAggregator(512*1024))
                                .addLast("handler",new NettyHttpHandler(plugin,rootFolder));
                    }
                })
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE);
        try {
            bootstrap.bind(port).sync();
        } catch (InterruptedException ignored) {

        }
    }

    public void stop(){
        try {
            group.shutdownGracefully();
        }catch (Exception | Error ignored){}
    }
}
