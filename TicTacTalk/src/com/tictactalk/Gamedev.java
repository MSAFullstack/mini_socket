package com.tictactalk;

import java.awt.*;
import javax.swing.*;

import com.customs.Round;

public class Gamedev extends JFrame {
    public JPanel panel = new JPanel();

    public Gamedev() {
        this.setTitle("TicTacTalk");
        this.setSize(852, 756);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        
        panel.setBackground(Color.decode("#4ED59B"));
        panel.setLayout(new BorderLayout());

        
        JLabel titleLabel = new JLabel("TIC TAC TALK", JLabel.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 40));
        
        panel.add(titleLabel, BorderLayout.NORTH);

        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.decode("#4ED59B"));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5));
        boardPanel.setBackground(Color.BLACK);


        for (int i = 0; i < 9; i++) {
            JButton cell = new JButton("");
            cell.setFont(new Font("Arial", Font.BOLD, 40));
            boardPanel.add(cell);
        }

        
        JPanel userPanel = new JPanel();
        userPanel.setBackground(Color.decode("#4ED59B"));
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        

        
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        
        
        
        Round.RoundPanel user1Panel = new Round.RoundPanel(Color.decode("#FFFACD"));
        user1Panel.setLayout(new BoxLayout(user1Panel, BoxLayout.Y_AXIS));
 

        JLabel user1Score = new JLabel("1000", JLabel.CENTER);
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

        JLabel user1Name = new JLabel("Player 1", JLabel.CENTER);

        user1Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1Record = new JLabel("1½Â 2¹« 4ÆÐ", JLabel.CENTER);
        user1Record.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user1WinRate = new JLabel("½Â·ü: 20%", JLabel.CENTER);
        user1WinRate.setAlignmentX(Component.CENTER_ALIGNMENT);

        user1Panel.add(Box.createVerticalStrut(5));
        user1Panel.add(user1Score);
        user1Panel.add(user1Pic);
        user1Panel.add(user1Name);
        user1Panel.add(user1Record);
        user1Panel.add(user1WinRate);


        Round.RoundPanel user2Panel = new Round.RoundPanel(Color.decode("#FFFACD"));
        user2Panel.setLayout(new BoxLayout(user2Panel, BoxLayout.Y_AXIS));


        JLabel user2Score = new JLabel("1000", JLabel.CENTER);
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

        JLabel user2Name = new JLabel("Player 2", JLabel.CENTER);
        user2Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2Record = new JLabel("3½Â 1¹« 2ÆÐ", JLabel.CENTER);
        user2Record.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user2WinRate = new JLabel("½Â·ü: 60%", JLabel.CENTER);
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


        
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JTextArea chatArea = new JTextArea(6, 20);
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTextArea chatInputArea = new JTextArea(2, 20);
        chatInputArea.setLineWrap(true);
        chatInputArea.setWrapStyleWord(true);
        chatInputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(chatInputArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        JButton sendButton = new JButton("Àü¼Û");
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        
        userPanel.add(profilePanel);
        userPanel.add(chatPanel);

        boardPanel.setPreferredSize(new Dimension(600, 0));
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        userPanel.setPreferredSize(new Dimension(252, 0));
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        centerPanel.add(userPanel, BorderLayout.EAST);
        panel.add(centerPanel, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(Color.decode("#4ED59B"));
        bottomPanel.setPreferredSize(new Dimension(852, 80));

   
        JTextField chatInput = new JTextField();


   
        JLabel timerLabel = new JLabel("³²Àº ½Ã°£ : 60ÃÊ", JLabel.CENTER);
        timerLabel.setPreferredSize(new Dimension(200, 80));

        bottomPanel.add(chatInput, BorderLayout.CENTER);
        bottomPanel.add(timerLabel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

   
        this.add(panel);
        this.setVisible(true);
    }

}