package com.tictactalk;

import java.awt.*;
import javax.swing.*;

public class Test extends JFrame {

    public JPanel panel = new JPanel();

    public Test() {
        this.setTitle("TicTacTalk");
        this.setSize(852, 756); // (213, 189) * 4
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false); // 창 크기 고정

        panel.setBackground(Color.decode("#4ED59B"));
        // 세로 정렬

        // --- 1. 로고 영역 ---
        JLabel logoLabel = new JLabel("TicTacTalk", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(50)); // 여백
        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(50));

        // --- 2. ID / PW 입력 영역 ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setMaximumSize(new Dimension(400, 100));
        inputPanel.setOpaque(false);

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        JLabel pwLabel = new JLabel("PW:");
        JPasswordField pwField = new JPasswordField();

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(pwLabel);
        inputPanel.add(pwField);

        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(40));

        // --- 3. 버튼 영역 ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton loginButton = new JButton("로그인");
        JButton joinButton = new JButton("회원가입");

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // 버튼 사이 여백
        buttonPanel.add(joinButton);

        panel.add(buttonPanel);

        // 패널 붙이기 및 화면 표시
        this.add(panel);
        this.setVisible(true);
    }
}
