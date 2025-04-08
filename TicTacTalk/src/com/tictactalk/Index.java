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
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        inputPanel.add(pwPanel);

        panel.add(inputPanel);

        
        //��ư ����
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        Round.RoundButton signInButton = new Round.RoundButton("�α���");
        Round.RoundButton signUpButton = new Round.RoundButton("ȸ������");
        
        Dimension buttonSize = new Dimension(160, 35); //(41,14)*4
        signInButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        buttonPanel.add(signInButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        buttonPanel.add(signUpButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(buttonPanel);

		
        // �α��� ��ư Ŭ����
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
                    JOptionPane.showMessageDialog(null, "DB ������ �߻��߽��ϴ�. �ٽ� �õ����ּ���.", "����", JOptionPane.ERROR_MESSAGE);
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
	public void loading(String userId) {
		JDialog loadingDialog = new JDialog(this, "�÷��̾� ã��", true);
	    loadingDialog.setSize(700, 400); // (175,100)*4
	    loadingDialog.setLocationRelativeTo(this);
	    loadingDialog.setUndecorated(true);
	    loadingDialog.setOpacity(0.9f);
	    

	    loadingDialog.setLayout(new BoxLayout(loadingDialog.getContentPane(), BoxLayout.Y_AXIS));


	    JLabel welcomeLabel = new JLabel( userId +"�� ȯ���մϴ�.", JLabel.CENTER);
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
    
	//ȸ������ ����
	public void signup() {
	    JDialog signUpDialog = new JDialog(this, "ȸ������", true);
	    signUpDialog.setSize(700, 400);
	    signUpDialog.setLocationRelativeTo(this);
	    signUpDialog.setUndecorated(true);
	    signUpDialog.setOpacity(0.9f);
	    signUpDialog.setLayout(new BorderLayout()); 

	    // X ��ư
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

	    JLabel idInfoLabel = new JLabel("���̵� �ּ� 4�� �̻�");
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

	    JLabel pwInfoLabel = new JLabel("��й�ȣ�� �ּ� 8�� �̻�");
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

	    // ȸ������ ��ư
	    Round.RoundButton signupButton = new Round.RoundButton("ȸ������");
	    signupButton.setPreferredSize(new Dimension(150, 40));
	    signupButton.setMaximumSize(new Dimension(150, 40));
	    signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    signupButton.setFocusPainted(false);
	    signupButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
	    //ȸ������ üũ
	    signupButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String id = idField.getText();
	            String password = new String(pwField.getPassword());
	            
	            
	            idInfoLabel.setText("");
	            pwInfoLabel.setText("");
	            
	            boolean hasError = false; 

	            
	            if (id.length() < 4) {
	                idInfoLabel.setText("���̵�� 4�� �̻��̾�� �մϴ�");
	                hasError = true;
	            }

	            
	            if (password.length() < 8) {
	                pwInfoLabel.setText("��й�ȣ�� 8�� �̻��̾�� �մϴ�");
	                hasError = true;
	            }

	            
	            if (hasError) {
	                return;
	            }

	            
	            DBConnection dbConnection = new DBConnection();

	            try {
	                
	                if (dbConnection.doesUserIdExist(id)) {
	                    idInfoLabel.setText("�̹� �����ϴ� ���̵��Դϴ�.");
	                    return;
	                }

	                
	                dbConnection.saveUser(id, password);
	                System.out.println("ȸ������ ����: ID = " + id + ", PW = " + password);
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
	
	// �α��� ���� â ����
	public void loginFailed() {
	    JDialog loginFailedDialog = new JDialog(this, "�α��� ����", true);
	    loginFailedDialog.setSize(700, 400);
	    loginFailedDialog.setLocationRelativeTo(this);
	    loginFailedDialog.setUndecorated(true);
	    loginFailedDialog.setOpacity(0.9f);
	    loginFailedDialog.setLayout(new BorderLayout());

	    
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.setOpaque(false);

	    
	    JLabel failMessageLabel = new JLabel("���̵� �Ǵ� ��й�ȣ�� ���� �ʽ��ϴ�.", JLabel.CENTER);
	    failMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    JLabel retryMessageLabel = new JLabel("�ٽ� Ȯ�����ּ���", JLabel.CENTER);
	    retryMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    Round.RoundButton okButton = new Round.RoundButton("Ȯ��");
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
