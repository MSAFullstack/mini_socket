package com.customs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class CreateDb {
    static String url = "jdbc:oracle:thin:@localhost:1521:xe";
    static Connection conn = null;
    static Statement stmt = null;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("user", "scott");
        props.setProperty("password", "tiger");

        try {
            // Oracle JDBC 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // 데이터베이스 연결
            conn = DriverManager.getConnection(url, props);
            // Statement 객체 생성
            stmt = conn.createStatement();

            // 테이블 생성 SQL 쿼리
            String createTableSQL = "CREATE TABLE TTTDB (" +
                                    "id VARCHAR2(50) PRIMARY KEY, " +
                                    "password VARCHAR2(50), " +
                                    "win INT DEFAULT 0, " +
                                    "lose INT DEFAULT 0, " +
                                    "draw INT DEFAULT 0, " +
                                    "rating INT DEFAULT 1000" +
                                    ")";

            // 테이블 생성 실행
            stmt.executeUpdate(createTableSQL);
            System.out.println("테이블 TTTDB가 성공적으로 생성되었습니다.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 자원 반환
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
