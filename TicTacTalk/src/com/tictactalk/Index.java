package com.tictactalk;

import com.customs.*;
import com.db.DBConnection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
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
		
		//로고 영역
		ImageIcon logoIcon = new ImageIcon("img/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(logoLabel);
        
        //ID, PW 영역
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
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        inputPanel.add(pwPanel);

        panel.add(inputPanel);

        
        //버튼 영역
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        Round.RoundButton signInButton = new Round.RoundButton("로그인");
        Round.RoundButton signUpButton = new Round.RoundButton("회원가입");
        
        Dimension buttonSize = new Dimension(160, 35); //(41,14)*4
        signInButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        buttonPanel.add(signInButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        buttonPanel.add(signUpButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(buttonPanel);

		
        // 로그인 버튼 클릭시
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String userId = idField.getText();
                String password = new String(pwField.getPassword());

                try {
                    DBConnection dbConnection = new DBConnection();
                    if (dbConnection.Login(userId, password)) {
                        loading(userId);
                    } else {
                        loginFailed();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "DB 오류가 발생했습니다. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        
     // 회원가입 버튼 클릭시
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });
        
        
        
        
		this.add(panel);
		this.setVisible(true);
		
	}
	
	
	//로딩창 구현
	public void loading(String userId) {
		JDialog loadingDialog = new JDialog(this, "플레이어 찾기", true);
	    loadingDialog.setSize(700, 400); // (175,100)*4
	    loadingDialog.setLocationRelativeTo(this);
	    loadingDialog.setUndecorated(true);
	    loadingDialog.setOpacity(0.9f);
	    

	    loadingDialog.setLayout(new BoxLayout(loadingDialog.getContentPane(), BoxLayout.Y_AXIS));


	    JLabel welcomeLabel = new JLabel( userId +"님 환영합니다.", JLabel.CENTER);
	    welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
	    


	    JLabel loadingLabel = new JLabel("플레이어를 찾는 중입니다...", JLabel.CENTER);
	    loadingLabel.setAlignmentX(CENTER_ALIGNMENT);

	    
	    LoadingBar loadingbar = new LoadingBar();
	    loadingbar.setPreferredSize(new Dimension(200,200));
	    loadingbar.setAlignmentX(CENTER_ALIGNMENT);
	    
	    
	    Round.RoundButton cancelButton = new Round.RoundButton("취소");
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
	    
	    
	    Timer timer = new Timer(1000, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent evt) {
	            loadingDialog.dispose();
	            Game game = new Game();
	            game.setVisible(true);
	        }
	    });
	    timer.setRepeats(false);
	    timer.start();

	    

	    loadingDialog.setVisible(true);
	}
    
	//회원가입 구현
	public void signup() {
	    JDialog signUpDialog = new JDialog(this, "회원가입", true);
	    signUpDialog.setSize(700, 400);
	    signUpDialog.setLocationRelativeTo(this);
	    signUpDialog.setUndecorated(true);
	    signUpDialog.setOpacity(0.9f);
	    signUpDialog.setLayout(new BorderLayout()); 

	    // X 버튼
	    JButton closeButton = new JButton("X");
	    closeButton.setPreferredSize(new Dimension(45, 45));
	    closeButton.setMaximumSize(new Dimension(45, 45));
	    closeButton.setBackground(null);
	    closeButton.setForeground(Color.BLACK);
	    closeButton.setFont(new Font("Arial", Font.BOLD, 25));
	    closeButton.setBorder(BorderFactory.createEmptyBorder());
	    closeButton.setFocusable(false);
	    
	    closeButton.setContentAreaFilled(false);
	    closeButton.setBorderPainted(false);
	    closeButton.setFocusPainted(false);

	    closeButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            signUpDialog.dispose();
	        }
	    });


	    JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
	    closePanel.setOpaque(false);
	    closePanel.add(closeButton);

	    
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.setOpaque(false);

	   
	    JPanel inputPanel = new JPanel();
	    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
	    inputPanel.setOpaque(false);
	    inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    // ID
	    JPanel idFieldPanel = new JPanel();
	    idFieldPanel.setLayout(new BoxLayout(idFieldPanel, BoxLayout.X_AXIS));
	    idFieldPanel.setOpaque(false);
	    
	    idFieldPanel.setMaximumSize(new Dimension(800, 40));
	    JLabel idLabel = new JLabel("ID");
	    idLabel.setPreferredSize(new Dimension(60, 30));
	    Round.RoundTextField idField = new Round.RoundTextField(20);
	    idFieldPanel.add(idLabel);
	    idFieldPanel.add(idField);

	    JLabel idInfoLabel = new JLabel("아이디 최소 4자 이상");
	    idInfoLabel.setForeground(Color.RED);
	    idInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

	    JPanel idPanel = new JPanel();
	    idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));
	    idPanel.setOpaque(false);
	    idPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    idPanel.setMaximumSize(new Dimension(400, 60));

	    idPanel.add(idFieldPanel);
	    idPanel.add(Box.createRigidArea(new Dimension(0, 5)));
	    idPanel.add(idInfoLabel);



	    // PW
	    JPanel pwFieldPanel = new JPanel();
	    pwFieldPanel.setLayout(new BoxLayout(pwFieldPanel, BoxLayout.X_AXIS));
	    pwFieldPanel.setOpaque(false);
	    
	    pwFieldPanel.setMaximumSize(new Dimension(800, 40));
	    JLabel pwLabel = new JLabel("PW");
	    pwLabel.setPreferredSize(new Dimension(60, 30));
	    Round.RoundPasswordField pwField = new Round.RoundPasswordField(20);
	    pwFieldPanel.add(pwLabel);
	    pwFieldPanel.add(pwField);

	    JLabel pwInfoLabel = new JLabel("비밀번호는 최소 8자 이상");
	    pwInfoLabel.setForeground(Color.RED);
	    pwInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

	    JPanel pwPanel = new JPanel();
	    pwPanel.setLayout(new BoxLayout(pwPanel, BoxLayout.Y_AXIS));
	    pwPanel.setOpaque(false);
	    pwPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    pwPanel.setMaximumSize(new Dimension(400, 60));

	    pwPanel.add(pwFieldPanel);
	    pwPanel.add(Box.createRigidArea(new Dimension(0, 5)));
	    pwPanel.add(pwInfoLabel);

	    // 회원가입 버튼
	    Round.RoundButton signupButton = new Round.RoundButton("회원가입");
	    signupButton.setPreferredSize(new Dimension(150, 40));
	    signupButton.setMaximumSize(new Dimension(150, 40));
	    signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    signupButton.setFocusPainted(false);
	    signupButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
	    //회원가임 체크
	    signupButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String id = idField.getText();
	            String password = new String(pwField.getPassword());
	            
	            
	            idInfoLabel.setText("");
	            pwInfoLabel.setText("");
	            
	            boolean hasError = false; 

	            
	            if (id.length() < 4) {
	                idInfoLabel.setText("아이디는 4자 이상이어야 합니다");
	                hasError = true;
	            }

	            
	            if (password.length() < 8) {
	                pwInfoLabel.setText("비밀번호는 8자 이상이어야 합니다");
	                hasError = true;
	            }

	            
	            if (hasError) {
	                return;
	            }

	            
	            DBConnection dbConnection = new DBConnection();

	            try {
	                
	                if (dbConnection.doesUserIdExist(id)) {
	                    idInfoLabel.setText("이미 존재하는 아이디입니다.");
	                    return;
	                }

	                
	                dbConnection.saveUser(id, password);
	                System.out.println("회원가입 성공: ID = " + id + ", PW = " + password);
	                signUpDialog.dispose();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        }
	    });

	    

	    inputPanel.add(idPanel);
	    inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    inputPanel.add(pwPanel);
	    inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
	    inputPanel.add(signupButton);

	    mainPanel.add(Box.createVerticalGlue());
	    mainPanel.add(inputPanel);
	    mainPanel.add(Box.createVerticalGlue());

	   
	    signUpDialog.add(closePanel, BorderLayout.NORTH);
	    signUpDialog.add(mainPanel, BorderLayout.CENTER);

	    signUpDialog.setVisible(true);
	}
	
	// 로그인 실패 창 구현
	public void loginFailed() {
	    JDialog loginFailedDialog = new JDialog(this, "로그인 실패", true);
	    loginFailedDialog.setSize(700, 400);
	    loginFailedDialog.setLocationRelativeTo(this);
	    loginFailedDialog.setUndecorated(true);
	    loginFailedDialog.setOpacity(0.9f);
	    loginFailedDialog.setLayout(new BorderLayout());

	    
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.setOpaque(false);

	    
	    JLabel failMessageLabel = new JLabel("아이디 또는 비밀번호가 맞지 않습니다.", JLabel.CENTER);
	    failMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    JLabel retryMessageLabel = new JLabel("다시 확인해주세요", JLabel.CENTER);
	    retryMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    Round.RoundButton okButton = new Round.RoundButton("확인");
	    okButton.setPreferredSize(new Dimension(150, 40));
	    okButton.setMaximumSize(new Dimension(150, 40));
	    okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    okButton.setFocusPainted(false);
	    okButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    okButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            loginFailedDialog.dispose();
	        }
	    });

	    
	    mainPanel.add(Box.createVerticalGlue());
	    mainPanel.add(failMessageLabel);
	    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    mainPanel.add(retryMessageLabel);
	    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
	    mainPanel.add(okButton);
	    mainPanel.add(Box.createVerticalGlue());

	    loginFailedDialog.add(mainPanel, BorderLayout.CENTER);

	    loginFailedDialog.setVisible(true);
	}




	

}
