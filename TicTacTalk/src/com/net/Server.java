package com.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {
    private static final int PORT = 3000;

    // 사용자 리스트
    private static final List<ClientHandler> clients = new ArrayList<>();

    private final Socket socket;
    private String clientId;
    private BufferedReader br;
    private BufferedWriter bw;

    public Server(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 입출력 설정
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 1. 최초 ID 수신
            String initMsg;
            while ((initMsg = br.readLine()) != null) {
                if (initMsg.startsWith("ID ")) {
                    clientId = initMsg.substring(3).trim();
                    System.out.println("ID 등록됨: " + clientId);
                    synchronized (clients) {
                        clients.add(new ClientHandler(clientId, bw));
                        if (clients.size() % 2 == 0) {
                            broadcast("call"); // 2명 매칭되면 게임 시작
                            System.out.println("두 명 매칭됨. 게임 시작.");
                        }
                    }
                    break;
                }
            }

            // 2. 이후 일반 메시지 수신 처리
            String msg;
            while ((msg = br.readLine()) != null) {
                if (msg.startsWith("MOVE")) {
                    broadcast("MOVE " + msg.substring(5)); // MOVE 3 등
                } else {
                    broadcast(clientId + ": " + msg); // 채팅 메시지
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 연결 종료 시 클라이언트 제거
            synchronized (clients) {
                clients.removeIf(handler -> handler.id.equals(clientId));
            }
            System.out.println("연결 종료: " + clientId);
        }
    }

    // 전체 사용자에게 메시지 전송
    private void broadcast(String msg) {
        synchronized (clients) {
            for (ClientHandler handler : clients) {
                try {
                    handler.bw.write(msg);
                    handler.bw.newLine();
                    handler.bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 클라이언트 정보 보관용 내부 클래스
    static class ClientHandler {
        String id;
        BufferedWriter bw;

        ClientHandler(String id, BufferedWriter bw) {
            this.id = id;
            this.bw = bw;
        }
    }

    // 메인 실행부
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버 시작됨. 포트: " + PORT);

            while (true) {
                Socket sock = serverSocket.accept();
                System.out.println("새 연결 수신: " + sock.getInetAddress());
                Server thread = new Server(sock);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
