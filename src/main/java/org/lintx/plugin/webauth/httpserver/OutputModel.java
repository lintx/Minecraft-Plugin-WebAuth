package org.lintx.plugin.webauth.httpserver;

import com.google.gson.annotations.SerializedName;

public class OutputModel {
    @SerializedName("code")
    private int code=0;//0失败，1成功
    @SerializedName("message")

    private String message="";//code=0时，代表失败原因，或者部分仅提示的返回消息的原因
    @SerializedName("token")
    private String token="";//登录成功后返回的token，需要带入到除了注册和登录之外的其他请求中
    @SerializedName("userToken")
    private String userToken="";//游戏登录token
    @SerializedName("userTokenTime")
    private String userTokenTime="";//游戏登录token到期时间
    @SerializedName("userId")
    private int userId=0;
    @SerializedName("playerName")
    private String playerName="";
    @SerializedName("openRegister")
    private boolean openRegister = false;
    @SerializedName("openChangePlayerName")
    private boolean openChangePlayerName = false;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserTokenTime() {
        return userTokenTime;
    }

    public void setUserTokenTime(String userTokenTime) {
        this.userTokenTime = userTokenTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isOpenRegister() {
        return openRegister;
    }

    public void setOpenRegister(boolean openRegister) {
        this.openRegister = openRegister;
    }

    public boolean isOpenChangePlayerName() {
        return openChangePlayerName;
    }

    public void setOpenChangePlayerName(boolean openChangePlayerName) {
        this.openChangePlayerName = openChangePlayerName;
    }
}
