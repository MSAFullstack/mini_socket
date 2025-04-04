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
        this.setResizable(false); // â ũ�� ����

        panel.setBackground(Color.decode("#4ED59B"));
        // ���� ����

        // --- 1. �ΰ� ���� ---
        JLabel logoLabel = new JLabel("TicTacTalk", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(50)); // ����
        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(50));

        // --- 2. ID / PW �Է� ���� ---
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

        // --- 3. ��ư ���� ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton loginButton = new JButton("�α���");
        JButton joinButton = new JButton("ȸ������");

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // ��ư ���� ����
        buttonPanel.add(joinButton);

        panel.add(buttonPanel);

        // �г� ���̱� �� ȭ�� ǥ��
        this.add(panel);
        this.setVisible(true);
    }
}
