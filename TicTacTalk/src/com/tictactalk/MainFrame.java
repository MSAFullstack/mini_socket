package com.tictactalk;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.net.ConnectDb;

public class MainFrame extends JFrame {
	public static CardLayout cardLayout = new CardLayout();
	public static JPanel cardPanel = new JPanel(cardLayout);
	
	public MainFrame() {
		setTitle("TicTacTalk");
		setSize(852,756);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		cardPanel.add(new Index(), "Index");
		cardPanel.add(new JPanel(), "Game");
		
		add(cardPanel);
		setVisible(true);
	}
	
	public static void switchTo(String name) {
		cardLayout.show(cardPanel, name);
	}

	public static void main(String[] args) {
		//MainFrame ���� ����
		ConnectDb cd = new ConnectDb();
        cd.connectDb();
		new MainFrame();

	}

}