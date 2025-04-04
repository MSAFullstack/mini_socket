package com.customs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class member {

	public static void main(String[] args) {
		ArrayList<Integer> list=new ArrayList<>();
		String sql="select * from tttdb";
		
//		String sql="desc emp";
		String url="jdbc:oracle:thin:@172.30.1.71:1521:xe";
		Properties props=new Properties();
		props.setProperty("user", "scott");
		props.setProperty("password", "tiger");

		Connection conn=null;
		Statement stmt=null;
		java.sql.ResultSet rs=null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn=DriverManager.getConnection(url, props);
			stmt=conn.createStatement();
			rs=stmt.executeQuery(sql);
			System.out.println("");
			while(rs.next()) {
				
//				list.add(Integer.parseInt(rs.getObject(1).toString()));
				System.out.print(rs.getObject(1)+"\t");
				System.out.print(rs.getObject(2)+"\t");
				System.out.print(rs.getObject(3)+"\t");
				System.out.println(rs.getObject(4));
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null)rs.close();
				if(stmt!=null)stmt.close();
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
