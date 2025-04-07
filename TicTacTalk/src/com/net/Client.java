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
import com.tictactalk.MainFrame;

public class Client extends Thread {
    public static BufferedWriter bw;
    private static Game gameInstance;
    public static String playerId;
    public static String enemyId;
    private Socket sock;

    public static void setGameInstance(Game game) {
        gameInstance = game;
    }

    public void run() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            InetAddress addr = InetAddress.getByAddress(new byte[] { (byte) 172, 30, 1, 26 });
            sock = new Socket(addr, 3000);

            OutputStream os = sock.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
            //로그인 ID 서버에 전송
            bw.write("id:"+playerId);
            bw.newLine();
            bw.flush();
            
            is = sock.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            while (true) {
                String msg;
                while ((msg = br.readLine()) != null) {
                	if(msg.startsWith("enemy:")) {
                		enemyId=msg.substring(6);
                		System.out.println("상대 플레이어: "+enemyId);
                	}else if (!msg.equals("call") && !msg.startsWith("result:")) {
                        if (gameInstance != null) {
                            gameInstance.appendChat(msg);
                        }
                    }
                    executeCommand(msg);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(String msg) {
        if (msg.equals("call")) {
            System.out.println("게임시작!");
            SwingUtilities.invokeLater(() -> {
            	MainFrame.switchTo("Game");
            });
        } else if (msg.equals("out")) {
            System.out.println("상대방 나감");
        } else if (msg.startsWith("result:")) {
            String[] parts = msg.split(":");
            if (parts.length == 3) {
                String result = parts[1];
                String targetId = parts[2];
                System.out.println("게임 결과 - " + targetId + ": " + result);
                updateGameResultInDatabase(targetId, result);
            }
        }
    }

    public static void sendGameResult(String result) {
        try {
            if (bw != null && playerId != null) {
                bw.write("result:" + result + ":" + playerId);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateGameResultInDatabase(String playerId, String result) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "scott";
        String password = "tiger";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            String sql = "";

            switch (result) {
                case "win":
                    sql = "UPDATE tttdb SET win = win + 1 WHERE id = '" + playerId + "'";
                    break;
                case "lose":
                    sql = "UPDATE tttdb SET lose = lose + 1 WHERE id = '" + playerId + "'";
                    break;
                case "draw":
                    sql = "UPDATE tttdb SET draw = draw + 1 WHERE id = '" + playerId + "'";
                    break;
            }

            if (!sql.isEmpty()) {
                stmt.executeUpdate(sql);
                System.out.println("DB 업데이트 완료: " + sql);
            }

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public void closeIO() {
    	try {
    		if(bw !=null) bw.close();
    		if(sock !=null) sock.close();
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public static void main(String[] args) {
        Client client = new Client();
        playerId = com.tictactalk.Index.id; 
        client.start();
    }
}
