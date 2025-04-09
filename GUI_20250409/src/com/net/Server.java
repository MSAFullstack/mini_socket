package com.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Server {
    static ServerSocket serverSocket;

    static List<BufferedWriter> chat = new ArrayList<>();
    static Map<String, BufferedWriter> userMap = new HashMap<>();
    static Map<String, String> enemyMap = new HashMap<>();
    static Map<String, Socket> socketMap = new HashMap<>();
    static Map<Socket, String> playerIds = new HashMap<>();
    static Queue<Socket> waitingList = new LinkedList<>();
    static Set<Socket> activePlayers = new HashSet<>(); // Active players tracking

    private static void processGameResult(String id, String result) {
        System.out.println(id + "-" + result);
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "scott";
        String password = "tiger";

        List<String> validResults = Arrays.asList("win", "draw", "lose");
        if (!validResults.contains(result)) {
            System.out.println("[유효하지 않음] 결과 값이 올바르지 않음: " + result);
            return;
        }

        String sql = "UPDATE TTTDB SET " + result + " = " + result + " + 1 WHERE id=?";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("DB 업데이트 성공: " + id + " - " + result);
            } else {
                System.out.println("DB 업데이트 실패: " + id);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[DB 오류] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(3000);
            System.out.println("[서버] 서버 시작");

            while (true) {
                Socket sock = serverSocket.accept();
                new Thread(() -> handleClient(sock)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket sock) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

            String idLine = br.readLine();
            String id = idLine.substring(idLine.indexOf(":") + 1);
            userMap.put(id, bw);
            socketMap.put(id, sock);
            playerIds.put(sock, id);
            chat.add(bw);

            // 클라이언트가 게임 대기 중일 때 대기 리스트에 추가
            waitingList.add(sock);
            System.out.println("[�굹] " + id);

            // 2명 이상 접속시 게임 시작 사인 'call' 보내기
            if (waitingList.size() >= 2) {
                Socket s1 = waitingList.poll();
                Socket s2 = waitingList.poll();

                String id1 = playerIds.get(s1);
                String id2 = playerIds.get(s2);

                BufferedWriter out1 = userMap.get(id1);
                BufferedWriter out2 = userMap.get(id2);

                enemyMap.put(id1, id2);
                enemyMap.put(id2, id1);

                out1.write("call:" + id2);
                out2.write("call:" + id1);
                out1.newLine();
                out2.newLine();
                out1.flush();
                out2.flush();

                // 두 명의 플레이어가 게임을 시작했으므로 이들을 activePlayers에 추가
                activePlayers.add(s1);
                activePlayers.add(s2);
            }

            // 클라이언트 메시지 처리
            String msg;
            while ((msg = br.readLine()) != null) {
                String senderId = playerIds.get(sock);
                System.out.println("[" + senderId + "] " + msg);

                if (msg.startsWith("move:")) {
                    // Validate the move message
                    if (isValidMove(msg)) {
                        String enemyId = enemyMap.get(senderId);
                        BufferedWriter enemyWriter = userMap.get(enemyId);
                        if (enemyWriter != null) {
                            enemyWriter.write(msg);
                            enemyWriter.newLine();
                            enemyWriter.flush();
                        }
                    } else {
                        System.out.println("Invalid move received from " + senderId + ": " + msg);
                    }
                    continue;
                }

                if (msg.startsWith("result:")) {
                    String[] parts = msg.split(":");
                    if (parts.length == 2) {
                        String result = parts[1];
                        processGameResult(senderId, result);
                    }
                    continue;
                }

                // 채팅 메시지 처리
                for (BufferedWriter writer : chat) {
                    writer.write(senderId + ": " + msg);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	// 클라이언트 연결 종료 시 리소스 정리
            try {
                sock = socketMap.get(playerIds.get(sock));
                if (sock != null) {
                    chat.remove(sock);
                    sock.close();
                 // 게임이 끝난 후 대기 리스트에 다시 추가할 수 있도록 처리
                    if (!activePlayers.contains(sock)) {
                        waitingList.add(sock);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isValidMove(String moveMessage) {
        // Validate move message format: move:row:col
        String[] parts = moveMessage.split(":");
        if (parts.length != 3) {
            return false; // Not in the format of "move:row:col"
        }
        
        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            
            // Check if the row and column are within valid bounds (0-2 for a 3x3 grid)
            if (row >= 0 && row <= 2 && col >= 0 && col <= 2) {
                return true; // Valid move
            }
        } catch (NumberFormatException e) {
            // If parsing fails, the message is invalid
            return false;
        }

        return false; // Invalid move if row/col are out of bounds
    }
}
