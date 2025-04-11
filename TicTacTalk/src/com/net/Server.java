package com.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import com.sun.xml.internal.org.jvnet.mimepull.CleanUpExecutorFactory;

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
		String url = "jdbc:oracle:thin:@172.30.1.2:1521:xe";
		String user = "scott";
		String password = "tiger";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection(url, user, password);
			// 결과에 따른 DB 업데이트
			// 1. 승자/패자/무승부 카운트 업데이트
			String sql = "UPDATE TTTDB SET " + result + " = " + result + " + 1 WHERE id=?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, id);
			stmt.executeUpdate();

			// 2. rating 업데이트
			String ratingSql = "";
			switch (result) {
			case "win":
				// 승자 +30, 패자 -20
				ratingSql = "UPDATE TTTDB SET rating = rating + 30 WHERE id = ?";
				PreparedStatement winStmt = conn.prepareStatement(ratingSql);
				winStmt.setString(1, id);
				winStmt.executeUpdate();
				break;
			case "lose":
				//패자 -20, 승자 +30
				ratingSql = "UPDATE TTTDB SET rating = rating - 20 WHERE id = ?";
				PreparedStatement loseStmt2 = conn.prepareStatement(ratingSql);
				loseStmt2.setString(1, id);
				loseStmt2.executeUpdate();
				break;
			case "draw":
				String drawSql = "UPDATE TTTDB SET draw = draw + 1 WHERE id = ?";
				PreparedStatement drawStmt1 = conn.prepareStatement(drawSql);
				drawStmt1.setString(1, id);
				drawStmt1.executeUpdate();
				break;
			}
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			serverSocket = new ServerSocket(3000);

			while (true) {
				Socket sock = serverSocket.accept();
				new Thread(() -> handleClient(sock)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleClient(Socket sock) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock.getInputStream(),StandardCharsets.UTF_8));
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8));

			String idLine = br.readLine();
			String id = idLine.substring(idLine.indexOf(":") + 1);
			userMap.put(id, bw);
			socketMap.put(id, sock);
			playerIds.put(sock, id);
			chat.add(bw);

			// 클라이언트가 게임 대기 중일 때 대기 리스트에 추가
			waitingList.add(sock);

			// 2명 이상 접속 시 게임 시작 사인 'call' 보내기
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
				// 게임 진행(좌표) 처리
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
				//게임 결과 처리
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
		} catch (java.net.SocketException se) {
			System.out.println("클라이언트 연결 끊김: " + sock);
			//필요 시 로그인 출력 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				String id = playerIds.get(sock);
				if (id != null) {
					BufferedWriter writer = userMap.get(id);
					if (writer != null) {
						chat.remove(writer);
						try {
							writer.close();
						} catch (IOException ignore) {
						}
					}

					userMap.remove(id);
					enemyMap.remove(id);
					socketMap.remove(id);
				}
				playerIds.remove(sock);
				waitingList.remove(sock);
				activePlayers.remove(sock);

				if (br != null)
					try {
						br.close();
					} catch (IOException ignore) {
					}
				if (bw != null)
					try {
						bw.close();
					} catch (IOException ignore) {
					}
				if (sock != null && sock.isClosed())
					try {
						sock.close();
					} catch (IOException ignore) {
					}

				System.out.println("클라이언트 정상 종료" + id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isValidMove(String moveMessage) {
		// moveMessage 유효 형식 확인, 포맷 -> move:row:col
		String[] parts = moveMessage.split(":");
		if (parts.length != 3) {
			return false; // move:row:col 형식 아닌 경우
		}

		try {
			int row = Integer.parseInt(parts[1]);
			int col = Integer.parseInt(parts[2]);

			// 행열 유효 확인
			if (row >= 0 && row <= 2 && col >= 0 && col <= 2) {
				return true; //
			}
		} catch (NumberFormatException e) {
			return false;
		}

		return false; // 행열 범위 벗어남 
	}
}
