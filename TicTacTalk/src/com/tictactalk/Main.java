package com.tictactalk;

import java.sql.SQLException;

import com.db.*;


public class Main {

	public static void main(String[] args) {
		
        DBConnection dbConnection = new DBConnection();
        try {
			if (!dbConnection.doesUserTableExist()) {
				dbConnection.createUserTable();
			    System.out.println("테이블 생성 성공");
			}else{
				System.out.println("테이블이 이미 존재합니다.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//메인 화면을 불러오기
		Index index = new Index();
//		Game game = new Game();

	}

}
