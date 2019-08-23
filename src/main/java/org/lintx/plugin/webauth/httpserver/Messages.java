package org.lintx.plugin.webauth.httpserver;

public class Messages {
    static final String validRequest = "无效请求";
    static final String usernameShort = "用户名太短（不能小于4字符）";
    static final String usernameLong = "用户名太长（不能大于16字符）";
    static final String usernameRepeat = "用户名重复";
    static final String playerNameRepeat = "玩家名重复";
    static final String playerNameShort = "玩家名太短（不能小于4字符）";
    static final String playerNameLong = "玩家名太长（不能大于16字符）";
    static final String repeatRegister = "您已经注册过了，请勿重复注册";
    static final String registerSuccess = "注册成功";
    static final String registerFail = "注册失败，请重试";
    static final String loginFail = "登录失败，用户名或密码错误";
    static final String ipLoginFail = "失败次数过多，请等一会再试";
    static final String loginSuccess = "登录成功";
    static final String registerIsClose = "服务器已经关闭了注册功能";
    static final String notLogin = "登录信息已失效，请重新登录";
    static final String loginOut = "已经退出登录";
    static final String changePlayerNameIsClose = "服务器已经关闭了修改玩家名功能";
    static final String changePlayerNameSuccess = "修改玩家名成功";
    static final String changePlayerNameFail = "修改玩家名失败，请重试";
    static final String changePasswordSuccess = "修改密码成功";
    static final String changePasswordFail = "修改密码失败，请重试";
    static final String refreshTokenSuccess = "刷新登录凭据成功";
    static final String refreshTokenFail = "刷新登录凭据失败，请重试";
}
