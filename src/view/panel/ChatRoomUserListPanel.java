package view.panel;

import app.Application;
import domain.User;
import dto.request.ChatRoomInviteRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChatRoomUserListPanel extends JPanel {

    JPanel labelPanel = new JPanel();

    JLabel label = new JLabel("사용자 목록");
    
    String chatRoomName;
    JTextField inviteNicknameField;
    JButton inviteBtn;

    public ChatRoomUserListPanel(JFrame frame, String chatRoomName) {
        setLayout(null);
        this.chatRoomName = chatRoomName;

        label.setBounds(0, 0, 400, 50);
        add(label);

        // 채팅 메시지 영역 (스크롤)
        labelPanel.setSize(400, 150);
        labelPanel.setLayout(new GridLayout(50, 1));

        JScrollPane scrPane = new JScrollPane(labelPanel);
        scrPane.setBounds(0, 50, 400, 150);
        add(scrPane);

        // 초대 버튼 패널 (일반 채팅방만)
        if (!chatRoomName.startsWith("DM-")) {
            addInvitePanel();
        }

        frame.add(this);

        setBounds(410, 10, 400, 250);
    }

    private void addInvitePanel() {
        // 초대 패널
        JPanel invitePanel = new JPanel();
        invitePanel.setLayout(null);
        invitePanel.setBounds(0, 200, 400, 50);
        invitePanel.setBackground(new Color(240, 240, 240));
        invitePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // 라벨
        JLabel inviteLabel = new JLabel("초대:");
        inviteLabel.setBounds(5, 5, 30, 20);
        inviteLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        invitePanel.add(inviteLabel);

        // 닉네임 입력 필드
        inviteNicknameField = new JTextField();
        inviteNicknameField.setBounds(40, 5, 200, 20);
        inviteNicknameField.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        inviteNicknameField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        invitePanel.add(inviteNicknameField);

        // 초대 버튼
        inviteBtn = new JButton("초대");
        inviteBtn.setBounds(245, 5, 60, 20);
        inviteBtn.setFont(new Font("맑은 고딕", Font.BOLD, 11));
        inviteBtn.setBackground(new Color(100, 150, 200));
        inviteBtn.setForeground(Color.WHITE);
        inviteBtn.setBorderPainted(false);
        inviteBtn.setFocusPainted(false);
        inviteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String targetNickname = inviteNicknameField.getText().trim();
                if (targetNickname.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "초대할 사용자의 닉네임을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 서버에 초대 요청 전송
                ChatRoomInviteRequest inviteReq = new ChatRoomInviteRequest(chatRoomName, Application.me.getId(), targetNickname);
                Application.sender.sendMessage(inviteReq);

                // 입력 필드 초기화
                inviteNicknameField.setText("");
                JOptionPane.showMessageDialog(null, targetNickname + "님에게 초대를 보냈습니다.", "초대 발송", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        invitePanel.add(inviteBtn);

        add(invitePanel);
    }

    public void paintChatUsers(List<User> chatUsers) {
        labelPanel.removeAll();

        for (User user : chatUsers) {
            labelPanel.add(new Label(user.getNickName()));
        }

        labelPanel.revalidate();
        labelPanel.repaint();
        revalidate();
        repaint();
    }
}

