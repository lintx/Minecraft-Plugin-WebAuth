package org.lintx.plugin.webauth;

import org.lintx.plugin.webauth.models.PlayerModel;

public class Message {
    static final String commandHelp = "§b/webauth player <player_id|player_name> §a查看玩家资料\n" +
            "§b/webauth player <player_id|player_name> uuid <newuuid>§a 设置玩家UUID，\n" +
            "    设置UUID一般为迁移玩家数据用（比如给玩家设置其他登录插件登录后产生的离线id的UUID或正版玩家注册帐号后给玩家设置正版ID）\n" +
            "    §c修改玩家UUID后，玩家资料会丢失，相当于一个新号（除非新UUID原来登录过游戏），请谨慎操作。\n" +
            "§b/webauth player <player_id|player_name> password <newpassword>§a 设置玩家密码，设置密码后玩家登录凭据将无效。\n" +
            "§b/webauth player <player_id|player_name> playername <newplayername>§a 修改玩家名。\n" +
            "§b/webauth player add <username> <password>§a 新增玩家，主要用于关闭注册时手动注册玩家\n" +
            "§b/webauth reload§a 重新加载配置。";
    static final String notSupport = "登录方式不受服务器核心版本不支持，请联系服主";
    static final String noPermission = "§c权限不足";
    static final String playerNotFind = "§c没有找到对应的玩家";
    static final String playerInfo = "§aid:§6{id}§a,当前用户名:§6{username}§a,当前玩家名:§6{name}§a,当前UUID:§6{uuid}";
    static final String uuidIsValid = "§c无效的UUID格式";
    static final String uuidUpdateSuccess = "§cUUID更新成功";
    static final String playerNameUpdateSuccess = "§c玩家名更新成功";
    static final String passwordUpdateSuccess = "§c密码更新成功";
    static final String addPlayerFail = "§c新增玩家失败";
    static final String addPlayerSuccess = "§a新增玩家成功";
    static final String playerNameRepeat = "§c玩家用户名或玩家名重复";
    static final String reloadConfig = "§a重新加载配置文件";
    static final String playerNameShort = "玩家名太短（不能小于4字符）";
    static final String playerNameLong = "玩家名太长（不能大于16字符）";

    static String playerInfo(PlayerModel model){
        String info = playerInfo;
        info = info.replaceAll("\\{id\\}",""+model.getId());
        info = info.replaceAll("\\{username\\}",model.getUsername());
        info = info.replaceAll("\\{name\\}",model.getName());
        info = info.replaceAll("\\{uuid\\}",model.getUuid().toString());
        return info;
    }
}
