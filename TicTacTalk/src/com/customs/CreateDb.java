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
            // Oracle JDBC ����̹� �ε�
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // �����ͺ��̽� ����
            conn = DriverManager.getConnection(url, props);
            // Statement ��ü ����
            stmt = conn.createStatement();

            // ���̺� ���� SQL ����
            String createTableSQL = "CREATE TABLE TTTDB (" +
                                    "id VARCHAR2(50) PRIMARY KEY, " +
                                    "password VARCHAR2(50), " +
                                    "win INT DEFAULT 0, " +
                                    "lose INT DEFAULT 0, " +
                                    "draw INT DEFAULT 0, " +
                                    "rating INT DEFAULT 1000" +
                                    ")";

            // ���̺� ���� ����
            stmt.executeUpdate(createTableSQL);
            System.out.println("���̺� TTTDB�� ���������� �����Ǿ����ϴ�.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // �ڿ� ��ȯ
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
