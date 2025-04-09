package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import com.customs.Round;
import com.net.Client;

public class Index extends JPanel {
    public Index() {
        setBackground(Color.decode("#4ED59B"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("TicTacTalk", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 48));
        label.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(50));
        add(label);
        add(Box.createVerticalStrut(50));

        JTextField idField = new Round.RoundTextField(20);
        JPasswordField pwField = new Round.RoundPasswordField(20);
        Round.RoundButton loginBtn = new Round.RoundButton("로그인");

        idField.setMaximumSize(new Dimension(300, 40));
        pwField.setMaximumSize(new Dimension(300, 40));
        loginBtn.setMaximumSize(new Dimension(200, 50));

        add(idField);
        add(Box.createVerticalStrut(10));
        add(pwField);
        add(Box.createVerticalStrut(30));
        add(loginBtn);

        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();

            if (com.net.ConnectDb.login(id, pw)) {
                Client.setPlayerId(id);
                JDialog loading = makeLoadingDialog();
                Client.onGameStart = loading::dispose;
                Client client = new Client();
                client.start();
                loading.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "로그인 실패", "에러", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JDialog makeLoadingDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "매칭 대기", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JLabel("상대방을 기다리는 중...", JLabel.CENTER));
        return dialog;
    }
}