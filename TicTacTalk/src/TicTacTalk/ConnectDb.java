//패키지명 수정예정
package TicTacTalk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConnectDb {
	String sql="";
	//localhost->서버용 PC의 IP 주소로 지정
	private static final String url="jdbc:oracle:thin:@localhost:1521:xe";
	private static final String user="scott";
	private static final String password="tiger";
	
	private Connection conn=null;
	Statement stmt=null;
	ResultSet rs=null;
	//DB에서 Map에 저장하여 여러 클래스에서 호출
	//또는 입출력 관리가 쉬운 다른 자료구조로 바꾸기
	Map<String, List> map=new HashMap<>();
	List list=new ArrayList();
	
	public void getconnect() {
		Properties props=new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn=DriverManager.getConnection(url,props);
			stmt=conn.createStatement();
			sql="select id,pw,win,lose,draw,rating from tttdb";
			rs=stmt.executeQuery(sql);
			while(rs.next()) {
				list.add(rs.getString(2));
				list.add(rs.getString(3));
				list.add(rs.getString(4));
				list.add(rs.getString(5));
				list.add(rs.getString(6));
				map.put(rs.getString(1), list);
			}
			System.out.println(map);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
	public void signup(String id, String password) {
		Properties props=new Properties();
		props.setProperty("user", user);
		props.setProperty("password", ConnectDb.password);

		if(map.containsKey(id)) {
			//id 중복 확인 팝업
			System.out.println("아이디 중복");
		} else {
			//id 중복 아니면 DB에 insert
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn=DriverManager.getConnection(url,props);
				stmt=conn.createStatement();
				rs=stmt.executeQuery(sql);
				sql="insert into tttdb (id,pw) values ('"+id+"',"+"'"+password+"')";
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
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
	public static void main(String[] args) {
		ConnectDb cd=new ConnectDb();
		cd.getconnect();
	}

}
