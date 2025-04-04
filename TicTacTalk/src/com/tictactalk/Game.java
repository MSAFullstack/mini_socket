package com.tictactalk;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JFrame {
	
	public JPanel panel = new JPanel();
	
	public Game() {
		this.setTitle("TicTacTalk");
		this.setSize(852,756);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		panel.setBackground(Color.decode("#4ED59B"));

		
		
		
		
		this.add(panel);
		this.setVisible(true);
	}
	

}
