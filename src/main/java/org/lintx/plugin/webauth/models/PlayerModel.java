package org.lintx.plugin.webauth.models;

import org.lintx.plugin.webauth.utils.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class PlayerModel {
    private int id;
    private String username;
    private String password;
    private String slat;
    private String name;
    private UUID uuid;
    private String token;
    private LocalDateTime token_time;

    public PlayerModel(){

    }

    public PlayerModel(String username,String password){
        this(username,password,Utils.newUUID());
    }

    public PlayerModel(String username,String password,UUID uuid){
        setSlat(Utils.sha1(Utils.newToken()));
        setUsername(username);
        setName(username);
        updatePassword(password);
        setUuid(uuid);
        setToken("");
        setToken_time(LocalDateTime.now());
    }

    public boolean checkPassword(String password){
        String string = password + getSlat();
        String sign = Utils.sha1(string);
        return sign.equals(getPassword());
    }

    public boolean tokenIsEffective(){
        if (token.equals("")) return false;
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(token_time,now);
        return duration.toDays() < 7;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void updatePassword(String password){
        String string = password + getSlat();
        String sign = Utils.sha1(string);
        setPassword(sign);
        setToken("");
    }

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void updateToken(String token){
        setToken(Utils.sha1(token));
        setToken_time(LocalDateTime.now());
    }

    public LocalDateTime getToken_time() {
        return token_time;
    }

    public void setToken_time(LocalDateTime token_time) {
        this.token_time = token_time;
    }

    public void setToken_timeString(String timeString){
        setToken_time(Utils.str2DateTime(timeString));
    }

    public String getToken_timeString(){
        return Utils.dateTime2String(this.token_time);
    }

    public String getToken_expiredTimeString(){
        return Utils.dateTime2String(this.token_time.plusDays(7));
    }
}
