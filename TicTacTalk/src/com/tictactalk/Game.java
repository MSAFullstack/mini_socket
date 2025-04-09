package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import com.net.Client;
import com.net.ConnectDb;

public class Game extends JPanel implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isPlayerOneTurn = true;  // X는 첫 번째 플레이어
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private boolean isMyTurn;
    private JPanel playerInfoPanel;
    private String mySymbol;
    private String enemySymbol;

    public Game(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#4ED59B"));
        JLabel titleLabel = new JLabel("TIC TAC TALK", JLabel.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 40));
        this.add(titleLabel, BorderLayout.NORTH);

        // X/O 역할 분담
        if (Client.playerId.compareTo(Client.enemyID) < 0) {
            mySymbol = "X";  // 첫 번째 플레이어는 X
            enemySymbol = "O";  // 두 번째 플레이어는 O
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
                    System.out.println("버튼클릭시");
                    if (!this.isMyTurn || !buttons[row][col].getText().equals("")) {
                        return; // 내 턴이 아니거나 이미 버튼에 수가 들어있으면 무시
                    }

                    // 서버로 내 수 보내기
                    sendMove(row, col);
                    handleLocalMove(row, col);  // 버튼 클릭 후 로컬에서 상태 업데이트
                });
                boardPanel.add(buttons[i][j]);
            }
        }

        // 오른쪽 전체(플레이어 정보 + 채팅)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(280, 700));

        // 상단: 플레이어 전적 정보(초기 빈 패널)
        playerInfoPanel = new JPanel(new GridLayout(1, 2));
        playerInfoPanel.setMaximumSize(new Dimension(280, 140));
        playerInfoPanel.add(new JPanel());
        playerInfoPanel.add(new JPanel());

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
        statusLabel = new JLabel(isMyTurn ? "내 턴입니다." : "상대 턴입니다.", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel = new JLabel("남은 시간 : 60초", SwingConstants.RIGHT);
        timerLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(timerLabel, BorderLayout.EAST);

        rightPanel.add(playerInfoPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(chatPanel);
        rightPanel.add(Box.createVerticalStrut(10));

        add(boardPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
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

    private void handleLocalMove(int row, int col) {
        // 버튼에 내 기호 마킹
        buttons[row][col].setText(mySymbol); 
        
        // 해당 버튼을 비활성화
        buttons[row][col].setEnabled(false);

        // 승리 여부 확인
        if (checkWin()) {
            statusLabel.setText("승리!");
            disableBoard();  // 게임 끝나면 보드 비활성화
            Client.sendGameResult("win");  // 서버로 승리 결과 전송
        } else if (isBoardFull()) {
            statusLabel.setText("무승부!");
            Client.sendGameResult("draw");  // 서버로 무승부 결과 전송
        } else {
            isMyTurn = false;
            statusLabel.setText("상대 턴입니다.");
        }
    }

    public void markOpponentMove(int row, int col) {
        if (buttons[row][col].getText().equals("")) {
            buttons[row][col].setText(enemySymbol); // 상대의 기호로 마킹
            buttons[row][col].setEnabled(false); // 상대의 수를 마킹한 버튼 비활성화

            // 턴을 내 것으로 변경
            isMyTurn = true;
            statusLabel.setText("내 턴입니다.");
        }
    }
    private boolean checkWin() {
        String symbol = mySymbol;
        // 가로, 세로, 대각선 확인
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
                buttons[i][j].setEnabled(false);  // 게임 종료 후 버튼 비활성화
            }
        }
    }

    private void sendMessage() {
        String msg = chatInput.getText().trim();
        if (!msg.isEmpty()) {
            Client.sendMessage(Client.playerId + ":" + msg);  // 채팅 메시지 전송
            chatInput.setText("");
        }
    }

    public void appendChat(String msg) {
        chatArea.append(msg + "\n");  // 채팅 영역에 메시지 추가
    }
    public void updatePlayerPanels() {
        // 플레이어 1 정보 업데이트
        JPanel playerOnePanel = new JPanel();
        playerOnePanel.setLayout(new BoxLayout(playerOnePanel, BoxLayout.Y_AXIS));
        playerOnePanel.add(new JLabel("Player 1: " + Client.playerId));
        playerOnePanel.add(new JLabel("Symbol: " + mySymbol));
        
        // 플레이어 2 정보 업데이트
        JPanel playerTwoPanel = new JPanel();
        playerTwoPanel.setLayout(new BoxLayout(playerTwoPanel, BoxLayout.Y_AXIS));
        playerTwoPanel.add(new JLabel("Player 2: " + Client.enemyID));
        playerTwoPanel.add(new JLabel("Symbol: " + enemySymbol));
        
        // 전적 등 다른 정보 추가 가능 (예: 승패 기록)
        
        // 플레이어 정보 패널에 추가
        playerInfoPanel.removeAll();  // 기존 내용 제거
        playerInfoPanel.add(playerOnePanel);
        playerInfoPanel.add(playerTwoPanel);
        playerInfoPanel.revalidate();  // 변경 사항 반영
        playerInfoPanel.repaint();  // 화면 업데이트
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // 오버라이드 되어있음
    }

    public void onGameEnd(String result) {
        switch (result) {
            case "win":
                statusLabel.setText("승리!");
                break;
            case "lose":
                statusLabel.setText("패배!");
                break;
            case "draw":
                statusLabel.setText("무승부!");
                break;
        }
        disableBoard();  // 게임 종료 후 보드 비활성화
    }
}
