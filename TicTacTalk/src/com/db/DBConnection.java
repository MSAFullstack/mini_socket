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

    // 'user' 테이블이 존재하는지 확인하는 메서드
    public boolean doesUserTableExist() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();

            // 'USER' 테이블이 존재하는지 확인하는 쿼리 (대소문자 구분, 예약어 사용 시 큰따옴표로 감싸야 함)
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

    // 테이블 생성
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
    
    
    //로그인 검증
    public boolean Login(String userId, String password) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, props);
            stmt = conn.createStatement();

            // ID와 PW가 일치하는지 확인하는 쿼리
            String sql = "SELECT COUNT(*) FROM \"USER\" WHERE userId = '" + userId + "' AND userpw = '" + password + "'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1) > 0; // 1개 이상의 행이 존재하면 로그인 성공
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    //아이디 검증
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
    
    
    
    //회원 정보 저장
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
    


    
