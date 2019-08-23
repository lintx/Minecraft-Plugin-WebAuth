<template>
    <div class="text-center mb-4">
        <h1 class="cover-heading">帮助</h1>
        <div class="text-left">
            <ol>
                <li>本系统是WebAuth插件的用户管理系统。</li>
                <li>如果您已经注册，可以 <router-link class="btn-link" to="/login">点击这里</router-link> 来登录。</li>
                <li>如果您尚未注册，且插件配置允许注册，您应该先 <router-link class="btn-link" to="/register">注册</router-link> 一个帐号，否则请联系服主或管理员来申请帐号。</li>
                <li>使用了本插件的服务器，非正版玩家，需要使用“登录凭据”来登录游戏，您第一次登录后就可以看见您的“登录凭据”，下次登录时“登录凭据”将无法查看，除非您“重置登录凭据”使得旧的凭据失效。</li>
                <li>使用“登录凭据”登录游戏服务器的方法：直接将“登录凭据”作为离线用户名登录游戏即可（请注意，“登录凭据”的有效时间为7天，7天后这个“登录凭据”将无法登录游戏，届时您需要在该系统重置“登录凭据”）</li>
                <li>注册帐号时，注册的帐号就是您的玩家名，如果服务器允许，您可以在之后自由修改玩家名且一般不会造成玩家数据丢失，部分服务器使用中文玩家名可能会发生未知错误。</li>
            </ol>
        </div>
    </div>
</template>