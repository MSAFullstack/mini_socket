package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import com.net.Client;
import com.net.ConnectDb;

public class Game extends JPanel implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isPlayerOneTurn = true;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel timerLabel;

    public Game() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#4ED59B"));

        // ������ (���� 2/3)
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setPreferredSize(new Dimension(550, 550));
        Font font = new Font("Arial", Font.BOLD, 60);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(font);
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }

        // ������ ��ü (�÷��̾� ���� + ä��)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(280, 700));

        // ���: �÷��̾� ���� ����
        JPanel playerInfoPanel = new JPanel(new GridLayout(1, 2));
        playerInfoPanel.setMaximumSize(new Dimension(280, 140));
        JPanel p1Panel = createPlayerPanel(Client.playerId);
        JPanel p2Panel = createPlayerPanel(Client.enemyId!= null ? Client.enemyId : "���"); 
        playerInfoPanel.add(p1Panel);
        playerInfoPanel.add(p2Panel);

        // �߰�: ä��â
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(260, 400));
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatInput = new JTextField();
        sendButton = new JButton("����");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        chatInput.addActionListener(e -> sendMessage());

        // �ϴ�: ���� ���� �� Ÿ�̸�
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(Client.playerId + " �����Դϴ�.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel = new JLabel("���� �ð� : 60��", SwingConstants.RIGHT);
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

    private JPanel createPlayerPanel(String playerId) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode("#FFE57F"));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel ratingLabel = new JLabel("9999", JLabel.CENTER); // �⺻��
        JLabel profileLabel = new JLabel(new ImageIcon("img/default.png")); // ������ (���� rating�� ���� ����)
        JLabel nameLabel = new JLabel(playerId, JLabel.CENTER);
        
        JLabel recordLabel = new JLabel("���� �ҷ����� ��", JLabel.CENTER);
        JLabel winRateLabel = new JLabel("�·� �����", JLabel.CENTER);
        List<String> userInfo = ConnectDb.map.get(playerId);
        int win = Integer.parseInt(userInfo.get(1));
        int lose = Integer.parseInt(userInfo.get(2));
        int draw = Integer.parseInt(userInfo.get(3));
        int rating = Integer.parseInt(userInfo.get(4));
        ratingLabel.setText(String.valueOf(rating));
        int total = win + lose + draw;
        int rate = total > 0 ? (int)(((double)win / total) * 100) : 0;
        recordLabel.setText(win + "�� " + draw + "�� " + lose + "��");
        winRateLabel.setText("�·� " + String.format("%03d", rate) + "%");
        profileLabel.setIcon(new ImageIcon("img/profile_" + rating / 1000 + ".png"));
        System.out.println(win+"-----"+lose+"-----"+draw+"-----"+total+"-----"+rate+"-----");
        
        

        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        winRateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(ratingLabel);
        panel.add(profileLabel);
        panel.add(nameLabel);
        panel.add(recordLabel);
        panel.add(winRateLabel);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton) e.getSource();
        if (!clicked.getText().equals("")) return;
        clicked.setText(isPlayerOneTurn ? "X" : "O");
        if (checkWin()) {
            statusLabel.setText((isPlayerOneTurn ? "Player 1 (X)" : "Player 2 (O)") + " Wins!");
            disableBoard();
            Client.sendGameResult(isPlayerOneTurn ? "win" : "lose");
        } else if (isBoardFull()) {
            statusLabel.setText("���º��Դϴ�.");
            Client.sendGameResult("draw");
        } else {
            isPlayerOneTurn = !isPlayerOneTurn;
            statusLabel.setText((isPlayerOneTurn ? Client.playerId : "���") + " �����Դϴ�.");
        }
    }

    private boolean checkWin() {
        String symbol = isPlayerOneTurn ? "X" : "O";
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
            try {
                BufferedWriter bw = Client.bw;
                if (bw != null) {
                    bw.write(msg);
                    bw.newLine();
                    bw.flush();
                    chatInput.setText("");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void appendChat(String msg) {
        chatArea.append(msg + "\n");
    }
}