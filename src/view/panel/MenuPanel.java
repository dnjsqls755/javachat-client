package view.panel;

import app.Application;
import dto.request.ExitChatRequest;
import dto.request.LogoutRequest;
import view.frame.LobbyFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {

    String chatRoomName;

    JButton createChatBtn = new JButton("새로운 채팅방 생성");

    JButton exitBtn = new JButton("나가기");
    
    JButton backBtn = new JButton("뒤로가기");
    
    JButton logoutBtn = new JButton("로그아웃");

    public MenuPanel(JFrame frame, String chatRoomName) {
        setLayout(null);

        this.chatRoomName = chatRoomName;

        createChatBtn.setBounds(15, 10, 170, 35);
        createChatBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("chat room create btn clicked");
                LobbyFrame.createChatFrame.setVisible(true);
            }
        });
        add(createChatBtn);
        createChatBtn.setVisible(false); // 로비 채팅방, 생성된 채팅방에 따라 버튼 show, hide

        logoutBtn.setBounds(200, 10, 185, 35);
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("logout btn clicked");
                
                // 로그아웃 요청 전송
                Application.sender.sendMessage(new LogoutRequest(Application.me.getId()));
                
                // 로비 프레임 숨기기
                if (frame instanceof LobbyFrame) {
                    frame.setVisible(false);
                    
                    // 채팅 패널 및 사용자 정보 초기화
                    Application.chatPanelMap.clear();
                    Application.chatRoomUserListPanelMap.clear();
                    Application.users.clear();
                    Application.chatRooms.clear();
                    Application.me = null;
                    
                    // 로그인 화면 다시 표시
                    new view.frame.LoginFrame((LobbyFrame)frame);
                }
            }
        });
        add(logoutBtn);
        logoutBtn.setVisible(false);

        backBtn.setBounds(15, 10, 180, 35);
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("back btn clicked - 뒤로가기 (채팅방에서 나가지 않음)");
                
                // 채팅 화면만 닫기 (서버에 EXIT 전송 안 함)
                Application.chatPanelMap.remove(chatRoomName);
                Application.chatRoomUserListPanelMap.remove(chatRoomName);
                
                frame.dispose();
            }
        });
        add(backBtn);
        backBtn.setVisible(false);

        exitBtn.setBounds(205, 10, 180, 35);
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("chat room exit btn clicked - 채팅방 나가기");
                System.out.println("chat room = [" + chatRoomName + "] user exit.");

                // 서버에 채팅방 나가기 요청 전송
                Application.sender.sendMessage(new ExitChatRequest(chatRoomName, Application.me.getId()));
                
                // 채팅 화면 닫기
                Application.chatPanelMap.remove(chatRoomName);
                Application.chatRoomUserListPanelMap.remove(chatRoomName);
                
                frame.dispose();
            }
        });
        add(exitBtn);
        exitBtn.setVisible(false);

        frame.add(this);

        setBounds(410, 460, 400, 50);
        setVisible(true);
    }

    public void setCreateChatBtnVisible(boolean bool) {
        createChatBtn.setVisible(bool);
    }
    
    public void setLogoutBtnVisible(boolean bool) {
        logoutBtn.setVisible(bool);
    }
    
    public void setBackBtnVisible(boolean bool) {
        backBtn.setVisible(bool);
    }

    public void setExitBtnVisible(boolean bool) {
        exitBtn.setVisible(bool);
    }
}
