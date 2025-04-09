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
	public static Index instance;	//���� �ν��Ͻ�
    public static String id;
    public JPanel panel = new JPanel();
    public JDialog loadingDialog;

    public static Index getInstance() {
    	return instance;
    }
    
    public Index() {
    	instance = this;	//�ڱ� �ڽ��� ���� ������ ����
        setLayout(new BorderLayout());

        panel.setBackground(Color.decode("#4ED59B"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 로고
        ImageIcon logoIcon = new ImageIcon("img/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(logoLabel);

        // ID, PW 입력
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

        // 버튼 영역
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        Round.RoundButton signInButton = new Round.RoundButton("로그인");
        Round.RoundButton signUpButton = new Round.RoundButton("회원가입");

        Dimension buttonSize = new Dimension(164, 56);
        signInButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        buttonPanel.add(signInButton);
        buttonPanel.add(signUpButton);
        panel.add(buttonPanel);

        // 로그인 버튼 이벤트
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
    							//call 수신 후 loading dialog 닫고 화면 전환
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
                    JOptionPane.showMessageDialog(Index.this, "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.", "경고", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 회원가입 버튼 이벤트
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    public void loading() {
        loadingDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "플레이어 찾기", true);
        loadingDialog.setSize(700, 400);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setUndecorated(true);
        loadingDialog.setOpacity(0.9f);
        loadingDialog.setLayout(new BoxLayout(loadingDialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel(id + "님 환영합니다.", JLabel.CENTER);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loadingLabel = new JLabel("플레이어를 찾는 중입니다...", JLabel.CENTER);
        loadingLabel.setAlignmentX(CENTER_ALIGNMENT);

        LoadingBar loadingbar = new LoadingBar();
        loadingbar.setPreferredSize(new Dimension(200, 200));
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
        loadingDialog.setVisible(true);

    }
    
	//회원가입 구현
	public void signup() {
	    JDialog signUpDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "ȸ������", true);
	    signUpDialog.setSize(700, 400);
	    signUpDialog.setLocationRelativeTo(this);
	    signUpDialog.setUndecorated(true);
	    signUpDialog.setOpacity(0.9f);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // X 버튼
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setBackground(null); // 배경 없애기
        closeButton.setForeground(Color.BLACK); // 글자 색을 검정색으로 설정
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBorder(BorderFactory.createEmptyBorder()); // 테두리 없애기
        closeButton.setFocusable(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUpDialog.dispose();  // X 버튼 클릭 시 다이얼로그 종료
            }
        });

        mainPanel.add(closeButton);

        // ID, PW 입력 영역
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        // ID 패널
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        idPanel.setOpaque(false);
        idPanel.setMaximumSize(new Dimension(400, 40));
        JLabel idLabel = new JLabel("ID");
        idLabel.setPreferredSize(new Dimension(60, 30));
        Round.RoundTextField idField = new Round.RoundTextField(20);
        idPanel.add(idLabel);
        idPanel.add(idField);

        // PW 패널
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

        JButton signupButton = new JButton("회원가입");
        signupButton.setPreferredSize(new Dimension(150, 40));
        signupButton.setBackground(new Color(51, 153, 255));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //회원가입 클릭시
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