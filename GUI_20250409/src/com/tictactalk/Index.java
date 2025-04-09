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

        // 濡쒓퀬
        ImageIcon logoIcon = new ImageIcon("img/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(logoLabel);

        // ID, PW �엯�젰
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
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        inputPanel.add(pwPanel);
        panel.add(inputPanel);

        // 踰꾪듉 �쁺�뿭
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        Round.RoundButton signInButton = new Round.RoundButton("로그인");
        Round.RoundButton signUpButton = new Round.RoundButton("회원가입");

        Dimension buttonSize = new Dimension(160, 35);
        signInButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        buttonPanel.add(signInButton);
        inputPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        buttonPanel.add(signUpButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(buttonPanel);

        // 濡쒓렇�씤 踰꾪듉 �씠踰ㅽ듃
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
    							//call �닔�떊 �썑 loading dialog �떕怨� �솕硫� �쟾�솚
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
                	loginFailed();
                }
            }
        });

        // �쉶�썝媛��엯 踰꾪듉 �씠踰ㅽ듃
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    public void loading() {
        loadingDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "�뵆�젅�씠�뼱 李얘린", true);
        loadingDialog.setSize(700, 400);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setUndecorated(true);
        loadingDialog.setOpacity(0.9f);
        loadingDialog.setLayout(new BoxLayout(loadingDialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel(id + "님 환영합니다.", JLabel.CENTER);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loadingLabel = new JLabel("플레이어를 찾는 중...", JLabel.CENTER);
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
    
    
    //�쉶�썝媛��엯 援ы쁽
  	public void signup() {
  	    JDialog signUpDialog = new JDialog((Frame)null, "회원가입", true);
  	    
  	    signUpDialog.setSize(700, 400);
  	    signUpDialog.setLocationRelativeTo(this);
  	    signUpDialog.setUndecorated(true);
  	    signUpDialog.setOpacity(0.9f);
  	    signUpDialog.setLayout(new BorderLayout()); 

  	    // X 踰꾪듉
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

  	    // �쉶�썝媛��엯 踰꾪듉
  	    Round.RoundButton signupButton = new Round.RoundButton("회원가입");
  	    signupButton.setPreferredSize(new Dimension(150, 40));
  	    signupButton.setMaximumSize(new Dimension(150, 40));
  	    signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
  	    signupButton.setFocusPainted(false);
  	    signupButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //�쉶�썝媛��엯 �겢由��떆
  	  signupButton.addActionListener(new ActionListener() {
  	      @Override
  	      public void actionPerformed(ActionEvent e) {
  	          String id = idField.getText();
  	          String password = new String(pwField.getPassword());

  	          idInfoLabel.setText("");
  	          pwInfoLabel.setText("");

  	          boolean hasError = false;

  	          if (id.length() < 4) {
  	              idInfoLabel.setText("아이디는 4자 이상이어야 합니다.");
  	              hasError = true;
  	          }


  	          if (password.length() < 8) {
  	              pwInfoLabel.setText("비밀번호는 8자 이상이어야 합니다.");
  	              hasError = true;
  	          }

  	          if (hasError) {
  	              return;
  	          }


  	          try {
  	              if (com.net.ConnectDb.map.containsKey(id)) {
  	                  idInfoLabel.setText("이미 존재하는 아이디입니다.");
  	                  return;
  	              }

  	              com.net.ConnectDb.signup(id, password);
  	              System.out.println("회원가입 성공: ID = " + id + ", PW = " + password);
  	              signUpDialog.dispose();

  	          } catch (Exception ex) {
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
	
	// 濡쒓렇�씤 �떎�뙣 李� 援ы쁽
	public void loginFailed() {
		JDialog loginFailedDialog = new JDialog((Frame) null, "로그인 실패", true);
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