package com.tictactalk;

import java.sql.SQLException;

import com.db.*;


public class Main {

	public static void main(String[] args) {
		
        DBConnection dbConnection = new DBConnection();
        try {
			if (!dbConnection.doesUserTableExist()) {
				dbConnection.createUserTable();
			    System.out.println("���̺� ���� ����");
			}else{
				System.out.println("���̺��� �̹� �����մϴ�.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���� ȭ���� �ҷ�����
		Index index = new Index();
//		Game game = new Game();

	}

}
