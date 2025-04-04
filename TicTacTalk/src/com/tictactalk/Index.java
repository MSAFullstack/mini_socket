package com.tictactalk;

import com.customs.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Index extends JFrame{
	
	public JPanel panel = new JPanel();
		
	
	public Index() {
		this.setTitle("TicTacTalk");
		this.setSize(852,756); //(213, 189)*4
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		panel.setBackground(Color.decode("#4ED59B"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		//�ΰ� ����
		ImageIcon logoIcon = new ImageIcon("img/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(logoLabel);
        
        //ID, PW ����
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        //ID
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        idPanel.setOpaque(false);
        idPanel.setMaximumSize(new Dimension(400, 40));
        JLabel idLabel = new JLabel("ID");
        idLabel.setPreferredSize(new Dimension(60, 30));
        Round.RoundTextField idField = new Round.RoundTextField(20);
        idPanel.add(idLabel);
        idPanel.add(idField);

        //PW
        JPanel pwPanel = new JPanel();
        pwPanel.setLayout(new BoxLayout(pwPanel, BoxLayout.X_AXIS));
        pwPanel.setOpaque(false);
        pwPanel.setMaximumSize(new Dimension(400, 40));
        JLabel pwLabel = new JLabel("PW");
        pwLabel.setPreferredSize(new Dimension(60, 30));
        Round.RoundPasswordField pwField = new Round.RoundPasswordField(20);
        pwPanel.add(pwLabel);
        pwPanel.add(pwField);

        idPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        pwPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        inputPanel.add(idPanel);
        inputPanel.add(pwPanel);

        panel.add(inputPanel);

        
        //��ư ����
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        Round.RoundButton signInButton = new Round.RoundButton("�α���");
        Round.RoundButton signUpButton = new Round.RoundButton("ȸ������");
        
        Dimension buttonSize = new Dimension(164, 56); //(41,14)*4
        signInButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        buttonPanel.add(signInButton);
        buttonPanel.add(signUpButton);

        panel.add(buttonPanel);

		
        // �α��� ��ư Ŭ����
        
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String id = new String(idField.getText());
                String password = new String(pwField.getPassword());
                System.out.println(id+"<<>>"+password);
                // �α��� �ߺ� üũ 
                
                ArrayList<String> list=new ArrayList<>();
        		String sql="select * from tttdb";
        		
//        		String sql="desc emp";
        		String url="jdbc:oracle:thin:@172.30.1.71:1521:xe";
        		Properties props=new Properties();
        		props.setProperty("user", "scott");
        		props.setProperty("password", "tiger");

        		Connection conn=null;
        		Statement stmt=null;
        		java.sql.ResultSet rs=null;
        		try {
        			Class.forName("oracle.jdbc.driver.OracleDriver");
        			conn=DriverManager.getConnection(url, props);
        			stmt=conn.createStatement();
        			rs=stmt.executeQuery(sql);
        			id = new String(idField.getText());
                    password = new String(pwField.getPassword());
        			while(rs.next()) {
        				
//        				list.add(Integer.parseInt(rs.getObject(1).toString()));
        				int i=1;
        				list.add(rs.getObject(i).toString());
        				list.add(rs.getObject(i).toString());
        				i++;
        				if(rs.getObject(1).equals(id) && rs.getObject(2).equals(password)) {
        					System.out.print("�Ϸ�!!!!!!!!!!!!!!!!!");
        					loading();
        				}else {
        					System.out.println("���̵�� ��й�ȣ �ٽ� Ȯ��");
        				}
//        				System.out.print(rs.getObject(2)+"\t");
        			}
        			for(String a : list) {
        				System.out.println(a+"<");
        			}
        			System.out.println(list.toString());
        		} catch (SQLException e1) {
        			e1.printStackTrace();
        		} catch (ClassNotFoundException e1) {
        			e1.printStackTrace();
        		} finally {
        			try {
        				if(rs!=null)rs.close();
        				if(stmt!=null)stmt.close();
        				if(conn!=null)conn.close();
        			} catch (SQLException e1) {
        				e1.printStackTrace();
        			}
        		}
                
            }
        });
        
      // ȸ������ ��ư Ŭ����
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });
  
		this.add(panel);
		this.setVisible(true);
		
	}
	
	
	//�ε�â ����
	public void loading() {
		JDialog loadingDialog = new JDialog(this, "�÷��̾� ã��", true);
	    loadingDialog.setSize(700, 400); // (175,100)*4
	    loadingDialog.setLocationRelativeTo(this);
	    loadingDialog.setUndecorated(true);
	    loadingDialog.setOpacity(0.9f);
	    

	    loadingDialog.setLayout(new BoxLayout(loadingDialog.getContentPane(), BoxLayout.Y_AXIS));


	    JLabel welcomeLabel = new JLabel("Player1�� ȯ���մϴ�.", JLabel.CENTER);
	    welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);


	    JLabel loadingLabel = new JLabel("�÷��̾ ã�� ���Դϴ�...", JLabel.CENTER);
	    loadingLabel.setAlignmentX(CENTER_ALIGNMENT);

	    
	    LoadingBar loadingbar = new LoadingBar();
	    loadingbar.setPreferredSize(new Dimension(200,200));
	    loadingbar.setAlignmentX(CENTER_ALIGNMENT);
	    
	    
	    Round.RoundButton cancelButton = new Round.RoundButton("���");
	    cancelButton.setAlignmentX(CENTER_ALIGNMENT);
	    
	    cancelButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            loadingDialog.dispose();
	        }
	    });

	    loadingDialog.add(welcomeLabel);
	    loadingDialog.add(Box.createVerticalStrut(10));
	    loadingDialog.add(loadingLabel);
	    loadingDialog.add(Box.createVerticalStrut(10));
	    loadingDialog.add(loadingbar);
	    loadingDialog.add(Box.createVerticalStrut(10));
	    loadingDialog.add(cancelButton);

	    loadingDialog.setVisible(true);
	}
    
	//ȸ������ ����
	public void signup() {
	    JDialog signUpDialog = new JDialog(this, "ȸ������", true);
	    signUpDialog.setSize(700, 400);
	    signUpDialog.setLocationRelativeTo(this);
	    signUpDialog.setUndecorated(true);
	    signUpDialog.setOpacity(0.9f);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // X ��ư
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setBackground(null); // ��� ���ֱ�
        closeButton.setForeground(Color.BLACK); // ���� ���� ���������� ����
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBorder(BorderFactory.createEmptyBorder()); // �׵θ� ���ֱ�
        closeButton.setFocusable(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUpDialog.dispose();  // X ��ư Ŭ�� �� ���̾�α� ����
            }
        });

        mainPanel.add(closeButton);

        // ID, PW �Է� ����
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        // ID �г�
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        idPanel.setOpaque(false);
        idPanel.setMaximumSize(new Dimension(400, 40));
        JLabel idLabel = new JLabel("ID");
        idLabel.setPreferredSize(new Dimension(60, 30));
        Round.RoundTextField idField = new Round.RoundTextField(20);
        idPanel.add(idLabel);
        idPanel.add(idField);

        // PW �г�
        JPanel pwPanel = new JPanel();
        pwPanel.setLayout(new BoxLayout(pwPanel, BoxLayout.X_AXIS));
        pwPanel.setOpaque(false);
        pwPanel.setMaximumSize(new Dimension(400, 40));
        JLabel pwLabel = new JLabel("PW");
        pwLabel.setPreferredSize(new Dimension(60, 30));
        Round.RoundPasswordField pwField = new Round.RoundPasswordField(20);
        pwPanel.add(pwLabel);
        pwPanel.add(pwField);


        inputPanel.add(idPanel);
        inputPanel.add(pwPanel);

        JButton signupButton = new JButton("ȸ������");
        signupButton.setPreferredSize(new Dimension(150, 40));
        signupButton.setBackground(new Color(51, 153, 255));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //ȸ������ Ŭ����
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = new String(idField.getText());
                String password = new String(pwField.getPassword());
                System.out.println("ID: " + id + ", PW: " + password);
                signUpDialog.dispose();
            }
        });

        inputPanel.add(signupButton);
        mainPanel.add(inputPanel);
        signUpDialog.add(mainPanel);

        
        signUpDialog.setVisible(true);
	}


	

}
