package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.driver.OracleDriver;

public class DBConnection {

    private String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
    private Properties props;
    private OracleDriver driver;

    public DBConnection() {
        props = new Properties();
        props.setProperty("user", "scott");
        props.setProperty("password", "tiger");
        driver = new OracleDriver();
    }

    // 'user' ���̺��� �����ϴ��� Ȯ���ϴ� �޼���
    public boolean doesUserTableExist() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();

            // 'USER' ���̺��� �����ϴ��� Ȯ���ϴ� ���� (��ҹ��� ����, ����� ��� �� ū����ǥ�� ���ξ� ��)
            String checkTableSql = "SELECT COUNT(*) FROM user_tables WHERE table_name = 'USER'";
            rs = stmt.executeQuery(checkTableSql);

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // ���̺� ����
    public void createUserTable() throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        String createTableSql = "CREATE TABLE \"USER\" ("
                              + "userId VARCHAR2(50), "
                              + "userpw VARCHAR2(50))";

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();
            stmt.executeUpdate(createTableSql);
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    
    //�α��� ����
    public boolean Login(String userId, String password) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();

            // ID�� PW�� ��ġ�ϴ��� Ȯ���ϴ� ����
            String sql = "SELECT COUNT(*) FROM \"USER\" WHERE userId = '" + userId + "' AND userpw = '" + password + "'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1) > 0; // 1�� �̻��� ���� �����ϸ� �α��� ����
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    //���̵� ����
    public boolean doesUserIdExist(String userId) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();

            
            String sql = "SELECT COUNT(*) FROM \"USER\" WHERE userId = '" + userId + "'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
            return false; 
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    
    
    //ȸ�� ���� ����
    public void saveUser(String userId, String password) throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        String insertSql = "INSERT INTO \"USER\" (userId, userpw) VALUES ('" + userId + "', '" + password + "')";

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();
            stmt.executeUpdate(insertSql);
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    
    
    
    
    
}
    


    
