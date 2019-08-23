package org.lintx.plugin.webauth.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

public class MySql implements SqlInterface {
    private static String db_url;
    private static String username;
    private static String password;

    private static LinkedList<Connection> linkedlist = new LinkedList<Connection>();
    //最小连接数量
    private static int jdbcConnectionInitSize = 10;

    //当前最大连接数量=max*jdbcConnectionInitSize
    private static int max = 1;

    //是否真的关闭连接
    private boolean onClose = false;

    public MySql(String uri,String username,String password){
        MySql.db_url = uri;
        MySql.username = username;
        MySql.password = password;
        load();
    }

    public void close(){
        linkedListClear();
    }

    private void linkedListClear(){
        onClose = true;
        try {
            final Connection conn = linkedlist.removeFirst();
            if (conn!=null) {
                conn.close();
                linkedListClear();
            }
        } catch (Exception ignored) {
        }
    }

    public Connection getSQLConnection() {
        if (linkedlist.size() == 0 && max <= 5) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                for (int i = 0; i < jdbcConnectionInitSize; i++) {
                    Connection conn = DriverManager.getConnection(db_url, username, password);
                    linkedlist.add(conn);
                }
                max++;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if (linkedlist.size() > 0) {
            final Connection conn1 = linkedlist.removeFirst();
            return (Connection) Proxy.newProxyInstance(
                    conn1.getClass().getClassLoader(),
                    conn1.getClass().getInterfaces(), new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (!method.getName().equalsIgnoreCase("close") || onClose) {
                                return method.invoke(conn1, args);
                            } else {
                                linkedlist.add(conn1);
                                return null;
                            }
                        }
                    }
            );
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
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException ignored) {

        }
    }
}
