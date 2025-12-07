package view.panel;

import app.Application;
import domain.User;
import dto.request.ChatRoomInviteRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
            JLabel lbl = new JLabel(user.getNickName());
            lbl.setOpaque(true);
            lbl.setBackground(new Color(250, 250, 250));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        openUserProfileDialog(user);
                    }
                }
            });
            labelPanel.add(lbl);
        }

        labelPanel.revalidate();
        labelPanel.repaint();
        revalidate();
        repaint();
    }

    private void openUserProfileDialog(User user) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "프로필", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);

        JLabel avatar = new JLabel(user.getNickName() == null || user.getNickName().isEmpty() ? "?" : user.getNickName().substring(0, 1), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(100, 100));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(180, 200, 230));
        avatar.setForeground(Color.DARK_GRAY);
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 32f));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(160, 180, 210), 2));

        // 서버에 프로필 이미지 요청 (응답은 MessageReceiver에서 avatar에 세팅)
        Application.currentProfileDialog = dialog;
        Application.currentProfileAvatar = avatar;
        Application.sender.sendMessage(new dto.request.ProfileImageRequest(user.getId()));

        JPanel info = new JPanel(new GridLayout(5, 1, 6, 8));
        info.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel nickLabel = new JLabel("닉네임: " + user.getNickName());
        JLabel idLabel = new JLabel("아이디: " + user.getId());
        JLabel nameLabel = new JLabel("이름: -");
        JLabel genderLabel = new JLabel("성별: -");
        JLabel birthLabel = new JLabel("생년월일: -");
        for (JLabel l : new JLabel[]{nickLabel, idLabel, nameLabel, genderLabel, birthLabel}) {
            l.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        }
        info.add(nickLabel);
        info.add(idLabel);
        info.add(nameLabel);
        info.add(genderLabel);
        info.add(birthLabel);

        Application.currentProfileNameLabel = nameLabel;
        Application.currentProfileGenderLabel = genderLabel;
        Application.currentProfileBirthLabel = birthLabel;

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dialog.dispose());
        actions.add(closeBtn);

        dialog.add(avatar, BorderLayout.NORTH);
        dialog.add(info, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}

