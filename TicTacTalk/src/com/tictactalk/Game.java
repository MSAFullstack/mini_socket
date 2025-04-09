package com.tictactalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import com.net.Client;

public class Game extends JPanel {
    private TextArea chatArea = new TextArea();
    private TextField inputField = new TextField();
    private JButton[] btns = new JButton[9];
    private boolean isMyTurn = false;
    private boolean gameEnded = false;

    public Game() {
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            btns[i] = new JButton("");
            final int index = i;
            btns[i].setFont(new Font("Arial", Font.BOLD, 36));
            btns[i].addActionListener(e -> handleMove(index));
            boardPanel.add(btns[i]);
        }

        chatArea.setEditable(false);
        inputField.addActionListener(e -> sendMessage());

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(chatArea, BorderLayout.CENTER);
        chatPanel.add(inputField, BorderLayout.SOUTH);
        chatPanel.setPreferredSize(new Dimension(852, 200));

        add(boardPanel, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.SOUTH);

        Client.setGame(this);
    }

    private void handleMove(int index) {
        if (!isMyTurn || !btns[index].getText().equals("") || gameEnded) return;

        btns[index].setText("X");
        isMyTurn = false;
        checkGameState();

        try {
            Client.bw.write("MOVE " + index);
            Client.bw.newLine();
            Client.bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markOpponentMove(int index) {
        if (index >= 0 && index < 9 && btns[index].getText().equals("")) {
            btns[index].setText("O");
            isMyTurn = true;
            checkGameState();
        }
    }

    public void displayMessage(String msg) {
        chatArea.setText(chatArea.getText() + msg + "\n");
    }

    private void sendMessage() {
        String msg = inputField.getText();
        if (msg.isEmpty()) return;

        try {
            Client.bw.write(msg);
            Client.bw.newLine();
            Client.bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputField.setText("");
    }

    public void setMyTurn(boolean turn) {
        isMyTurn = turn;
    }

    private void checkGameState() {
        String[][] lines = new String[8][3];
        for (int i = 0; i < 3; i++) {
            lines[i][0] = btns[i * 3].getText();
            lines[i][1] = btns[i * 3 + 1].getText();
            lines[i][2] = btns[i * 3 + 2].getText();
            lines[i + 3][0] = btns[i].getText();
            lines[i + 3][1] = btns[i + 3].getText();
            lines[i + 3][2] = btns[i + 6].getText();
        }
        lines[6][0] = btns[0].getText(); lines[6][1] = btns[4].getText(); lines[6][2] = btns[8].getText();
        lines[7][0] = btns[2].getText(); lines[7][1] = btns[4].getText(); lines[7][2] = btns[6].getText();

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
        for (JButton btn : btns) {
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
}