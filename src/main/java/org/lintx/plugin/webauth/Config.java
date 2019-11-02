package org.lintx.plugin.webauth;

import org.lintx.plugin.webauth.config.DatabaseConfig;
import org.lintx.plugins.modules.configure.Configure;
import org.lintx.plugins.modules.configure.YamlConfig;

import java.io.File;

@YamlConfig
public class Config {
    private static Config instance = new Config();

    public static Config getInstance(){
        if (instance==null) instance = new Config();
        return instance;
    }

    public void load(WebAuth plugin){
        Configure.bungeeLoad(plugin, this);
        File file = new File(plugin.getDataFolder(),"config.yml");
        if (!file.exists()){
            Configure.bungeeSave(plugin,this);
        }
    }

    @YamlConfig
    private String notRegister = "你还没有注册，请打开网页\n{url}\n注册后登录游戏";
    @YamlConfig
    private String tokenIsValid = "登录凭证无效，请检查您的凭证\n如忘记凭证请打开网页\n{url}\n重置凭证";
    @YamlConfig
    private String tokenIsExpired = "登录凭证已过期，请打开网页\n{url}\n重置凭证";
    @YamlConfig
    private String nameIsValid = "玩家名中含有不允许的字符，请打开网页\n{url}\n修改玩家名或联系管理员";
    @YamlConfig
    private String tokenManageUrl = "";
    @YamlConfig
    private DatabaseConfig databaseConfig = new DatabaseConfig();
    @YamlConfig
    private int webPort = 0;
    @YamlConfig
    private boolean openRegister = true;
    @YamlConfig
    private boolean openChangePlayername = false;
    @YamlConfig
    private String playerNameRegexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    public String getNameIsValid() {
        return nameIsValid;
    }

    public void setNameIsValid(String nameIsValid) {
        this.nameIsValid = nameIsValid;
    }

    public String getPlayerNameRegexp() {
        return playerNameRegexp;
    }

    public void setPlayerNameRegexp(String playerNameRegexp) {
        this.playerNameRegexp = playerNameRegexp;
    }

    public String formatMessage(String message){
        return message.replaceAll("\\{url\\}",tokenManageUrl);
    }

    public String getNotRegister() {
        return notRegister;
    }

    public void setNotRegister(String notRegister) {
        this.notRegister = notRegister;
    }

    public String getTokenIsValid() {
        return tokenIsValid;
    }

    public void setTokenIsValid(String tokenIsValid) {
        this.tokenIsValid = tokenIsValid;
    }

    public String getTokenIsExpired() {
        return tokenIsExpired;
    }

    public void setTokenIsExpired(String tokenIsExpired) {
        this.tokenIsExpired = tokenIsExpired;
    }

    public static void setInstance(Config instance) {
        Config.instance = instance;
    }

    public String getTokenManageUrl() {
        return tokenManageUrl;
    }

    public void setTokenManageUrl(String tokenManageUrl) {
        this.tokenManageUrl = tokenManageUrl;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public boolean isOpenRegister() {
        return openRegister;
    }

    public void setOpenRegister(boolean openRegister) {
        this.openRegister = openRegister;
    }

    public boolean isOpenChangePlayername() {
        return openChangePlayername;
    }

    public void setOpenChangePlayername(boolean openChangePlayername) {
        this.openChangePlayername = openChangePlayername;
    }
}
