package org.lintx.plugin.webauth.httpserver;

import com.google.gson.annotations.SerializedName;

public class InputModel {
    @SerializedName("token")
    private String token="";//登录凭据
    @SerializedName("username")
    private String username="";
    @SerializedName("password")
    private String password="";
    @SerializedName("playerName")
    private String playerName="";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
