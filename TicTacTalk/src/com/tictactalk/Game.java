package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.customs.Round;
import com.net.Client;
import com.net.ConnectDb;

public class Game extends JPanel implements ActionListener {
    private JButton[] buttons = new JButton[9];
    private JTextArea chatArea;
    private JTextArea chatInput;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private boolean isMyTurn;
    private boolean gameEnded = false;
    private JPanel playerInfoPanel;
    
    static List<String> enemyInfo;
    static List<String> playerInfo;
    int pwin, pdraw, plose, prating, ptotal, prate;
    int ewin, edraw, elose, erating, etotal, erate;
    
    public Game() {
    	ConnectDb cdb = new ConnectDb();
    	cdb.connectDb();
    	playerInfo=cdb.map.get(Client.playerId);
    	enemyInfo=cdb.map.get(Client.enemyId);
    	//DB에서 정보 불러서 모든 값 저장해놓기
    	if(playerInfo !=null) {
    		pwin = Integer.parseInt(playerInfo.get(1));
    		pdraw = Integer.parseInt(playerInfo.get(2));
    		plose = Integer.parseInt(playerInfo.get(3));
    		prating = Integer.parseInt(playerInfo.get(4));
    		ptotal = pwin+pdraw+plose;
    		prate = ptotal > 0 ? (int)(((double)pwin/ptotal)*100) : 0;
    	}
    	if(enemyInfo !=null) {
    		ewin = Integer.parseInt(enemyInfo.get(1));
    		edraw = Integer.parseInt(enemyInfo.get(2));
    		elose = Integer.parseInt(enemyInfo.get(3));
    		erating = Integer.parseInt(enemyInfo.get(4));
    		etotal = ewin+edraw+elose;
    		erate = etotal > 0 ? (int)(((double)ewin/etotal)*100) : 0;
    	}
        setLayout(new BorderLayout());
        setBackground(Color.decode("#4ED59B"));
        
        JLabel titleLabel = new JLabel("TIC TAC TALK", JLabel.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 40));
        
        add(titleLabel, BorderLayout.NORTH);
        
        //X/O 역할 분담
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.decode("#4ED59B"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 게임판 (전체의 2/3)
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3,3,5,5));
        Font font = new Font("Arial", Font.BOLD, 40);
        for (int i = 0; i < 9; i++) {
            buttons[i]=new JButton("");
            final int index = i;
            buttons[i].setFont(font);
            buttons[i].addActionListener(e -> handleMove(index));
            boardPanel.add(buttons[i]);
        }

        // 플레이어 정보
        //connectDb를 한 다음에 map에서 id에 맞는 정보 불러오기
        JPanel userPanel = new JPanel();
        userPanel.setBackground(Color.decode("#4ED59B"));
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        Round.RoundPanel user1Panel = new Round.RoundPanel(Color.decode("#FFFACD"));
        user1Panel.setLayout(new BoxLayout(user1Panel, BoxLayout.Y_AXIS));

        JLabel user1Score = new JLabel(Integer.toString(prating), JLabel.CENTER);
        user1Score.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel user1Pic = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.GRAY);
                int diameter = 60;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                g.fillOval(x, y, diameter, diameter);
            }
        };
        user1Pic.setPreferredSize(new Dimension(80, 80));
        user1Pic.setOpaque(false);
        user1Pic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1Name = new JLabel(Client.playerId, JLabel.CENTER);

        user1Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1Record = new JLabel(pwin+"승 "+pdraw+"무 "+plose+"패", JLabel.CENTER);
        user1Record.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1WinRate = new JLabel(Integer.toString(prate), JLabel.CENTER);
        user1WinRate.setAlignmentX(Component.CENTER_ALIGNMENT);

        user1Panel.add(Box.createVerticalStrut(5));
        user1Panel.add(user1Score);
        user1Panel.add(user1Pic);
        user1Panel.add(user1Name);
        user1Panel.add(user1Record);
        user1Panel.add(user1WinRate);


        Round.RoundPanel user2Panel = new Round.RoundPanel(Color.decode("#FFFACD"));
        user2Panel.setLayout(new BoxLayout(user2Panel, BoxLayout.Y_AXIS));


        JLabel user2Score = new JLabel(Integer.toString(erating), JLabel.CENTER);
        user2Score.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel user2Pic = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.GRAY);
                int diameter = 60;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                g.fillOval(x, y, diameter, diameter);
            }
        };
        user2Pic.setPreferredSize(new Dimension(80, 80));
        user2Pic.setOpaque(false);
        user2Pic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2Name = new JLabel(Client.enemyId, JLabel.CENTER);
        user2Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2Record = new JLabel(ewin+"승 "+edraw+"무 "+elose+"패", JLabel.CENTER);
        user2Record.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2WinRate = new JLabel(Integer.toString(erate), JLabel.CENTER);
        user2WinRate.setAlignmentX(Component.CENTER_ALIGNMENT);

        user2Panel.add(Box.createVerticalStrut(5));
        user2Panel.add(user2Score);
        user2Panel.add(user2Pic);
        user2Panel.add(user2Name);
        user2Panel.add(user2Record);
        user2Panel.add(user2WinRate);

        profilePanel.add(user1Panel);
        profilePanel.add(Box.createRigidArea(new Dimension(30, 0)));
        profilePanel.add(user2Panel);

        // 중간: 채팅창
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        //채팅 내용창
        chatArea = new JTextArea(6,20);
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        
        //채팅 작성창
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chatInput = new JTextArea(2,20);
        chatInput.setLineWrap(true);
        chatInput.setWrapStyleWord(true);
        chatInput.setFont(new Font("Arial", Font.PLAIN, 14));
        chatInput.setEditable(true);
        JScrollPane inputScroll = new JScrollPane(chatInput);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        sendButton = new JButton("전송");
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        
        userPanel.add(profilePanel);
        userPanel.add(chatPanel);

        boardPanel.setPreferredSize(new Dimension(600, 0));
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        userPanel.setPreferredSize(new Dimension(252, 0));
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        centerPanel.add(userPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(Color.decode("#4ED59B"));
        bottomPanel.setPreferredSize(new Dimension(852, 80));

        JLabel timerLabel = new JLabel("남은 시간 : 60초", JLabel.CENTER);
        timerLabel.setPreferredSize(new Dimension(200, 80));

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(timerLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
   
        setVisible(true);
        
        //게임판과는 별도인 채팅창을 위한 리스너 코드
        sendButton.addActionListener(e-> {
        	String message = chatInput.getText().trim();
        	System.out.println("[채팅 전송 클릭됨 "+message);
        	if(!message.isEmpty()) {
        		Client.sendMessage(message);
        		chatInput.setText("");
        	}
        });
        
        Client.setGame(this);
        setMyTurn(true);
    }
    
    private void handleMove(int index) {
    	if (!isMyTurn || !buttons[index].getText().equals("")||gameEnded) return;
    	
    	//디버그 코드
    	System.out.println("[클릭됨] index: "+index);
    	buttons[index].setText("X");
    	isMyTurn = false;
    	checkGameState();
    	if(Client.bw == null) {
    		System.out.println("[오류] bw가 null입니다.");
    	}
    	try {
    		Client.bw.write("MOVE "+index);
    		Client.bw.newLine();
    		Client.bw.flush();
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    }
   
    
    public void markOpponentMove(int index) {
    	if(index >= 0 && index < 9 && buttons[index].getText().equals("")) {
    		buttons[index].setText("O");
    		isMyTurn=true;
    		checkGameState();
    	}
    }

//    public void updatePlayerPanels() {
//    	playerInfoPanel.removeAll();
//    	playerInfoPanel.add(createPlayerPanel(Client.playerId));
//    	playerInfoPanel.add(createPlayerPanel(Client.enemyId));
//    	playerInfoPanel.revalidate();
//    	playerInfoPanel.repaint();
//    }
    
//    private JPanel createPlayerPanel(String playerId) {
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBackground(Color.decode("#FFE57F"));
//        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//
//        JLabel ratingLabel = new JLabel("9999", JLabel.CENTER); // 기본값
////        JLabel profileLabel = new JLabel(new ImageIcon("img/default.png")); //프로필(추후 변경)
//        JLabel nameLabel = new JLabel(playerId, JLabel.CENTER);
//
//        JLabel recordLabel = new JLabel("전적 불러오는 중", JLabel.CENTER);
//        JLabel winRateLabel = new JLabel("승률 계산중", JLabel.CENTER);
//
//        if (playerId!=null&&!playerId.equals("상대")) {
//            List<String> userInfo = ConnectDb.map.get(playerId);
//            if (userInfo != null) {
//                int win = Integer.parseInt(userInfo.get(1));
//                int draw = Integer.parseInt(userInfo.get(2));
//                int lose = Integer.parseInt(userInfo.get(3));
//                int rating = Integer.parseInt(userInfo.get(4));
//                ratingLabel.setText(String.valueOf(rating));
//                int total = win + lose + draw;
//                int rate = total > 0 ? (int)(((double)win / total) * 100) : 0;
//                recordLabel.setText(win + "승 " + draw + "무 " + lose + "패");
//                winRateLabel.setText("승률 " + String.format("%03d", rate) + "%");
//                System.out.println("내 ID"+Client.playerId+" 상대 ID "+Client.enemyId+"상대 전적"+ConnectDb.map.get(Client.enemyId));
////                profileLabel.setIcon(new ImageIcon("img/profile_" + rating / 1000 + ".png"));
//            }else {
//            	recordLabel.setText("전적 없음");
//            	winRateLabel.setText("");
//            }
//        }
//
//        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
////        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        recordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        winRateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        panel.add(ratingLabel);
////        panel.add(profileLabel);
//        panel.add(nameLabel);
//        panel.add(recordLabel);
//        panel.add(winRateLabel);
//
//        return panel;
//    }

    public void setMyTurn(boolean turn) {
    	isMyTurn = turn;
    }
    private void checkGameState() {
    	String[][] lines = new String[8][3];
    	for (int i = 0; i < 3; i++) {
    	            lines[i][0] = buttons[i * 3].getText();
    	            lines[i][1] = buttons[i * 3 + 1].getText();
    	            lines[i][2] = buttons[i * 3 + 2].getText();
    	            lines[i + 3][0] = buttons[i].getText();
    	            lines[i + 3][1] = buttons[i + 3].getText();
    	            lines[i + 3][2] = buttons[i + 6].getText();
    	        }
    	        lines[6][0] = buttons[0].getText(); lines[6][1] = buttons[4].getText(); lines[6][2] = buttons[8].getText();
    	        lines[7][0] = buttons[2].getText(); lines[7][1] = buttons[4].getText(); lines[7][2] = buttons[6].getText();

    	        for (String[] line : lines) {
    	            if (line[0].equals("X") && line[1].equals("X") && line[2].equals("X")) {
    	                gameEnded = true;
    	                JOptionPane.showMessageDialog(this, "You Win!");
    	                return;
    	            } else if (line[0].equals("O") && line[1].equals("O") && line[2].equals("O")) {
    	                gameEnded = true;
    	                JOptionPane.showMessageDialog(this, "You Lose!");
    	                return;
    	            }
    	        }

    	        boolean draw = true;
    	        for (JButton btn : buttons) {
    	            if (btn.getText().equals("")) {
    	                draw = false;
    	                break;
    	            }
    	        }
    	        if (draw) {
    	            gameEnded = true;
    	            JOptionPane.showMessageDialog(this, "Draw!");
    	        }
    }

//    private void sendMessage() {
//        String msg = chatInput.getText();
//        if(msg.isEmpty()) return;
//        try {
//			Client.bw.write(msg);
//			Client.bw.newLine();
//			Client.bw.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        chatInput.setText("");
//    }

    public void displayMessage(String msg) {
        chatArea.setText(chatArea.getText()+msg+"\n");;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// 오버라이드 되어있음
	}
	
//	public void onGameEnd(String result) {
//		//Client에서 결과 도착시 오버라이드
//	}
}