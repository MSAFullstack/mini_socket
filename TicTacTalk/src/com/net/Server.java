package com.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends Thread {
    static int cnt = 0;
    static ArrayList<BufferedWriter> chat = new ArrayList<>();
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static Map<Socket, String> playerIds=new HashMap<>();
    private static List<Socket> waitingList = new ArrayList<>();
    
    Socket sock;

    public Server(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            is = sock.getInputStream();
            os = sock.getOutputStream();
            isr = new InputStreamReader(is);
            osw = new OutputStreamWriter(os);
            br = new BufferedReader(isr);
            bw = new BufferedWriter(osw);
            chat.add(bw);

            String ip = sock.getInetAddress().getHostAddress();

            while (true) {
                String msg = br.readLine();
                if (msg == null) break;

                if (msg.startsWith("id:")) {
                    String id = msg.substring(3);
                    playerIds.put(sock, id);
                    waitingList.add(sock);

                    // 2명이 쌓이면 매칭
                    if (waitingList.size() >= 2) {
                        Socket s1 = waitingList.remove(0);
                        Socket s2 = waitingList.remove(0);

                        String id1 = playerIds.get(s1);
                        String id2 = playerIds.get(s2);

                        PrintWriter out1 = new PrintWriter(s1.getOutputStream(), true);
                        PrintWriter out2 = new PrintWriter(s2.getOutputStream(), true);

                        out1.println("enemy:" + id2);
                        out2.println("enemy:" + id1);

                        out1.println("call");
                        out2.println("call");

                        System.out.println("매칭 완료: " + id1 + " vs " + id2);
                    }

                } else if (msg.startsWith("result:")) {
                    processGameResult(msg);
                }

                for (BufferedWriter temp : chat) {
                    temp.write(ip + ">" + msg);
                    temp.newLine();
                    temp.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processGameResult(String msg) {
        // msg format: result:win:playerId
        String[] parts = msg.split(":");
        if (parts.length != 3) return;

        String result = parts[1];
        String playerId = parts[2];

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
                System.out.println("서버 DB 업데이트 완료: " + sql);
            }

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    private static void notifyClients() {
		for(PrintWriter writer: clientWriters) {
			writer.println("call");
		}
	}
    public static void main(String[] args) {
    	ServerSocket serv=null;
		try {
			serv=new ServerSocket(3000);
			
			while(true) {
				Socket sock=serv.accept();
				cnt++;
				Server me=new Server(sock);
				PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
				clientWriters.add(out);
				if(cnt!=0&&cnt%2==0) {
					notifyClients();
					System.out.println("두명 접속");
				}
				me.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//Client 한테서 종료 사인받으면 정리(예:io close())
			
		}
    }
}