### WebAuth 更好的离线登录插件

#### 优点
1. 和正版登录共存。
2. 进入游戏前完成登录校验，有效防止压测假人、权限问题；玩家进入游戏后不用输入繁琐的指令，享受正版玩家的游戏体验。
3. 可以修改玩家名而玩家数据不丢失。
4. 关闭注册后可以实现已注册玩家自动“白名单”，未注册玩家无法进入游戏的效果。
5. 没有第三方服务器依赖。

#### 安装方法
1. 下载本插件，并将插件复制到BungeeCord的plugins目录中。
2. 下载前置插件[ConfigureCore](https://github.com/lintx/ConfigureCore-for-Minecraft-plugins)并将它也复制到BungeeCord的plugins目录中。
3. 如果您使用SQLite作为数据库，那么还需要安装BungeeCord端的SQLite支持插件比如[SQLite for BungeeCord](https://www.spigotmc.org/resources/sqlite-for-bungeecord.57191/)（注意：安装这个插件服务端需要重启否则无法安装成功）。
4. 重启BungeeCord服务端或使用BungeePluginManager插件加载本插件（需要你的BungeeCord安装了这个插件）。
5. 修改配置文件，注意webPort需要修改到1024以上，且需要打开该端口的防火墙以使得外部网络可以访问这个端口。
6. 使用命令`/webauth reload`重新加载配置文件。
7. 完成。

注意：
1. 本插件需要安装到BungeeCord中。
2. 需要使用类似Spigot、Paper之类支持BungeeCord端口转发的服务端。
3. 如果子服是基于Spigot的服务端，需要将`spigot.yml`的`settings`中的`bungeecord`设置为`true`。
4. BungeeCord端的`config.yml`的`ip_forward`要设置为`true`。


#### 配置文件
```yaml
#当玩家因为没有注册而无法登录游戏时的提示{url}会被替换为tokenManageUrl字段的内容，下同（该设置仅在BungeeCord的online_mode设置为false时有效）
notRegister: |-
  你还没有注册，请打开网页
  {url}
  注册后登录游戏
#登录凭据无效时，无法进入游戏的提示
tokenIsValid: |-
  登录凭证无效，请检查您的凭证
  如忘记凭证请打开网页
  {url}
  重置凭证
#登录凭据过期时，无法进入游戏的提示
tokenIsExpired: |-
  登录凭证已过期，请打开网页
  {url}
  重置凭证
#玩家名中含有不允许的字符时，无法进入游戏的提示
nameIsValid: |-
  玩家名中含有不允许的字符，请打开网页
  {url}
  修改玩家名或联系管理员
#插件web端网址，请自行修改为自己的网址
tokenManageUrl: 'http://localhost:9000/'
#数据库配置
databaseConfig:
  type: SQLITE   #SQLITE或MYSQL，表示是使用SQLite作为数据库还是使用MySQL数据库（注意使用MySQL时帐号需要有创建表权限）
  #type为SQLITE时以下3个设置不需要设置
  #MySQL连接地址，localhost：MySQL服务器地址，3306：MySQL端口，database：数据库名，其他内容一般不修改
  mysqlUri: jdbc:mysql://localhost:3306/database?autoReconnect=true&useSSL=false&characterEncoding=utf-8&useUnicode=true
  mysqlUser: root   #MySQL用户名，注意需要有创建数据表权限
  mysqlPassword: root   #MySQL用户密码
webPort: 9000      #玩家登录系统的web端口，1024以上（1024以下需要root权限），不能使用已经被占用的端口
openRegister: false    #是否开放注册，开放注册后玩家可以自行在web端注册帐号并进入游戏
openChangePlayername: false     #是否允许修改玩家名，允许修改后玩家可以自行在web端修改名字
userNameRegexp: ^[a-zA-Z0-9\u4e00-\u9fa5]+$    #玩家名允许的规则，正则表达式，默认允许大小写英文字母、数字、中文，如果只允许大小写字母和数字，可以修改为^[a-zA-Z0-9]+$，对正则表达式不熟悉的不推荐修改这个配置
```
关于mssql数据库配置：
mysql的uri配置为jdbc:mysql://`localhost`:`3306`/`database`?autoReconnect=true&useSSL=false&characterEncoding=utf-8&useUnicode=true这样的格式
其中，有3处需要修改，分别是`localhost`（数据地址，本机填localhost或127.0.0.1）、`3306`（数据库端口，mysql数据库默认端口为3306）和`database`（数据库名，请修改为自己的数据库名）



#### 其他
1. 玩家登录时用户名或密码错误超过5次后10分钟内无法登录。
2. 玩家注册后10分钟内无法注册。
3. 玩家登录后10分钟内无需重新登录。
4. 如果需要修改web端界面的，可以自行修改，然后将修改后的文件放入插件目录下的web目录中即可，但是请遵循api规则。
5. 假如您服务器的ip地址为1.1.1.1，web端口设置为9000，那么默认情况下使用`http://1.1.1.1:9000`和`http://1.1.1.1:9000/bungeewebauth/`都可以访问，并且其他资源都在`http://1.1.1.1:9000/bungeewebauth/`下，您可以非常方便的将web端地址映射为您服务器网页的一个二级目录下或者二级域名下。
6. 如需使用https，或者隐藏端口，请使用nginx、apache等软件的转发功能。
7. web端登录时，用户名和密码均区分大小写。