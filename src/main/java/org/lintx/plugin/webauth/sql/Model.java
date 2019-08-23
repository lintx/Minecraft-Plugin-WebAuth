package org.lintx.plugin.webauth.sql;

import org.lintx.plugin.webauth.models.PlayerModel;
import org.lintx.plugin.webauth.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Model {
    private final SqlInterface sql;
    public Model(SqlInterface sqlInterface){
        this.sql = sqlInterface;
    }

    private PlayerModel playerModelWithResultSet(ResultSet rs){
        try {
            PlayerModel model = new PlayerModel();
            model.setId(rs.getInt("id"));
            model.setUsername(rs.getString("username"));
            model.setPassword(rs.getString("password"));
            model.setSlat(rs.getString("slat"));
            model.setName(rs.getString("player_name"));
            model.setUuid(UUID.fromString(rs.getString("uuid")));
            model.setToken(rs.getString("token"));
            model.setToken_timeString(rs.getString("token_time"));
            return model;
        } catch (SQLException ignored) {

        }
        return null;
    }

    public PlayerModel getPlayerWithId(int id){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = sql.getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM webauth_player WHERE id=? LIMIT 1");
            ps.setInt(1,id);

            rs = ps.executeQuery();
            while(rs.next()){
                return playerModelWithResultSet(rs);
            }
        } catch (SQLException ignored) {

        } finally {
            release(conn,ps,rs);
        }
        return null;
    }

    public PlayerModel getPlayerWithToken(String token){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = sql.getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM webauth_player WHERE token=? LIMIT 1");
            ps.setString(1, Utils.sha1(token));

            rs = ps.executeQuery();
            while(rs.next()){
                return playerModelWithResultSet(rs);
            }
        } catch (SQLException ignored) {

        } finally {
            release(conn,ps,rs);
        }
        return null;
    }

    public PlayerModel getPlayerWithUsername(String username){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = sql.getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM webauth_player WHERE username=? LIMIT 1");
            ps.setString(1,username);

            rs = ps.executeQuery();
            while(rs.next()){
                return playerModelWithResultSet(rs);
            }
        } catch (SQLException ignored) {

        } finally {
            release(conn,ps,rs);
        }
        return null;
    }

    public PlayerModel getPlayerWithPlayerName(String name){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = sql.getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM webauth_player WHERE player_name=? LIMIT 1");
            ps.setString(1,name);

            rs = ps.executeQuery();
            while(rs.next()){
                return playerModelWithResultSet(rs);
            }
        } catch (SQLException ignored) {

        } finally {
            release(conn,ps,rs);
        }
        return null;
    }

    public boolean updatePlayer(PlayerModel model){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = sql.getSQLConnection();
            ps = conn.prepareStatement("update webauth_player set password=?,uuid=?,player_name=?,token=?,token_time=? where id=?");
            ps.setString(1,model.getPassword());
            ps.setString(2,model.getUuid().toString());
            ps.setString(3,model.getName());
            ps.setString(4,model.getToken());
            ps.setString(5,model.getToken_timeString());
            ps.setInt(6,model.getId());

            int r =  ps.executeUpdate();
            return r>0;
        } catch (SQLException ignored) {

        } finally {
            release(conn,ps,null);
        }
        return false;
    }

    public boolean checkPlayerUsername(String username){
        PlayerModel model = getPlayerWithUsername(username);
        if (model!=null) return false;
        return checkPlayerName(username);
    }

    public boolean checkPlayerName(String name){
        PlayerModel model = getPlayerWithPlayerName(name);
        return model==null;
    }

    public boolean insertPlayer(PlayerModel model){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = sql.getSQLConnection();
            ps = conn.prepareStatement("insert into webauth_player (username,password,slat,uuid,player_name,token,token_time) values (?,?,?,?,?,?,?)");
            ps.setString(1,model.getUsername());
            ps.setString(2,model.getPassword());
            ps.setString(3,model.getSlat());
            ps.setString(4,model.getUuid().toString());
            ps.setString(5,model.getName());
            ps.setString(6,model.getToken());
            ps.setString(7,model.getToken_timeString());

            int r =  ps.executeUpdate();
            return r>0;
        } catch (SQLException ignored) {

        } finally {
            release(conn,ps,null);
        }
        return false;
    }

    private void release(Connection conn, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ignored) {

            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception ignored) {

            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignored) {

            }
        }
    }
}
