package org.lintx.plugin.webauth;

import org.lintx.plugin.webauth.models.PlayerModel;

public class Message {
    static final String commandHelp = "§b/webauth player <player_id|player_name> §a查看玩家资料\n" +
            "§b/webauth player <player_id|player_name> uuid <newuuid>§a 设置玩家UUID，\n" +
            "    设置UUID一般为迁移玩家数据用（比如给玩家设置其他登录插件登录后产生的离线id的UUID或正版玩家注册帐号后给玩家设置正版ID）\n" +
            "    §c修改玩家UUID后，玩家资料会丢失，相当于一个新号（除非新UUID原来登录过游戏），请谨慎操作。\n" +
            "§b/webauth player <player_id|player_name> password <newpassword>§a 设置玩家密码，设置密码后玩家登录凭据将无效。\n" +
            "§b/webauth player <player_id|player_name> playername <newplayername>§a 修改玩家名。\n" +
            "§b/webauth player <player_id|player_name> token refresh [day]§a 刷新玩家登录凭据，day为凭据有效期（天数），不填写day时有效期为7天。\n" +
            "§b/webauth player add <username> <password>§a 新增玩家，主要用于关闭注册时手动注册玩家\n" +
            "§b/webauth reload§a 重新加载配置。\n" +
            "§b/webauth import <filename>§a 从文本文件中导入玩家数据，文件格式必须为UTF-8，\n" +
            "    每行格式可以是§b玩家名 UUID§a或§b玩家名§a\n" +
            "    不指定UUID时，将按照离线用户的规则生成玩家UUID\n" +
            "    密码为随机密码，将同时显示在消息窗口和插件目录下的对应日志文件中，\n" +
            "    导入完毕后需要将密码分发给对应的老玩家，§c并提醒玩家尽快修改密码。";
    static final String notSupport = "登录方式不受服务器核心版本不支持，请联系服主";
    static final String loginError = "登录时发生了错误，无法登录";
    static final String noPermission = "§c权限不足";
    static final String playerNotFind = "§c没有找到对应的玩家";
    static final String playerInfo = "§aid:§6{id}§a,用户名:§6{username}§a,当前玩家名:§6{name}§a,当前UUID:§6{uuid}";
    static final String uuidIsValid = "§c无效的UUID格式";
    static final String uuidIsRepeat = "§cUUID重复！";
    static final String uuidUpdateSuccess = "§aUUID更新成功";
    static final String playerNameUpdateSuccess = "§a玩家名更新成功";
    static final String tokenUpdateSuccess = "§a玩家登录凭据更新成功，新的凭据为：§b{token}§a(点击以复制)，有效期：§b{expire_time}";
    static final String passwordUpdateSuccess = "§a密码更新成功";
    static final String addPlayerFail = "§c新增玩家失败";
    static final String addPlayerSuccess = "§a新增玩家成功";
    static final String addMojangPlayer = "§a新增玩家{name}经查询是正版玩家，UUID为{uuid}。\n" +
            "§a新增并设置UUID为正版UUID请输入命令§b/webauth player add {name} 密码 confirm\n" +
            "§a新增并不设置UUID为正版UUID请输入命令§b/webauth player add {name} 密码 ignore";
    static final String playerNameRepeat = "§c玩家用户名或玩家名重复";
    static final String reloadConfig = "§a重新加载配置文件";
    static final String playerNameShort = "§c玩家名太短（不能小于4字符）";
    static final String playerNameLong = "§c玩家名太长（不能大于16字符）";
    static final String fileNotExist = "§c文件不存在";
    static final String fileNotFile = "§c目标不是一个文件";

    static String playerInfo(PlayerModel model){
        String info = playerInfo;
        info = info.replaceAll("\\{id\\}",""+model.getId());
        info = info.replaceAll("\\{username\\}",model.getUsername());
        info = info.replaceAll("\\{name\\}",model.getName());
        info = info.replaceAll("\\{uuid\\}",model.getUuid().toString());
        return info;
    }
}
