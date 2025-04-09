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
    public static String enemyId;
    public static Runnable onGameStart;
    private Socket sock;
    
    public static void setGame(Game game) { gameInstance = game; 
    System.out.println("[gameInstance 등록 완료]");}
    public static void setPlayerId(String id) { playerId  = id; }
    public static String getPlayerId() { return playerId; }

    @Override
    public void run() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        ConnectDb db=new ConnectDb();
        db.connectDb();
        System.out.println("[Client] DB connect ok. 전적 수:" + ConnectDb.map.size());
        try {
            InetAddress addr = InetAddress.getByAddress(new byte[] { (byte) 172, 30, 1, 2 });
            sock = new Socket(addr, 3000);

            OutputStream os = sock.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
            //로그인 ID 서버에 전송
            bw.write("ID "+playerId);
            bw.newLine();
            bw.flush();
            
            is = sock.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String msg;
            while ((msg = br.readLine()) != null) {
                handleMessage(msg);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleMessage(String msg) {
    	if(msg.startsWith("enemy:")) {
    		enemyId = msg.substring(msg.indexOf(":")+1);
    		System.out.println("[상대 ID] "+enemyId);
    		return;
    	}
    	
        if (msg.equals("call")) {
            System.out.println("게임 시작!");
            //중복 방지용: 기존  Game 패널 있으면 제거
            SwingUtilities.invokeLater(()-> {
            	Game game = new Game();
            	setGame(game);
            	MainFrame.cardPanel.add(game, "Game");
            	MainFrame.switchTo("Game");
            	if(Index.getInstance().loadingDialog != null) {
            		Index.getInstance().loadingDialog.dispose();
            	}
            	if (onGameStart != null) onGameStart.run();
            });
        } else if(msg.startsWith("MOVE ")) {
        	int index = Integer.parseInt(msg.split(" ")[1]);
        	if(gameInstance != null) gameInstance.markOpponentMove(index);
//        } else if(msg.equals("your_turn")){
//        	gameInstance.setMyTurn(true);
        } else {
        	if (gameInstance != null) gameInstance.displayMessage(msg);
        }
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

	public static void sendMessage(String string) {
		System.out.println("[Client 전송] "+string);
		try {
			if(bw!=null) {
				bw.write(string);
				bw.newLine();
				bw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}