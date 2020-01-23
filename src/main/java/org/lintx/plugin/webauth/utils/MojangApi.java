package org.lintx.plugin.webauth.utils;

import com.google.gson.Gson;
import org.lintx.plugin.webauth.Config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MojangApi {
    public static MojangAccount getMojangAccount(String name){
        try {
            String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
            URL obj = new URL(url);
            Config config = Config.getInstance();
            HttpURLConnection conn;
            if (!config.getHttpProxyAddress().isEmpty() && config.getHttpProxyPort()>0){
                Proxy.Type proxyType = Proxy.Type.HTTP;
                if (config.getHttpProxyType().equalsIgnoreCase("socks")){
                    proxyType = Proxy.Type.SOCKS;
                }
                Proxy proxy = new Proxy(proxyType,new InetSocketAddress(config.getHttpProxyAddress(),config.getHttpProxyPort()));
                conn = (HttpURLConnection) obj.openConnection(proxy);
            }else {
                conn = (HttpURLConnection) obj.openConnection();
            }
            conn.setRequestMethod("GET");

            if (conn.getInputStream()==null){
                return new MojangAccount();
            }

            try(Reader reader = new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8)) {
                return new Gson().fromJson(reader,MojangAccount.class);
            } catch (IOException ignored) {

            }
        }catch (Exception ignore){

        }
        return new MojangAccount();
    }

    public static class MojangAccount{
        public String id = "";
        public String name = "";

        public boolean checkName(String name){
            return !name.equalsIgnoreCase(this.name);
        }

        public UUID getUUID(){
            String str = this.name;
            if (str==null || str.isEmpty()) return null;
            if (str.length()==32){
                str = str.substring(0,8) + "-" + str.substring(8,12) + "-" + str.substring(12,16) + "-" + str.substring(16,20) + "-" + str.substring(20,32);
            }
            try {
                return UUID.fromString(str);
            }catch (Exception ignore){
                return null;
            }
        }
    }
}
