package org.lintx.plugin.webauth.config;

import org.lintx.plugins.modules.configure.YamlConfig;

public class DatabaseConfig {
    public enum DatabaseType{
        MYSQL,
        SQLITE
    }

    @YamlConfig
    private DatabaseType type = DatabaseType.MYSQL;
    @YamlConfig
    private String mysqlUri = "jdbc:mysql://localhost:3306/database?autoReconnect=true&useSSL=false&characterEncoding=utf-8&useUnicode=true";
    @YamlConfig
    private String mysqlUser = "root";
    @YamlConfig
    private String mysqlPassword = "root";

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public String getMysqlUri() {
        return mysqlUri;
    }

    public void setMysqlUri(String mysqlUri) {
        this.mysqlUri = mysqlUri;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

}
