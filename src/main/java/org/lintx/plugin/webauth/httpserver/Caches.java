package org.lintx.plugin.webauth.httpserver;

import org.lintx.plugin.webauth.models.PlayerModel;
import org.lintx.plugin.webauth.utils.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Caches {
    private static class LoginLog{
        LocalDateTime lastTime;
        int count;
    }

    private static class LoginCache{
        LocalDateTime time;
        PlayerModel model;
    }
    //本类功能：1.实现登录token的缓存，2.实现登录失败的缓存
    private static Map<String,LoginCache> tokens = new HashMap<>();
    private static Map<String,LoginLog> loginLogMap = new HashMap<>();
    private static Map<String,LoginLog> registerLogMap = new HashMap<>();

    static String login(PlayerModel model){
        String token = Utils.sha1(Utils.newToken());
        if (tokens.containsKey(token)){
            return login(model);
        }
        LoginCache cache = new LoginCache();
        cache.time = LocalDateTime.now();
        cache.model = model;
        tokens.put(token, cache);
        return token;
    }

    static PlayerModel getLogin(String token){
        if (!tokens.containsKey(token)){
            return null;
        }
        return tokens.get(token).model;
    }

    static void refreshLogin(String token,PlayerModel model){
        if (tokens.containsKey(token)){
            LoginCache cache = tokens.get(token);
            cache.time = LocalDateTime.now();
            cache.model = model;
            tokens.put(token,cache);
        }
    }

    static void loginOut(String token){
        tokens.remove(token);
    }

    static void loginFail(String ip){
        LoginLog log;
        if (!loginLogMap.containsKey(ip)){
            log = new LoginLog();
            loginLogMap.put(ip,log);
        }else {
            log = loginLogMap.get(ip);
        }
        log.count += 1;
        log.lastTime = LocalDateTime.now();
        loginLogMap.put(ip,log);
    }

    static boolean canLogin(String ip){
        if (!loginLogMap.containsKey(ip)){
            return true;
        }
        LoginLog log = loginLogMap.get(ip);
        return log.count <= 5;
    }

    static void registerLog(String ip){
        LoginLog log;
        if (!registerLogMap.containsKey(ip)){
            log = new LoginLog();
            registerLogMap.put(ip,log);
        }else {
            log = registerLogMap.get(ip);
        }
        log.count += 1;
        log.lastTime = LocalDateTime.now();
        registerLogMap.put(ip,log);
    }

    static boolean canRegister(String ip){
        if (!registerLogMap.containsKey(ip)){
            return true;
        }
        LoginLog log = registerLogMap.get(ip);
        return log.count <= 0;
    }

    public static void checkCaches(){
        LocalDateTime now = LocalDateTime.now();
        Iterator<Map.Entry<String, LoginLog>> it = loginLogMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, LoginLog> entry = it.next();
            Duration duration = Duration.between(entry.getValue().lastTime,now);
            if (duration.getSeconds()>600){
                it.remove();
            }
        }

        Iterator<Map.Entry<String, LoginCache>> it1 = tokens.entrySet().iterator();
        while(it1.hasNext()) {
            Map.Entry<String, LoginCache> entry = it1.next();
            Duration duration = Duration.between(entry.getValue().time,now);
            if (duration.getSeconds()>600){
                it1.remove();
            }
        }

        Iterator<Map.Entry<String, LoginLog>> it2 = registerLogMap.entrySet().iterator();
        while(it2.hasNext()) {
            Map.Entry<String, LoginLog> entry = it2.next();
            Duration duration = Duration.between(entry.getValue().lastTime,now);
            if (duration.getSeconds()>600){
                it2.remove();
            }
        }
    }
}
