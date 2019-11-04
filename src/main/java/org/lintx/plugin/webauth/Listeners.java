package org.lintx.plugin.webauth;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import org.lintx.plugin.webauth.models.PlayerModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listeners implements Listener {
    private WebAuth auth;

    Listeners(WebAuth auth){
        this.auth = auth;
    }

    @EventHandler(priority = 127)
    public void onLogin(PreLoginEvent event){
        if (event.isCancelled()){
            return;
        }
        PendingConnection connection = event.getConnection();
        String name = connection.getName();
        boolean isToken = name.length() == 16 && name.startsWith("=") && name.endsWith("=");
        if (!connection.isOnlineMode() || isToken){
            if (event.getConnection() instanceof InitialHandler){
                try {
                    Config config = Config.getInstance();
                    PlayerModel model = auth.getModel().getPlayerWithToken(name);
                    if (model==null){
                        if (isToken){
                            connection.disconnect(new TextComponent(config.formatMessage(config.getTokenIsValid())));
                        }else {
                            connection.disconnect(new TextComponent(config.formatMessage(config.getNotRegister())));
                        }
                    }else if (!model.tokenIsEffective()){
                        connection.disconnect(new TextComponent(config.formatMessage(config.getTokenIsExpired())));
                    }else if (!Pattern.matches(config.getPlayerNameRegexp(),model.getName())) {
                        connection.disconnect(new TextComponent(config.formatMessage(config.getNameIsValid())));
                    }else {
                        try {
                            InitialHandler handler = (InitialHandler)event.getConnection();
                            handler.setOnlineMode(false);
                            handler.getLoginRequest().setData(model.getName());
                            handler.setUniqueId(model.getUuid());
                        }catch (Error | Exception e){
                            e.printStackTrace();
                            connection.disconnect(new TextComponent(Message.notSupport));
                        }
                    }
                }catch (Error | Exception e){
                    e.printStackTrace();
                    connection.disconnect(new TextComponent(Message.loginError));
                }
            }else {
                connection.disconnect(new TextComponent(Message.notSupport));
            }
        }
    }
}
