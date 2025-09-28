package view.frame;

import app.Application;
import domain.User;
import dto.request.LoginRequest;
import network.MessageSender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener {

    LobbyFrame lobbyFrame;

    JLabel idLabel = new JLabel("아이디 ");
    JLabel nameLabel = new JLabel("이름 ");

    JTextField idTextF = new JTextField(20);
    JTextField nameTextF = new JTextField(20);

    JButton loginBtn = new JButton("로그인");
    JButton joinBtn = new JButton("회원가입"); // 회원가입 버튼 추가

    public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;

        setLayout(null);

        idLabel.setBounds(100, 50, 100, 50);
        add(idLabel);
        idTextF.setBounds(200, 50, 300, 50);
        add(idTextF);

        nameLabel.setBounds(100, 120, 100, 50);
        add(nameLabel);
        nameTextF.setBounds(200, 120, 300, 50);
        add(nameTextF);

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
            String name = nameTextF.getText();

            if (id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "user id is empty.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "user name is empty.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(id, name);
            Application.me = user;
            Application.users.add(user);

            Application.sender.sendMessage(new LoginRequest(id, name));

            this.dispose();
            lobbyFrame.setVisible(true);
        }

        if (e.getSource() == joinBtn) {
            new JoinFrame(); // 회원가입 창 열기
        }
    }
}

