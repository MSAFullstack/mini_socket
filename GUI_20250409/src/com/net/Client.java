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
            // �꽌踰� 二쇱냼 �꽕�젙
            InetAddress addr = InetAddress.getByAddress(new byte[] { (byte) 172, 30, 1, 72 });
            sock = new Socket(addr, 3000);

            // 異쒕젰 �뒪�듃由� �꽕�젙
            OutputStream os = sock.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
            
            // 濡쒓렇�씤 ID �쟾�넚
            bw.write("id:" + playerId);
            bw.newLine();
            bw.flush();

            // �엯�젰 �뒪�듃由� �꽕�젙
            InputStream is = sock.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // �꽌踰꾨줈遺��꽣 硫붿떆吏�瑜� 諛쏄린 �쐞�븳 諛섎났臾�
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
                // �꽌踰꾨줈 �궡 �닔 蹂대궡湲� (�삎�떇: move:row:col)
                bw.write("move:" + row + ":" + col); // "move:row:col" �쟾�넚
                bw.newLine();
                bw.flush(); // �꽌踰꾨줈 �뜲�씠�꽣 �쟾�넚
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void executeCommand(String msg) {
        // 'move' 硫붿떆吏� 泥섎━ (�옄�떊�쓽 �꽩�뿉�꽌 �닔瑜� �몢�뒗 寃쎌슦)
        if (msg.startsWith("move:") && !msg.contains(":")) { // �겢�씪�씠�뼵�듃�뿉�꽌 �옄�떊�쓽 �닔瑜� 蹂대궪 �븣
            System.out.println("너나");
            String[] parts = msg.split(":");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            
            sendMoveToServer(row, col); // �궡 �닔瑜� �꽌踰꾨줈 蹂대깂
            return;
        }

        // 'call' 硫붿떆吏� 泥섎━: �긽��諛� ID瑜� �꽕�젙�븯怨� 寃뚯엫 �떆�옉
        if (msg.startsWith("call:")) {
            enemyID = msg.substring(5, msg.length());
            // 寃뚯엫 �떆�옉 濡쒖쭅
            System.out.println("적의 id : " + enemyID);
            System.out.println("나의 id : " + playerId);
            if (playerId != null && enemyID != null) {
                SwingUtilities.invokeLater(() -> {
                    // �궡 �꽩 寃곗젙
                    boolean isMyTurn = playerId.compareTo(enemyID) < 0;

                    // Game 媛앹껜 �깮�꽦
                    Game game = new Game(isMyTurn) {
                        @Override
                        public void onGameEnd(String result) {
                            Client.sendGameResult(result);
                        }
                    };

                    // 寃뚯엫 �뙣�꼸 �꽕�젙
                    MainFrame.cardPanel.add(game, "Game");
                    MainFrame.switchTo("Game");
                    Client.setGameInstance(game);

                    // �뵆�젅�씠�뼱 �뙣�꼸 �뾽�뜲�씠�듃
                    game.updatePlayerPanels();

                    // 濡쒕뵫 �떎�씠�뼹濡쒓렇 �떕湲�
                    if (Index.getInstance().loadingDialog != null) {
                        Index.getInstance().loadingDialog.dispose();
                    }
                });
            } else {
                // playerId �삉�뒗 enemyId媛� null�씪 寃쎌슦 泥섎━ (�삁: �삤瑜� 硫붿떆吏� 異쒕젰)
                System.err.println("Error: playerId or enemyId is null.");
            }
            return;
        }

        // �긽��諛⑹쓽 �닔 泥섎━ (�긽��諛⑹씠 �닔瑜� �몢�뿀�쓣 �븣)
        if (msg.startsWith("move:") && msg.contains(":")) { // �긽��諛⑹쓽 �닔瑜� 泥섎━
            String[] parts = msg.split(":");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            if (gameInstance != null) {
                SwingUtilities.invokeLater(() -> {
                    if (gameInstance != null) {
                        gameInstance.markOpponentMove(x, y);  // �긽��諛⑹쓽 �닔 諛섏쁺
                        System.out.println("[상대 수] " + x + "," + y);
                    }
                });
            }
            // 梨꾪똿 硫붿떆吏� 泥섎━
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

    // �뒪�듃由쇨낵 �냼耳볦쓣 �떕�뒗 硫붿꽌�뱶
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