package com.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Server extends Thread{
	private static final int PORT = 3000;
	
	//사용자 리스트, 최초 ID 수신, 전체 사용자에게 메시지 전송
	private static final List<ClientHandler> clients = new ArrayList<>();
	
	private final Socket socket;
    static List<BufferedWriter> chat = new ArrayList<>();
    static Map<String, String> enemyMap = new HashMap<>();
    static Map<Socket, String> playerIds=new HashMap<>();
    
    private String clientId;
    private BufferedWriter bw;
    private BufferedReader br;
    
    public Server(Socket socket) {
    	this.socket = socket;
    }
    @Override
    public void run() {
    	try {
    		br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//1. 최초 ID 수신
			String initMsg;
			while ((initMsg=br.readLine()) != null) {
				if((initMsg.startsWith("ID "))) {
					clientId = initMsg.substring(3).trim();
					System.out.println("ID 등록됨: "+clientId);
					synchronized (clients) {
						clients.add(new ClientHandler(clientId, bw));
						if(clients.size() %2 ==0) {
							broadcast("call");
							ClientHandler p1 = clients.get(clients.size()-2);
							ClientHandler p2 = clients.get(clients.size()-1);
							
							p1.bw.write("enemy:"+p2.id);
							p1.bw.newLine();
							p1.bw.flush();
							
							p2.bw.write("enemy:"+p1.id);
							p2.bw.newLine();
							p2.bw.flush();
							
//							p1.bw.write("your_turn");
//							p1.bw.newLine();
//							p1.bw.flush();
							
							System.out.println("두 명 매칭. 게임 시작.");
					    }	//2명 매치되면 게임 시작
					}
					break;
				}
			}
			
			//이후 일반 메시지 수신 처리
			String msg;
			while((msg = br.readLine()) != null) {
				if(msg.startsWith("MOVE ")) {	
					broadcast("MOVE " + msg.substring(5));
					System.out.println("[서버] MOVE 처리됨: "+msg);
				} else {	//채팅메시지
					broadcast(clientId+": "+msg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//연결 종료 시 클라이언트 제거
			synchronized (clients) {
				clients.removeIf(handler -> handler.id.equals(clientId));
			}
			System.out.println("연결 종료: "+clientId);
		}
    }
    //전체 사용자에게 메시지 전송
    private void broadcast(String msg) {
    	synchronized (clients) {
    		for(ClientHandler handler : clients) {
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
    //클라이언트 정보 보관용 내부 클래스
    static class ClientHandler {
    	String id;
    	BufferedWriter bw;
    	
    	ClientHandler(String id, BufferedWriter bw){
    		this.id=id;
    		this.bw=bw;
    	}
    }
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
        	System.out.println("서버 시작 됨.");

            while (true) {
                Socket sock = serverSocket.accept();
                System.out.println("새 연결 수신"+sock.getInetAddress());
                Server thread = new Server(sock);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void processGameResult(String id, String result) {
    	System.out.println("[게임종료]"+id+"-"+result);
    	String url = "jdbc:oracle:thin:@localhost:1521:xe";
    	String user = "scott";
    	String password = "tiger";
    	
    	List<String> validResults = Arrays.asList("win","draw","lose");
    	if (!validResults.contains(result)) {
    		System.out.println("[DB연결] 게임 결과 전적: "+result);
    		return;
    	}
    	
    	String sql="update TTTDB set "+result+" = "+result+" + 1 where id=?";
    	
    	try {
    		Class.forName("oracle.jdbc.driver.OracleDriver");
    		Connection conn = DriverManager.getConnection(url, user, password);
    		PreparedStatement stmt = conn.prepareStatement(sql);
    		
    		stmt.setString(1, id);
    		int updated = stmt.executeUpdate();
    		if(updated>0) {
    			System.out.println("[DB 업데이트] "+id+"- "+result);
    		}else {
    			System.out.println("[DB 업데이트 실패] : "+id);
    		}
    	} catch (ClassNotFoundException | SQLException e) {
    		System.out.println("[DB 연결 실패] " + e.getMessage());
    		e.printStackTrace();
    	}
    }
}