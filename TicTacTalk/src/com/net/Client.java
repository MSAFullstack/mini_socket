package com.net;

import com.tictactalk.Game;
import com.tictactalk.MainFrame;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    public static Socket sock = null;
    public static BufferedWriter bw;
    private static Game gameInstance;
    private static String playerId;
    public static Runnable onGameStart;

    public static void setGame(Game game) { gameInstance = game; }
    public static void setPlayerId(String id) { playerId = id; }
    public static String getPlayerId() { return playerId; }

    @Override
    public void run() {
        try {
            InetAddress addr = InetAddress.getByName("172.30.1.1");
            sock = new Socket(addr, 3000);
            bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

            // 서버에 ID 전송
            bw.write("ID " + playerId);
            bw.newLine();
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String msg;
            while ((msg = br.readLine()) != null) handleMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(String msg) {
        if (msg.equals("call")) {
            Game game = new Game();
            setGame(game);
            MainFrame.cardPanel.add(game, "Game");
            MainFrame.switchTo("Game");
            //한 명만 선공 처리
            if(playerId.endsWith("1")) {
            	game.setMyTurn(true);
            }
            if (onGameStart != null) onGameStart.run();
        } else if (msg.startsWith("MOVE")) {
            int index = Integer.parseInt(msg.split(" ")[1]);
            if (gameInstance != null) gameInstance.markOpponentMove(index);
        } else {
            if (gameInstance != null) gameInstance.displayMessage(msg);
        }
    }
}