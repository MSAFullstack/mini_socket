package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;

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
        
        

        if (Client.playerId.compareTo(Client.enemyID) < 0) {
            mySymbol = "X";  
            enemySymbol = "O"; 
        } else {
            mySymbol = "O";
            enemySymbol = "X";
        }

        // 게임판 (왼쪽 2/3)	
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setPreferredSize(new Dimension(550, 550));
        Font font = new Font("Arial", Font.BOLD, 60);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;

                // 踰꾪듉 珥덇린�솕
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(font);
                buttons[i][j].setFocusPainted(false);
                buttons[row][col].addActionListener(e -> {
                    System.out.println("踰꾪듉�겢由��떆");
                    if (!this.isMyTurn || !buttons[row][col].getText().equals("")) {
                        return; // �궡 �꽩�씠 �븘�땲嫄곕굹 �씠誘� 踰꾪듉�뿉 �닔媛� �뱾�뼱�엳�쑝硫� 臾댁떆
                    }

                    // �꽌踰꾨줈 �궡 �닔 蹂대궡湲�
                    sendMove(row, col);
                    System.out.println("here pass?");
                    handleLocalMove(row, col);  // 踰꾪듉 �겢由� �썑 濡쒖뺄�뿉�꽌 �긽�깭 �뾽�뜲�씠�듃
                });
                boardPanel.add(buttons[i][j]);
            }
        }

        // 오른쪽 전체 (플레이어 정보 + 채팅)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.decode("#FFFFFF"));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(280, 700));

        // 상단 : 플레이어 전적 정보
        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.X_AXIS));
        playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 중간 : 채팅창
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

        // �긽�깭, �떆媛� �몴�떆
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(isMyTurn ? "당신 차례입니다." : "상대 차례입니다.", JLabel.CENTER);
        timerLabel = new JLabel("남은 시간 : 60초", SwingConstants.RIGHT);
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
    
    
    

    private void sendMove(int row, int col) {
        try {
            BufferedWriter bw = Client.bw;
            if (bw != null) {
                bw.write("move:" + row + ":" + col);
                bw.newLine();
                bw.flush(); // �꽌踰꾩뿉 �뜲�씠�꽣 �쟾�넚
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLocalMove(int row, int col) {
        buttons[row][col].setText(mySymbol); 
        buttons[row][col].setEnabled(false);
        System.out.println("pass here!"+ checkWin());

        // �듅由� �뿬遺� �솗�씤
        if (checkWin()) {
        	 System.out.println("게임 승리 조건 만족");
            statusLabel.setText("승리!");
            disableBoard();  // 보드 비활성화
            Client.sendGameResult("win");
            onGameEnd("win");
        } else if (isBoardFull()) {
        	 System.out.println("무승부 조건 만족");
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
            buttons[row][col].setText(enemySymbol); 
            buttons[row][col].setEnabled(false);

            
            isMyTurn = true;
            statusLabel.setText("당신 차례입니다.");
        }
    }
    private boolean checkWin() {
        String symbol = mySymbol;
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(symbol) && buttons[i][1].getText().equals(symbol) && buttons[i][2].getText().equals(symbol)) return true;
            if (buttons[0][i].getText().equals(symbol) && buttons[1][i].getText().equals(symbol) && buttons[2][i].getText().equals(symbol)) return true;
        }
        if (buttons[0][0].getText().equals(symbol) && buttons[1][1].getText().equals(symbol) && buttons[2][2].getText().equals(symbol)) return true;
        if (buttons[0][2].getText().equals(symbol) && buttons[1][1].getText().equals(symbol) && buttons[2][0].getText().equals(symbol)) return true;
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) return false;
            }
        }
        return true;
    }

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
            Client.sendMessage(Client.playerId + ":" + msg);
            chatInput.setText("");
        }
    }

    public void appendChat(String msg) {
        chatArea.append(msg + "\n"); 
    }
    public void updatePlayerPanels() {
        // player1 프로필 화면
    	fetchPlayerInfos();
        Round.RoundPanel playerOnePanel = new Round.RoundPanel(Color.decode("#FFFACD"));
        playerOnePanel.setLayout(new BoxLayout(playerOnePanel, BoxLayout.Y_AXIS));
        System.out.println(myid+","+mywins+","+mydraws+","+mylosses+","+myrating);
        System.out.println(enemyid+","+enemywins+","+enemydraws+","+enemylosses+","+enemyrating);
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

        JLabel user1WinRate = new JLabel("승률: 20%", JLabel.CENTER);
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
        user2Record.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2WinRate = new JLabel("승률: 20%", JLabel.CENTER);
        user2WinRate.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerTwoPanel.add(Box.createVerticalStrut(5));
        playerTwoPanel.add(user2Score);
        playerTwoPanel.add(user2Pic);
        playerTwoPanel.add(user2Name);
        playerTwoPanel.add(user2Record);
        playerTwoPanel.add(user2WinRate);
        
        
        
        
        playerTwoPanel.add(new JLabel("Symbol: " + enemySymbol));
        
        // �쟾�쟻 �벑 �떎瑜� �젙蹂� 異붽� 媛��뒫 (�삁: �듅�뙣 湲곕줉)
        
        // �뵆�젅�씠�뼱 �젙蹂� �뙣�꼸�뿉 異붽�
        playerInfoPanel.removeAll();  // 湲곗〈 �궡�슜 �젣嫄�
        playerInfoPanel.add(playerOnePanel);
        playerInfoPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        playerInfoPanel.add(playerTwoPanel);
        playerInfoPanel.revalidate();  // 蹂�寃� �궗�빆 諛섏쁺
        playerInfoPanel.repaint();  // �솕硫� �뾽�뜲�씠�듃
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
