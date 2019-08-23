package org.lintx.plugin.webauth;

import com.google.common.base.Charsets;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.lintx.plugin.webauth.models.PlayerModel;
import org.lintx.plugin.webauth.utils.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends Command {
    private WebAuth auth;

    public Commands(WebAuth auth,String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.auth = auth;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("webauth")){
            sender.sendMessage(new TextComponent(Message.noPermission));
            return;
        }
        if (args.length>0){
            String child = args[0];
            if (child.equalsIgnoreCase("player")){
                if (args.length>=2){
                    String _id = args[1];
                    if (_id.equalsIgnoreCase("add")){
                        if (args.length>=4){
                            String username = args[2];
                            if (!auth.getModel().checkPlayerUsername(username)){
                                sender.sendMessage(new TextComponent(Message.playerNameRepeat));
                                return;
                            }
                            if (username.length()<4){
                                sender.sendMessage(new TextComponent(Message.playerNameShort));
                                return;
                            }
                            if (username.getBytes().length>16){
                                sender.sendMessage(new TextComponent(Message.playerNameLong));
                                return;
                            }
                            String password = args[3];
                            PlayerModel model = new PlayerModel(username,password);
                            if (auth.getModel().insertPlayer(model)){
                                model = auth.getModel().getPlayerWithPlayerName(username);
                                sender.sendMessage(new TextComponent(Message.addPlayerSuccess));
                                sender.sendMessage(new TextComponent(Message.playerInfo(model)));
                            }else {
                                sender.sendMessage(new TextComponent(Message.addPlayerFail));
                            }
                            return;
                        }
                        sender.sendMessage(new TextComponent(Message.commandHelp));
                        return;
                    }
                    PlayerModel model = null;
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher isNum = pattern.matcher(_id);
                    if (isNum.matches()){
                        int id = Integer.parseInt(_id);
                        model = auth.getModel().getPlayerWithId(id);
                    }else {
                        model = auth.getModel().getPlayerWithPlayerName(_id);
                    }
                    if (model==null){
                        sender.sendMessage(new TextComponent(Message.playerNotFind));
                        return;
                    }

                    if (args.length>=4){
                        String action = args[2];
                        String val = args[3];
                        if (action.equalsIgnoreCase("password")){
                            model.updatePassword(val);
                            sender.sendMessage(new TextComponent(Message.passwordUpdateSuccess));
                            return;
                        }else if (action.equalsIgnoreCase("uuid")) {
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(val);
                            }catch (IllegalArgumentException ignore){
                                sender.sendMessage(new TextComponent(Message.uuidIsValid));
                                return;
                            }
                            if (auth.getModel().getPlayerWithUUID(uuid)!=null){
                                sender.sendMessage(new TextComponent(Message.uuidIsRepeat));
                                return;
                            }
                            model.setUuid(uuid);
                            auth.getModel().updatePlayer(model);
                            model = auth.getModel().getPlayerWithId(model.getId());
                            sender.sendMessage(new TextComponent(Message.uuidUpdateSuccess));
                            sender.sendMessage(new TextComponent(Message.playerInfo(model)));
                            return;
                        }else if (action.equalsIgnoreCase("playername")){
                            if (!auth.getModel().checkPlayerUsername(val)){
                                sender.sendMessage(new TextComponent(Message.playerNameRepeat));
                                return;
                            }
                            if (val.length()<4){
                                sender.sendMessage(new TextComponent(Message.playerNameShort));
                                return;
                            }
                            if (val.getBytes().length>16){
                                sender.sendMessage(new TextComponent(Message.playerNameLong));
                                return;
                            }
                            model.setName(val);
                            auth.getModel().updatePlayer(model);
                            model = auth.getModel().getPlayerWithId(model.getId());
                            sender.sendMessage(new TextComponent(Message.playerNameUpdateSuccess));
                            sender.sendMessage(new TextComponent(Message.playerInfo(model)));
                            return;
                        }
                    }else {
                        sender.sendMessage(new TextComponent(Message.playerInfo(model)));
                        return;
                    }
                }
            }else if (child.equalsIgnoreCase("reload")){
                auth.reload();
                sender.sendMessage(new TextComponent(Message.reloadConfig));
                return;
            }else if (child.equalsIgnoreCase("import")){
                if (args.length>=2){
                    String filename = args[1];
                    auth.getProxy().getScheduler().runAsync(auth, () -> importPlayerData(sender,filename));
                    return;
                }
            }
        }
        sender.sendMessage(new TextComponent(Message.commandHelp));
    }

    private void importPlayerData(CommandSender sender,String filename){
        File file = new File(auth.getDataFolder(),filename);
        if (!file.exists()){
            sender.sendMessage(new TextComponent(Message.fileNotExist));
            return;
        }
        if (!file.isFile()){
            sender.sendMessage(new TextComponent(Message.fileNotFile));
            return;
        }
        sender.sendMessage(new TextComponent("检测到文件存在，开始处理数据，请稍后"));
        String logFilename = filename + ".log";
        File logFile = new File(auth.getDataFolder(),logFilename);

        try (InputStream inputStream = new FileInputStream(file)) {
            Reader r = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(r);

            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
            String line;
            while ((line = br.readLine()) != null) {
                sender.sendMessage(new TextComponent(addUser(line, writer)));
            }
            br.close();
            r.close();
            writer.close();
        } catch (IOException ignored) {

        }
        sender.sendMessage(new TextComponent("数据处理完毕，记录已经存储在" + logFilename + "中，请分发密码后提醒玩家及时修改密码。"));
    }

    private String addUser(String line,BufferedWriter writer){
        String[] arr = line.split("\\s+");
        String name = arr[0];

        if (name.length()<4){
            return addUserResultMessage(writer,line,false,"玩家名长度太短");
        }
        if (name.getBytes().length>16){
            return addUserResultMessage(writer,line,false,"玩家名长度太长");
        }
        if (!auth.getModel().checkPlayerUsername(name)){
            return addUserResultMessage(writer,line,false,"玩家名已经存在");
        }

        UUID uuid;
        if (arr.length>=2){
            try {
                uuid = UUID.fromString(arr[1]);
            }catch (IllegalArgumentException ignored){
                return addUserResultMessage(writer,line,false,"UUID格式不正确");
            }
        }else {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        }
        if (auth.getModel().getPlayerWithUUID(uuid)!=null){
            return addUserResultMessage(writer,line,false,"UUID重复");
        }

        String password = Utils.newPassword();
        PlayerModel model = new PlayerModel(name,password,uuid);
        boolean success = auth.getModel().insertPlayer(model);
        if (success){
            return addUserResultMessage(writer,line, true,password);
        }else {
            return addUserResultMessage(writer,line, false,"插入到数据库失败");
        }
    }

    private String addUserResultMessage(BufferedWriter writer,String line,boolean success,String extra){
        String message = "数据：";
        message += line;
        message += " ，状态：";
        if (success){
            message += "成功，密码：";
            message += extra;
        }else {
            message += "失败，原因：";
            message += extra;
        }
        try {
            writer.write(message + "\n");
        } catch (IOException ignored) {

        }
        return message;
    }
}
