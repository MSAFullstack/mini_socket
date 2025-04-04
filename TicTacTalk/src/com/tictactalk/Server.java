//패키지명 수정예정
package TicTacTalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
	static ArrayList<BufferedWriter> list=new ArrayList<>();
	Socket sock;
	
	public Server(Socket sock) {
		this.sock=sock;
	}
	@Override
	public void run() {
		InputStream is=null;
		OutputStream os=null;
		InputStreamReader isr=null;
		OutputStreamWriter osw=null;
		BufferedReader br=null;
		BufferedWriter bw=null;
		
		try {
			is=sock.getInputStream();
			os=sock.getOutputStream();
			isr=new InputStreamReader(is);
			osw=new OutputStreamWriter(os);
			br=new BufferedReader(isr);
			bw=new BufferedWriter(osw);
			list.add(bw); 	//모든 스레드의 bw가 list에 저장됨 
			String ip=sock.getInetAddress().getHostAddress();
			while(true) {
				String msg=br.readLine();
				for(int i=0; i<list.size(); i++) {	//읽은 메시지를 모두에게
					BufferedWriter temp=list.get(i);
					temp.write(ip+">"+msg);
					temp.newLine();
					temp.flush();
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ServerSocket serv=null;
		try {
			serv=new ServerSocket(3000);
			
			while(true) {
				Socket sock=serv.accept();
				Server me=new Server(sock);
				me.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//Client 한테서 종료 사인받으면 정리(예:io close())
			
		}
	}
}