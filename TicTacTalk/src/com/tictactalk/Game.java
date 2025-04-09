package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import com.customs.Round;
import com.net.Client;
import com.net.ConnectDb;

public class Game extends JPanel implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isPlayerOneTurn = true;  // X�뒗 泥� 踰덉㎏ �뵆�젅�씠�뼱
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

    

	public Game(boolean isMyTurn) {

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
//        if (Client.playerId.compareTo(Client.enemyID) < 0) {
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
					System.out.println("차례를 지켜주세요!");
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
        timerLabel = new JLabel("남은 시간 : "+ timer +"초", SwingConstants.RIGHT);
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
	private void startTimer() {
		cntTimer = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(timer >0) {
					timer--;
					timerLabel.setText("남은 시간 : " + timer + "초");
				}else {
					onGameEnd("lose");
				}
				
			}
		});
		cntTimer.start();
	}

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
	
	private void fetchPlayerInfos() {
    	ConnectDb cdb = new ConnectDb();
    	cdb.connectDb();
        myInfo = ConnectDb.map.get(myid);
        if (myInfo != null) {
            mywins = Integer.parseInt(myInfo.get(1));      // 승
            mydraws = Integer.parseInt(myInfo.get(2));     // 무
            mylosses = Integer.parseInt(myInfo.get(3));    // 패
            myrating = Integer.parseInt(myInfo.get(4));    // 평점
        }

        enemyInfo = ConnectDb.map.get(enemyid);
        if (enemyInfo != null) {
            enemywins = Integer.parseInt(enemyInfo.get(1));
            enemydraws = Integer.parseInt(enemyInfo.get(2));
            enemylosses = Integer.parseInt(enemyInfo.get(3));
            enemyrating = Integer.parseInt(enemyInfo.get(4));
        }
    }
	private void handleLocalMove(int row, int col) {
        buttons[row][col].setText(mySymbol); 
        buttons[row][col].setEnabled(false);

		// 승리 여부 확인
        if (checkWin()) {
           statusLabel.setText("승리!");
           disableBoard();  // 보드 비활성화
           Client.sendGameResult("win");
           onGameEnd("win");
       } else if (isBoardFull()) {
           statusLabel.setText("무승부!");
           Client.sendGameResult("draw");
           onGameEnd("draw");
       } else {
           isMyTurn = false;
           statusLabel.setText("상대의 차례입니다.");
       }
   }

	public void markOpponentMove(int row, int col) {
		if (buttons[row][col].getText().equals("")) {
			buttons[row][col].setText(enemySymbol); // 상대의 기호로 마킹
			buttons[row][col].setEnabled(false); // 상대의 수를 마킹한 버튼 비활성화

			// 턴을 내 것으로 변경
			isMyTurn = true;
			statusLabel.setText("당신 차례입니다.");
		}
	}

	private boolean checkWin() {
		String symbol = mySymbol;
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

	private boolean isBoardFull() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (buttons[i][j].getText().equals(""))
					return false;
			}
		}
		return true;
	}

	private void disableBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				buttons[i][j].setEnabled(false); // 게임 종료 후 버튼 비활성화
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
        
        JLabel user1Name = new JLabel(myid, JLabel.CENTER);
        
        user1Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1Record = new JLabel(mywins+"승"+ mydraws +"무"+mylosses + "패", JLabel.CENTER);
        user1Record.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1WinRate = new JLabel("승률: "+(int)(((double)mywins/(mywins+mydraws+mylosses))*100)+"%",JLabel.CENTER);
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
        
        JLabel user2Name = new JLabel(enemyid, JLabel.CENTER);
        
        user2Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2Record = new JLabel(enemywins+"승"+ enemydraws +"무"+enemylosses + "패", JLabel.CENTER);
        System.out.println(enemywins);
        System.out.println(enemydraws);
        System.out.println(enemywins/(enemywins+enemydraws+enemylosses)*100);
        user2Record.setAlignmentX(Component.CENTER_ALIGNMENT);
        double avg = (4/5)*100;
        JLabel user2WinRate = new JLabel("승률: "+(int)(((double)enemywins/(enemywins+enemydraws+enemylosses))*100)+"%",JLabel.CENTER);
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
    @Override
    public void actionPerformed(ActionEvent e) {
    }
    
    public void onGameEnd(String result) {
        String message;
        System.out.println();
        switch (result) {
            case "win":
                statusLabel.setText("승리!");
                message = "승리!";
                break;
            case "lose":
                statusLabel.setText("패배!");
                message = "패배!";
                break;
            case "draw":
                statusLabel.setText("무승부!");
                message = "무승부!";
                break;
            default:
                message = "";
        }

        disableBoard();

        // 게임 종료 팝업
        JOptionPane.showMessageDialog(this, message, "게임 종료", JOptionPane.INFORMATION_MESSAGE);
    }
    
}