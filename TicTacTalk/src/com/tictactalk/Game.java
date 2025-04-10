package com.tictactalk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.customs.Round;
import com.net.Client;
import com.net.ConnectDb;
import com.net.Server;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.REUtil;

public class Game extends JPanel implements ActionListener {
	private JButton[][] buttons = new JButton[3][3];
	private JTextArea chatArea;
	private JTextField chatInput;
	private JButton sendButton;
	private JLabel statusLabel;
	private JLabel timerLabel;
	private boolean isMyTurn;
	private JPanel playerInfoPanel;
	private String mySymbol;
	private String enemySymbol;
	private Timer cntTimer;
	private int timer = 60;
	static java.util.List<String> myInfo;
	private int myrating, mywins, mydraws, mylosses;
	private String myid = Client.playerId;

	static java.util.List<String> enemyInfo;
	private int enemyrating, enemywins, enemydraws, enemylosses;
	private String enemyid = Client.enemyID;

	public JDialog endGameDialog;

	public Game(boolean isMyTurn) {
		//서버에서 선후공 정해줌
		this.isMyTurn = isMyTurn;
		setLayout(new BorderLayout());
		setBackground(Color.decode("#4ED59B"));

		JLabel titleLabel = new JLabel("TIC TAC TALK", JLabel.CENTER);
		titleLabel.setFont(new Font("Inter", Font.BOLD, 40));
		this.add(titleLabel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.decode("#4ED59B"));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// X/O 역할 분담
		if (isMyTurn) {
			mySymbol = "X"; // 첫 번째 플레이어는 X
			enemySymbol = "O"; // 두 번째 플레이어는 O
		} else {
			mySymbol = "O";
			enemySymbol = "X";
		}

		// 게임판 (전체의 2/3)
		JPanel boardPanel = new JPanel(new GridLayout(3, 3));
		boardPanel.setPreferredSize(new Dimension(550, 550));
		Font font = new Font("Arial", Font.BOLD, 60);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				final int row = i;
				final int col = j;

				// 버튼 초기화
				buttons[i][j] = new JButton("");
				buttons[i][j].setFont(font);
				buttons[i][j].setFocusPainted(false);
				buttons[row][col].addActionListener(e -> {
					statusLabel.setText("차례를 지켜주세요");
					if (!this.isMyTurn || !buttons[row][col].getText().equals("")) {
						return; // 내 턴이 아니거나 이미 버튼에 수가 들어있으면 무시
					}

					// 서버로 내 수 보내기
					sendMove(row, col);
					handleLocalMove(row, col); // 버튼 클릭 후 로컬에서 상태 업데이트
				});
				boardPanel.add(buttons[i][j]);
			}
		}

		// 오른쪽 전체(플레이어 정보 + 채팅)
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.decode("#FFFFFF"));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setPreferredSize(new Dimension(280, 700));

		// 상단: 플레이어 전적 정보(초기 빈 패널)
		playerInfoPanel = new JPanel();
		playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.X_AXIS));
		playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// 중간: 채팅창
		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(260, 400));
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		chatInput = new JTextField();
		sendButton = new JButton("전송");

		sendButton.addActionListener(e -> sendMessage());
		chatInput.addActionListener(e -> sendMessage());

		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(chatInput, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);
		chatPanel.add(scrollPane, BorderLayout.CENTER);
		chatPanel.add(inputPanel, BorderLayout.SOUTH);

		// 상태, 시간 표시
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusLabel = new JLabel(isMyTurn ? "당신 차례입니다." : "상대 차례입니다.", JLabel.CENTER);
		timerLabel = new JLabel("남은 시간 : " + timer + "초", SwingConstants.RIGHT);
		timerLabel.setForeground(Color.RED);
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		statusPanel.add(timerLabel, BorderLayout.EAST);

		rightPanel.add(playerInfoPanel);
		rightPanel.add(Box.createVerticalStrut(10));
		rightPanel.add(chatPanel);
		rightPanel.add(Box.createVerticalStrut(10));

		centerPanel.add(boardPanel, BorderLayout.CENTER);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		centerPanel.add(rightPanel, BorderLayout.EAST);

		add(centerPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);

		startTimer();
	}
	//전체 게임 시간 60초 한정
	private void startTimer() {
		cntTimer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (timer > 0) {
					timer--;
					timerLabel.setText("남은 시간 : " + timer + "초");
				} else {
					// win,lose,draw 결과 없이 시간 끝나면 draw
					onGameEnd("draw");
					cntTimer.stop();
				}

			}
		});
		cntTimer.start();
	}
	//서버에 버튼 좌표 전송
	private void sendMove(int row, int col) {
		try {
			BufferedWriter bw = Client.bw;
			if (bw != null) {
				bw.write("move:" + row + ":" + col);
				bw.newLine();
				bw.flush(); // 서버에 데이터 전송
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Player 정보 DB에서 불러오기
	private void fetchPlayerInfos() {
		ConnectDb cdb = new ConnectDb();
		cdb.connectDb();
		myInfo = ConnectDb.map.get(myid);
		if (myInfo != null) {
			mywins = Integer.parseInt(myInfo.get(1)); // 승
			mydraws = Integer.parseInt(myInfo.get(2)); // 무
			mylosses = Integer.parseInt(myInfo.get(3)); // 패
			myrating = Integer.parseInt(myInfo.get(4)); // 평점
		}
		enemyInfo = ConnectDb.map.get(enemyid);
		if (enemyInfo != null) {
			enemywins = Integer.parseInt(enemyInfo.get(1));
			enemydraws = Integer.parseInt(enemyInfo.get(2));
			enemylosses = Integer.parseInt(enemyInfo.get(3));
			enemyrating = Integer.parseInt(enemyInfo.get(4));
		}
	}
	//턴 제어, 승리 여부 확인
	private void handleLocalMove(int row, int col) {
		buttons[row][col].setText(mySymbol);
		buttons[row][col].setEnabled(false);

		// 승리 여부 확인
		if (checkWin(mySymbol)) {
			cntTimer.stop();
			statusLabel.setText("승리!");
			disableBoard(); // 보드 비활성화
			onGameEnd("win");
			endGame("승리");
		} else if (isBoardFull()) {
			cntTimer.stop();
			statusLabel.setText("무승부!");
			disableBoard();
			onGameEnd("draw");
			endGame("무승부");
		} else {
			isMyTurn = false;
			statusLabel.setText("상대의 차례입니다.");
		}
	}
	//턴 제어, 패배 여부 확인
	public void markOpponentMove(int row, int col) {
		if (buttons[row][col].getText().equals("")) {
			buttons[row][col].setText(enemySymbol); // 상대의 기호로 마킹
			buttons[row][col].setEnabled(false); // 상대의 수를 마킹한 버튼 비활성화
			// 패배 조건은 여기서 확인
			if (checkWin(enemySymbol)) {
				cntTimer.stop();
				statusLabel.setText("패배!");
				disableBoard();
				onGameEnd("lose");
				endGame("패배");
				
			} else if (isBoardFull()) {
				cntTimer.stop();
				statusLabel.setText("무승부!");
				disableBoard();
				onGameEnd("draw");
				endGame("무승부");
			} else {
				// 턴을 내 것으로 변경
				isMyTurn = true;
				statusLabel.setText("당신 차례입니다.");
			}
		}
	}
	//승리 조건
	private boolean checkWin(String symbol) {
		// 가로, 세로, 대각선 확인
		for (int i = 0; i < 3; i++) {
			if (buttons[i][0].getText().equals(symbol) && buttons[i][1].getText().equals(symbol)
					&& buttons[i][2].getText().equals(symbol))
				return true;
			if (buttons[0][i].getText().equals(symbol) && buttons[1][i].getText().equals(symbol)
					&& buttons[2][i].getText().equals(symbol))
				return true;
		}
		if (buttons[0][0].getText().equals(symbol) && buttons[1][1].getText().equals(symbol)
				&& buttons[2][2].getText().equals(symbol))
			return true;
		if (buttons[0][2].getText().equals(symbol) && buttons[1][1].getText().equals(symbol)
				&& buttons[2][0].getText().equals(symbol))
			return true;
		return false;
	}
	//승리, 패배 아닌데 버튼 모두 채워졌을 때 무승부
	private boolean isBoardFull() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (buttons[i][j].getText().equals(""))
					return false;
			}
		}
		return true;
	}
	//버튼 비활성화
	private void disableBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				buttons[i][j].setEnabled(false); 
			}
		}
	}
	
	private void sendMessage() {
		String msg = chatInput.getText().trim();
		if (!msg.isEmpty()) {
			Client.sendMessage(msg); // 채팅 메시지 전송
			chatInput.setText("");
		}
	}

	public void appendChat(String msg) {
		chatArea.append(msg + "\n"); // 채팅 영역에 메시지 추가
	}

	public void updatePlayerPanels() {
		fetchPlayerInfos();
		// player1 프로필 화면
		Round.RoundPanel playerOnePanel = new Round.RoundPanel(Color.decode("#FFFACD"));
		playerOnePanel.setLayout(new BoxLayout(playerOnePanel, BoxLayout.Y_AXIS));

		JLabel user1Score = new JLabel(Integer.toString(myrating), JLabel.CENTER);
		user1Score.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel user1Pic = new JPanel() {
            private Image image;

            {
                try {
                    image = javax.imageio.ImageIO.read(new File("img/"+getGradeImageName(myrating)+".png")); // 이미지 파일 불러오기
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int diameter = 60;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;

                // 원형 마스크를 그리기 위한 Graphics2D 설정
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setClip(new java.awt.geom.Ellipse2D.Float(x, y, diameter, diameter));

                if (image != null) {
                    g2d.drawImage(image, x, y, diameter, diameter, this); // 이미지 원 안에 맞춰 그리기
                } else {
                    g2d.setColor(Color.GRAY);
                    g2d.fillOval(x, y, diameter, diameter); // 이미지가 없으면 회색 원
                }

                g2d.dispose();
            }
        };
        user1Pic.setPreferredSize(new Dimension(80, 80)); // 패널 크기 설정

		user1Pic.setPreferredSize(new Dimension(80, 80));
		user1Pic.setOpaque(false);
		user1Pic.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel user1Name = new JLabel(myid, JLabel.CENTER);

		user1Name.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel user1Record = new JLabel(mywins + "승" + mydraws + "무" + mylosses + "패", JLabel.CENTER);
		user1Record.setAlignmentX(Component.CENTER_ALIGNMENT);
		int mytotal = mywins + mydraws + mylosses;
		int myrate = 0;
		if (mytotal > 0) {
			myrate = (int) (((double) mywins / (mywins + mydraws + mylosses)) * 100);
		} else {
			myrate = 0;
		}
		JLabel user1WinRate = new JLabel("승률: " + myrate + "%", JLabel.CENTER);
		user1WinRate.setAlignmentX(Component.CENTER_ALIGNMENT);

		playerOnePanel.add(Box.createVerticalStrut(5));
		playerOnePanel.add(user1Score);
		playerOnePanel.add(user1Pic);
		playerOnePanel.add(user1Name);
		playerOnePanel.add(user1Record);
		playerOnePanel.add(user1WinRate);

		playerOnePanel.add(new JLabel("Symbol: " + mySymbol));

		// player2 프로필 화면
		Round.RoundPanel playerTwoPanel = new Round.RoundPanel(Color.decode("#FFFACD"));
		playerTwoPanel.setLayout(new BoxLayout(playerTwoPanel, BoxLayout.Y_AXIS));

		JLabel user2Score = new JLabel(Integer.toString(enemyrating), JLabel.CENTER);
		user1Score.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel user2Pic = new JPanel() {
            private Image image;

            {
                try {
                    image = javax.imageio.ImageIO.read(new File("img/"+getGradeImageName(enemyrating)+".png")); // 이미지 파일 불러오기
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int diameter = 60;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;

                // 원형 마스크를 그리기 위한 Graphics2D 설정
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setClip(new java.awt.geom.Ellipse2D.Float(x, y, diameter, diameter));

                if (image != null) {
                    g2d.drawImage(image, x, y, diameter, diameter, this); // 이미지 원 안에 맞춰 그리기
                } else {
                    g2d.setColor(Color.GRAY);
                    g2d.fillOval(x, y, diameter, diameter); // 이미지가 없으면 회색 원
                }

                g2d.dispose();
            }
        };
		user2Pic.setPreferredSize(new Dimension(80, 80));
		user2Pic.setOpaque(false);
		user2Pic.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel user2Name = new JLabel(enemyid, JLabel.CENTER);

		user2Name.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel user2Record = new JLabel(enemywins + "승" + enemydraws + "무" + enemylosses + "패", JLabel.CENTER);
		user2Record.setAlignmentX(Component.CENTER_ALIGNMENT);
		int enemytotal = mywins + mydraws + mylosses;
		int enemyrate = 0;
		if (enemytotal > 0) {
			enemyrate = (int) (((double) enemywins / (enemywins + enemydraws + enemylosses)) * 100);
		} else {
			enemyrate = 0;
		}
		JLabel user2WinRate = new JLabel(
				"승률: " + (int) (((double) enemywins / (enemywins + enemydraws + enemylosses)) * 100) + "%",
				JLabel.CENTER);
		user2WinRate.setAlignmentX(Component.CENTER_ALIGNMENT);

		playerTwoPanel.add(Box.createVerticalStrut(5));
		playerTwoPanel.add(user2Score);
		playerTwoPanel.add(user2Pic);
		playerTwoPanel.add(user2Name);
		playerTwoPanel.add(user2Record);
		playerTwoPanel.add(user2WinRate);

		playerTwoPanel.add(new JLabel("Symbol: " + enemySymbol));

		playerInfoPanel.removeAll();
		playerInfoPanel.add(playerOnePanel);
		playerInfoPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		playerInfoPanel.add(playerTwoPanel);
		playerInfoPanel.revalidate();
		playerInfoPanel.repaint();
	}
	//implements 위한 override
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	//서버에서 오버라이드 됨(서버에 result 전송)
	public void onGameEnd(String result) {
	}
	//종료 팝업
	public void endGame(String resultmessage) {
		endGameDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "게임 종료", true);
		endGameDialog.setSize(700, 400);
		endGameDialog.setLocationRelativeTo(this);
		endGameDialog.setUndecorated(true);
		endGameDialog.setOpacity(0.9f);
		endGameDialog.setLayout(new BoxLayout(endGameDialog.getContentPane(), BoxLayout.Y_AXIS));
		
		// HTML로 메시지 포맷팅, resultmessage 부분만 빨간색으로 바꿈
	    String htmlMessage = myid + "님이 <font color='red'>" + resultmessage + "</font> 하였습니다.";

	    // 폰트 스타일 설정
	    Font font = new Font("Arial", Font.BOLD, 30);  // 폰트 스타일과 크기 설정

	    // JLabel에 HTML과 폰트 적용
	    JLabel resultLabel = new JLabel("<html>" + htmlMessage + "</html>", JLabel.CENTER);
	    resultLabel.setAlignmentX(CENTER_ALIGNMENT);
	    resultLabel.setFont(font);  // 폰트 스타일 적용
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);

		Round.RoundButton replayButton = new Round.RoundButton("돌아가기");
		Round.RoundButton endButton = new Round.RoundButton("게임 종료");

		Dimension buttonSize = new Dimension(160, 35);
		replayButton.setPreferredSize(buttonSize);
		endButton.setPreferredSize(buttonSize);

		Dimension boxSize = new Dimension(50, 0);
		buttonPanel.add(replayButton);
		buttonPanel.add(Box.createRigidArea(boxSize));
		buttonPanel.add(endButton);

		// 돌아가기 -> Index 화면으로 전환
		replayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				endGameDialog.dispose();
				Index index = new Index();
				MainFrame.cardPanel.add(index, "Index");
				MainFrame.switchTo("Index");
			}
		});

		// 게임 종료 -> 창 닫기
		endButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.closeIO();
				System.exit(0);
			}
		});

		endGameDialog.add(Box.createVerticalStrut(100));
		endGameDialog.add(resultLabel);
		endGameDialog.add(Box.createVerticalStrut(180));
		endGameDialog.add(buttonPanel);
		endGameDialog.setVisible(true);

	}
	//rating을 기준으로 계급 결정 -> 프로필 사진 결
	private String getGradeImageName(int rating) {
		if (rating >= 2000) return "royal";
		if (rating >= 1500) return "gold";
		if (rating >= 1250) return "silver";
		return "bronze";
	}

}