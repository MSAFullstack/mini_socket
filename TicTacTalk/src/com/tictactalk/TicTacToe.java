package com.tictactalk;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class TicTacToe extends JFrame implements Runnable, ActionListener {
	Button[] btns;
	String[] answer;
	String s="";
	public TicTacToe() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		Panel cenP = new Panel(new GridLayout(3, 3));
		btns = new Button[9];
		int cnt=0;
			
		for(int i=0; i<btns.length; i++) {
			btns[i] = new Button(i+"");
			btns[i].setForeground(Color.white);
			cenP.add(btns[i]);
			btns[i].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					s+=String.valueOf(e.getActionCommand());
					String[] answer = s.split("");
					
					if(s.contains("0") && s.contains("1") &&  s.contains("2")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}
					if(s.contains("3") && s.contains("4") &&  s.contains("5")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}
					if(s.contains("6") && s.contains("7") &&  s.contains("8")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}// 가로 게임 종료
					if(s.contains("0") && s.contains("3") &&  s.contains("6")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}
					if(s.contains("1") && s.contains("4") &&  s.contains("7")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}
					if(s.contains("2") && s.contains("5") &&  s.contains("8")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}// 세로 게임 종료
					if(s.contains("0") && s.contains("4") &&  s.contains("8")){
						System.out.println("게임 끝!");
						setEnabled(false);
					}
					if(s.contains("2") && s.contains("4") &&  s.contains("6")){
						
						System.out.println("게임 끝!");
						setEnabled(false);
					}
					
				}
			});
		}
		
		
		add(cenP,BorderLayout.CENTER);
		setBounds(800, 100, 550, 550);
		setVisible(true);
//		String[] answer = s.split("");
//		if(answer.equals("0") && answer.equals("1") && answer.equals("2")){
//			System.out.println("게임 끝!");
//		}
		// int로 받고 
		 
	}
	
	
	
	
	public static void main(String[] args) {
		new TicTacToe();
		int[][] ttt = new int[3][3];

		for(int i =0; i<ttt.length; i++) {
			for(int j=0; j<ttt[i].length; j++) {
				System.out.print(ttt[i][j]+" ");
			}System.out.println();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
 
	 

}
