package view.frame;

import app.ClientApplication;
import domain.User;
import dto.request.LoginRequest;
import network.MessageSender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener {

    LobbyFrame lobbyFrame;

    JLabel idLabel = new JLabel("아이디 ");
    JLabel pwLabel = new JLabel("비밀번호");

    JTextField idTextF = new JTextField(20);
    JPasswordField pwTextF = new JPasswordField(20);


    JButton loginBtn = new JButton("로그인");
    JButton joinBtn = new JButton("회원가입"); // 회원가입 버튼 추가

    public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;

        setLayout(null);

        idLabel.setBounds(100, 50, 100, 50);
        add(idLabel);
        idTextF.setBounds(200, 50, 300, 50);
        add(idTextF);

        pwLabel.setBounds(100, 120, 100, 50);
        add(pwLabel);
        pwTextF.setBounds(200, 120, 300, 50);
        add(pwTextF);

        loginBtn.setBounds(150, 220, 140, 50);
        loginBtn.addActionListener(this);
        add(loginBtn);

        joinBtn.setBounds(310, 220, 140, 50); // 회원가입 버튼 위치 설정
        joinBtn.addActionListener(this);
        add(joinBtn);

        setSize(600, 400);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String id = idTextF.getText();
            String pw = new String(pwTextF.getPassword());


            if (id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "user id is empty.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pw.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "user name is empty.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(id, pw);
            ClientApplication.me = user;
            ClientApplication.users.add(user);

            ClientApplication.sender.sendMessage(new LoginRequest(id, pw));

            this.dispose();
            lobbyFrame.setVisible(true);
        }

        if (e.getSource() == joinBtn) {
            new JoinFrame(); // 회원가입 창 열기
        }
    }
}

