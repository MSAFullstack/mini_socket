//패키지명 수정예정
package TicTacTalk;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//종료버튼을 누르면 서버에 종료 메시지 주고, server에서 내 인덱스를 찾아서 지우거나
//server에서 list의 value값에 해당하는 bw 제거
//한 사람한테만?
//자료구조
//set-> 소켓 중복 X
//map-> key를 가질 수 있으니 key를 ip를 배정하면 client 2개를 켜도 하나만 작동

public class Client extends Frame implements ActionListener {
	static TextArea ta;
	TextField tf;
	static Socket sock=null;
	static BufferedWriter bw;
	
	public Client() {
		setLayout(new BorderLayout());
		ta=new TextArea();
		ta.setEditable(false);
		tf=new TextField();
		add(ta,BorderLayout.CENTER);
		add(tf,BorderLayout.SOUTH);
		tf.addActionListener(this);
		setBounds(600,200,200,300);
		setVisible(true);
		ta.setFocusable(false);
	}
	
	public static void main(String[] args) {
		new Client();
		InputStream is=null;
		InputStreamReader isr=null;
		BufferedReader br=null;
		
		try {
			InetAddress addr=null;
			addr=InetAddress.getByAddress(new byte[] {(byte)172,30,1,71});
			sock=new Socket(addr,3000);
			
			OutputStream os=null;
			OutputStreamWriter osw=null;
			try {
				os=sock.getOutputStream();
				osw=new OutputStreamWriter(os);
				bw=new BufferedWriter(osw);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			is=sock.getInputStream();
			isr=new InputStreamReader(is);
			br=new BufferedReader(isr);
			while(true) {
				String msg=br.readLine();
				ta.setText(ta.getText()+msg+"\n");
				ta.revalidate();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
//		} finally {		//dispose 할 때 close 해야 함
//				try {
//					if(sock!=null) sock.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String before=ta.getText();
		String msg=e.getActionCommand();
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		tf.setText(" ");
		tf.setText("");
		ta.revalidate();
	}
}