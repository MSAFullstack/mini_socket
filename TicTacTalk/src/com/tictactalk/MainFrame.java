package com.tictactalk;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	public static CardLayout cardLayout = new CardLayout();
	public static JPanel cardPanel = new JPanel(cardLayout);
	
	public MainFrame() {
		setTitle("TicTacTalk");
		setSize(852, 756);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		//CardLayout으로 Index 화면, Game 화면 전환
		cardPanel.add(new Index(), "Index");
		cardPanel.add(new JPanel(), "Game");

		add(cardPanel);
		setVisible(true);
	}

	public static void switchTo(String name) {
		cardLayout.show(cardPanel, name);
	}

	public static void main(String[] args) {
		// MainFrame 역할 수행
		new MainFrame();

	}

}