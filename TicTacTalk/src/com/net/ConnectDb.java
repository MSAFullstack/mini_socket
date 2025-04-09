package com.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.tictactalk.MainFrame;

public class ConnectDb {
    static String sql = "";
    static String url = "jdbc:oracle:thin:@localhost:1521:xe";
    static Connection conn = null;
    static Statement stmt = null;
    static ResultSet rs = null;

    public static Map<String, List<String>> member = new HashMap<>();

    public static void connectDb() {
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
                member.put(rs.getString("id"), list);
            }
            System.out.println("[DB 로딩 완료] " + member);
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
    	connectDb();
        if (member.containsKey(id)) {
            List<String> list = member.get(id);
            if(list.get(0).equals(password)) {
            	System.out.println("login success"+id);
            	return true;
            }else {
            	System.out.println("password inconsistency");
            }
        }else {
        	System.out.println("id does not exist");
        }
        return false;
    }

    public static void signup(String id, String password) {
        Properties props = new Properties();
        props.setProperty("user", "scott");
        props.setProperty("password", "tiger");

        if (member.containsKey(id)) {
            System.out.println("아이디 중복");
        } else {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection(url, props);
                stmt = conn.createStatement();
                sql = "insert into TTTDB (id, password, win, draw, lose, rating) values ('" + id + "','" + password + "', 0, 0, 0, 1000)";
                stmt.executeUpdate(sql);
                System.out.println("회원가입 성공: " + id);
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
        new MainFrame();
    }
}