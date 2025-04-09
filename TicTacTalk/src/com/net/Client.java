package com.net;

import java.awt.BorderLayout;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.tictactalk.Game;
import com.tictactalk.Index;
import com.tictactalk.MainFrame;

public class Client extends Thread {
    public static BufferedWriter bw;
    public static BufferedReader br;
    private static Game gameInstance;
    public static String playerId;
    public static String enemyID;
    private Socket sock;

    @Override
    public void run() {
        ConnectDb db = new ConnectDb();
        db.connectDb();
        System.out.println("[Client] DB connect ok. 전적 수:" + ConnectDb.map.size());
        
        try {
            // 서버 주소 설정
            InetAddress addr = InetAddress.getByAddress(new byte[] { (byte) 172, 30, 1, 13 });
            sock = new Socket(addr, 3000);

            // 출력 스트림 설정
            OutputStream os = sock.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
            
            // 로그인 ID 전송
            bw.write("id:" + playerId);
            bw.newLine();
            bw.flush();

            // 입력 스트림 설정
            InputStream is = sock.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // 서버로부터 메시지를 받기 위한 반복문
            String msg;
            while ((msg = br.readLine()) != null) {
                executeCommand(msg);
                 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    private static void sendMoveToServer(int row, int col) {
        try {
            if (bw != null) {
                // 서버로 내 수 보내기 (형식: move:row:col)
                bw.write("move:" + row + ":" + col); // "move:row:col" 전송
                bw.newLine();
                bw.flush(); // 서버로 데이터 전송
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void executeCommand(String msg) {
        // 'move' 메시지 처리 (자신의 턴에서 수를 두는 경우)
        if (msg.startsWith("move:") && !msg.contains(":")) { // 클라이언트에서 자신의 수를 보낼 때
            System.out.println("너나");
            String[] parts = msg.split(":");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            
            sendMoveToServer(row, col); // 내 수를 서버로 보냄
            return;
        }

        // 'call' 메시지 처리: 상대방 ID를 설정하고 게임 시작
        if (msg.startsWith("call:")) {
            enemyID = msg.substring(5, msg.length());
            // 게임 시작 로직
            System.out.println("적의 id : " + enemyID);
            System.out.println("나의 id : " + playerId);
            if (playerId != null && enemyID != null) {
                SwingUtilities.invokeLater(() -> {
                    // 내 턴 결정
                    boolean isMyTurn = playerId.compareTo(enemyID) < 0;

                    // Game 객체 생성
                    Game game = new Game(isMyTurn) {
                        @Override
                        public void onGameEnd(String result) {
                            Client.sendGameResult(result);
                        }
                    };

                    // 게임 패널 설정
                    MainFrame.cardPanel.add(game, "Game");
                    MainFrame.switchTo("Game");
                    Client.setGameInstance(game);

                    // 플레이어 패널 업데이트
                    game.updatePlayerPanels();

                    // 로딩 다이얼로그 닫기
                    if (Index.getInstance().loadingDialog != null) {
                        Index.getInstance().loadingDialog.dispose();
                    }
                });
            } else {
                // playerId 또는 enemyId가 null일 경우 처리 (예: 오류 메시지 출력)
                System.err.println("Error: playerId or enemyId is null.");
            }
            return;
        }

        // 상대방의 수 처리 (상대방이 수를 두었을 때)
        if (msg.startsWith("move:") && msg.contains(":")) { // 상대방의 수를 처리
            String[] parts = msg.split(":");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            if (gameInstance != null) {
                SwingUtilities.invokeLater(() -> {
                    if (gameInstance != null) {
                        gameInstance.markOpponentMove(x, y);  // 상대방의 수 반영
                        System.out.println("[상대 수] " + x + "," + y);
                    }
                });
            }
            // 채팅 메시지 처리
            return;
        }
    
        if (gameInstance != null) {
        	gameInstance.appendChat(msg);
        }
    }
    
    public static void setGameInstance(Game game) {
    	gameInstance = game;
    }

    public static void sendGameResult(String result) {
        try {
            if (bw != null) {
                bw.write("result:" + result);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 스트림과 소켓을 닫는 메서드
    public void closeIO() {
    	try {
    		if(bw != null) bw.close();
    		if(br != null) br.close();
    		if(sock != null) sock.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public static void main(String[] args) {
        Client client = new Client();
        playerId = com.tictactalk.Index.id; 
        client.start();
    }

    public static void sendMessage(String string) {
        try {
            if (bw != null) {
                bw.write(string);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}