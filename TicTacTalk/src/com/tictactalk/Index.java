package com.tictactalk;

import com.customs.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                loading();
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


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // X ��ư
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setBackground(null);
        closeButton.setForeground(Color.BLACK);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setFocusable(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUpDialog.dispose();
            }
        });

        mainPanel.add(closeButton);

        // ID, PW �Է� ����
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
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
        JLabel idinfoLabel = new JLabel("���̵� �ּ� 4�� �̻�");
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
        JLabel pwinfoLabel = new JLabel("���̵� �ּ� 4�� �̻�");
        
        pwPanel.add(pwLabel);
        pwPanel.add(pwField);

        inputPanel.add(idPanel);
        inputPanel.add(idinfoLabel);
        inputPanel.add(pwPanel);
        inputPanel.add(pwinfoLabel);

         
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
