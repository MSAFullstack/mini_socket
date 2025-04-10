package com.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

//DB연결 클래스
public class ConnectDb {
    static String sql = "";
    static String url = "jdbc:oracle:thin:@172.30.1.2:1521:xe";
    static Connection conn = null;
    static Statement stmt = null;
    static ResultSet rs = null;

    public static Map<String, List<String>> map = new HashMap<>();

    public void connectDb() {
        Properties props = new Properties();
        props.setProperty("user", "scott");
        props.setProperty("password", "tiger");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();
            sql = "select id,password,win,draw,lose,rating from TTTDB";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                List<String> list = new ArrayList<>();
                list.add(rs.getString("password"));
                list.add(rs.getString("win"));
                list.add(rs.getString("draw"));
                list.add(rs.getString("lose"));
                list.add(rs.getString("rating"));
                map.put(rs.getString("id"), list);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean login(String id, String password) {
        if (map.containsKey(id)) {
            List<String> list = map.get(id);
            return list.get(0).equals(password);
        }
        return false;
    }

    public static void signup(String id, String password) {
        Properties props = new Properties();
        props.setProperty("user", "scott");
        props.setProperty("password", "tiger");

        if (map.containsKey(id)) {
            System.out.println("아이디 중복");
        } else {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection(url, props);
                stmt = conn.createStatement();
				sql = "insert into TTTDB (id, password, win, draw, lose, rating) values ('" + id + "','" + password + "', 0, 0, 0, 1000)";
                stmt.executeUpdate(sql);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ConnectDb cd = new ConnectDb();
        cd.connectDb();
    }
}