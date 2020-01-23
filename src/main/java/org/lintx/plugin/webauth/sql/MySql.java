package org.lintx.plugin.webauth.sql;

import org.lintx.plugin.webauth.Config;

import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class MySql implements SqlInterface {
    private static String db_url;
    private static String username;
    private static String password;

//    private static LinkedList<Connection> linkedlist = new LinkedList<>();
    private static LinkedList<ConnectionPool> pools = new LinkedList<>();
    //最小连接数量
    private static int jdbcConnectionInitSize = 10;

    //当前最大连接数量=max*jdbcConnectionInitSize
    private static int max = 1;

    private final long timeout;

    public MySql(String uri,String username,String password,long timeout){
        MySql.db_url = uri;
        MySql.username = username;
        MySql.password = password;
        this.timeout = timeout;
        load();
    }

    public void close(){
        linkedListClear();
    }

    private void linkedListClear(){
        try {
            final ConnectionPool pool = pools.removeFirst();
            if (pool!=null) {
                pool.close = true;
                pool.connection.close();
                linkedListClear();
            }
        } catch (Exception e) {
            if (Config.getInstance().getDatabaseConfig().isPrintError()){
                e.printStackTrace();
            }
        }
    }

    public Connection getSQLConnection() {
        if (pools.size() == 0 && max <= 5 * jdbcConnectionInitSize) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                for (int i = 0; i < jdbcConnectionInitSize; i++) {
                    Connection conn = DriverManager.getConnection(db_url, username, password);
                    ConnectionPool pool = new ConnectionPool(conn,timeout);
                    pool.connection = (Connection) Proxy.newProxyInstance(
                            pool.connection.getClass().getClassLoader(),
                            pool.connection.getClass().getInterfaces(), (proxy, method, args) -> {
                                if (!method.getName().equalsIgnoreCase("close") || pool.close) {
                                    if (method.getName().equalsIgnoreCase("close") && pool.close){
                                        max--;
                                    }
                                    return method.invoke(pool.connection, args);
                                } else {
                                    pool.update();
                                    pools.add(pool);
                                    return null;
                                }
                            }
                    );

                    pools.add(pool);
                    max++;
                }
            }
            catch (Exception e){
                if (Config.getInstance().getDatabaseConfig().isPrintError()){
                    e.printStackTrace();
                }
            }
        }
        if (pools.size() > 0) {
            final ConnectionPool pool = pools.removeFirst();
            if (!pool.check()){
                pool.close = true;
                try {
                    pool.connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return getSQLConnection();
            }

            return pool.connection;
        }
        return null;
    }



    private void load() {
        Connection connection = getSQLConnection();
        if (connection==null){
            return;
        }
        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTable1 = "CREATE TABLE IF NOT EXISTS `webauth_player` (" +
                    "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                    "`username` varchar(64) NOT NULL," +
                    "`password` varchar(64) NOT NULL," +
                    "`slat` varchar(64) NOT NULL," +
                    "`player_name` varchar(16) NOT NULL," +
                    "`uuid` varchar(64) NOT NULL," +
                    "`token` varchar(64) NOT NULL," +
                    "`token_time` varchar(20) NOT NULL" +
                    ");";
            s.executeUpdate(SQLiteCreateTable1);
//            String SQLiteCreateTable2 = "CREATE TABLE IF NOT EXISTS `player_history` (" +
//                    "`player_id` INTEGER," +
//                    "`name` varchar(64) NOT NULL" +
//                    ");";
//            s.executeUpdate(SQLiteCreateTable2);
            s.close();
        } catch (SQLException e) {
            if (Config.getInstance().getDatabaseConfig().isPrintError()){
                e.printStackTrace();
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            if (Config.getInstance().getDatabaseConfig().isPrintError()){
                e.printStackTrace();
            }
        }
    }

    static class ConnectionPool{
        Connection connection;
        LocalDateTime lastTime;
        final long timeout;
        boolean close = false;
        ConnectionPool(Connection connection,long timeout){
            this.connection = connection;
            this.timeout = timeout;
            update();
        }

        void update(){
            lastTime = LocalDateTime.now();
        }

        boolean check(){
            Duration duration = Duration.between(LocalDateTime.now(),lastTime);
            return duration.getSeconds() > timeout;
        }
    }
}
