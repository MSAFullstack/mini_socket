package com.tictactalk;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import com.customs.LoadingBar;
import com.customs.Round;
import com.net.Client;
import com.net.ConnectDb;

public class Index extends JPanel{
	public static Index instance;
    public static String id;
    public JPanel panel = new JPanel();
    public JDialog loadingDialog;
    
    public static Index getInstance() {
    	return instance;
    }
    
    public Index() {
    	instance = this;
        setLayout(new BorderLayout());

        panel.setBackground(Color.decode("#4ED59B"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // �ΰ�
        ImageIcon logoIcon = new ImageIcon("img/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(logoLabel);

        // ID, PW �Է�
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        idPanel.setOpaque(false);
        idPanel.setMaximumSize(new Dimension(400, 40));
        JLabel idLabel = new JLabel("ID");
        idLabel.setPreferredSize(new Dimension(60, 30));
        Round.RoundTextField idField = new Round.RoundTextField(20);
        idPanel.add(idLabel);
        idPanel.add(idField);

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

        // ��ư ����
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        Round.RoundButton signInButton = new Round.RoundButton("�α���");
        Round.RoundButton signUpButton = new Round.RoundButton("ȸ������");

        Dimension buttonSize = new Dimension(164, 56);
        signInButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        buttonPanel.add(signInButton);
        buttonPanel.add(signUpButton);
        panel.add(buttonPanel);

        // �α��� ��ư �̺�Ʈ
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = idField.getText();
                String password = new String(pwField.getPassword());
      
                ConnectDb db = new ConnectDb();
                db.connectDb();
                if (ConnectDb.login(id, password)) {
                    Client.playerId = id;
                    Client client = new Client() {
                    	@Override
    					public void run() {
    							super.run();
    							//call ���� �� loading dialog �ݰ� ȭ�� ��ȯ
    							if(loadingDialog !=null) {
    								SwingUtilities.invokeLater(() -> {
    									loadingDialog.dispose();
    									MainFrame.switchTo("Game");
    								});
    							}
    					};
                    };
                    client.start();
                    loading();
                } else {
                    JOptionPane.showMessageDialog(Index.this, "�α��� ����: ���̵� �Ǵ� ��й�ȣ�� Ʋ�Ƚ��ϴ�.", "���", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ȸ������ ��ư �̺�Ʈ
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    public void loading() {
        loadingDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "�÷��̾� ã��", true);
        loadingDialog.setSize(700, 400);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setUndecorated(true);
        loadingDialog.setOpacity(0.9f);
        loadingDialog.setLayout(new BoxLayout(loadingDialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel(id + "�� ȯ���մϴ�.", JLabel.CENTER);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loadingLabel = new JLabel("�÷��̾ ã�� ���Դϴ�...", JLabel.CENTER);
        loadingLabel.setAlignmentX(CENTER_ALIGNMENT);

        LoadingBar loadingbar = new LoadingBar();
        loadingbar.setPreferredSize(new Dimension(200, 200));
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
	    JDialog signUpDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "ȸ������", true);
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
                com.net.ConnectDb.signup(id,password);
                signUpDialog.dispose();
            }
        });

        inputPanel.add(signupButton);
        mainPanel.add(inputPanel);
        signUpDialog.add(mainPanel);

        
        signUpDialog.setVisible(true);
	}
}