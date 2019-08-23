package org.lintx.plugin.webauth.sql;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLite implements SqlInterface {
    private Connection connection;
    private String dbname;
    private File folder;
    public SQLite(File folder, String dbname){
        this.folder = folder;
        this.dbname = dbname;
        load();
    }

    public Connection getSQLConnection() {
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }

            File dataFolder = new File(folder, dbname+".db");

            Class.forName("org.sqlite.JDBC");
            Properties properties = new Properties();
            properties.setProperty("characterEncoding", "UTF-8");
            properties.setProperty("encoding", "\"UTF-8\"");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.toString());
            return connection;
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void load() {
        connection = getSQLConnection();
        if (connection==null){
            return;
        }
        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTable1 = "CREATE TABLE IF NOT EXISTS \"webauth_player\" (" +
                    "\"id\" INTEGER PRIMARY KEY," +
                    "\"username\" varchar(64) NOT NULL," +
                    "\"password\" varchar(64) NOT NULL," +
                    "\"slat\" varchar(64) NOT NULL," +
                    "\"player_name\" varchar(16) NOT NULL," +
                    "\"uuid\" varchar(64) NOT NULL," +
                    "\"token\" varchar(64) NOT NULL," +
                    "\"token_time\" varchar(20) NOT NULL" +
                    ");";
            s.executeUpdate(SQLiteCreateTable1);
//            String SQLiteCreateTable2 = "CREATE TABLE IF NOT EXISTS \"player_history\" (" +
//                    "\"player_id\" INTEGER," +
//                    "\"name\" varchar(64) NOT NULL" +
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