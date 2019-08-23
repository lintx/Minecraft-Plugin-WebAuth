package org.lintx.plugin.webauth;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.lintx.plugin.webauth.models.PlayerModel;

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
            }
        }
        sender.sendMessage(new TextComponent(Message.commandHelp));
    }
}
