package org.lintx.plugin.webauth;

import lombok.Getter;
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
    @Getter
    private String notRegister = "你还没有注册，请打开网页\n{url}\n注册后登录游戏";

    @YamlConfig
    @Getter
    private String tokenIsValid = "登录凭证无效，请检查您的凭证\n如忘记凭证请打开网页\n{url}\n重置凭证";

    @YamlConfig
    @Getter
    private String tokenIsExpired = "登录凭证已过期，请打开网页\n{url}\n重置凭证";

    @YamlConfig
    @Getter
    private String nameIsValid = "玩家名中含有不允许的字符，请打开网页\n{url}\n修改玩家名或联系管理员";

    @YamlConfig
    private String tokenManageUrl = "";

    @YamlConfig
    @Getter
    private DatabaseConfig databaseConfig = new DatabaseConfig();

    @YamlConfig
    @Getter
    private int webPort = 0;

    @YamlConfig
    @Getter
    private boolean openRegister = true;

    @YamlConfig
    @Getter
    private boolean openChangePlayername = false;

    @YamlConfig
    @Getter
    private String playerNameRegexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @YamlConfig
    @Getter
    private boolean checkPlayerNameFromMojang = false;

    @YamlConfig
    @Getter
    private String httpProxyType = "http/socks";

    @YamlConfig
    @Getter
    private String httpProxyAddress = "";

    @YamlConfig
    @Getter
    private int httpProxyPort = 0;

    public String formatMessage(String message){
        return message.replaceAll("\\{url\\}",tokenManageUrl);
    }

    public static void setInstance(Config instance) {
        Config.instance = instance;
    }
}
