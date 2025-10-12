package view.panel;

import app.ClientApplication;
import domain.ChatRoom;
import dto.request.EnterChatRequest;
import view.frame.ChatFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatRoomListPanel extends JPanel {

    JPanel labelPanel = new JPanel();

    JLabel label = new JLabel("채팅방 목록 (채팅방 이름 클릭 시 채팅방으로 이동)");

    public ChatRoomListPanel(JFrame frame) {
        setLayout(null);

        label.setBounds(0, 0, 400, 50);
        add(label);

        labelPanel.setSize(400, 200);
        labelPanel.setLayout(new GridLayout(30, 1));

        JScrollPane scrPane = new JScrollPane(labelPanel);
        scrPane.setBounds(0, 50, 400, 200);
        add(scrPane);

        frame.add(this);

        setBounds(410, 210, 400, 250);
    }

    public void paintChatRoomList() {
        labelPanel.removeAll();

        for (ChatRoom chatRoom : ClientApplication.chatRooms) {
            JLabel label = new JLabel(chatRoom.getName());
            label.addMouseListener(new ChatRoomMouseAdapter(chatRoom.getName()));
            labelPanel.add(label);
        }

        labelPanel.revalidate();
        labelPanel.repaint();
    }

    public void addChatRoomLabel(String chatRoomName) {
        JLabel label = new JLabel(chatRoomName);
        label.addMouseListener(new ChatRoomMouseAdapter(chatRoomName));
        labelPanel.add(label);

        labelPanel.revalidate();
        labelPanel.repaint();
    }

    // 채팅방 레이블을 누르면, 해당 채팅방 접속
    class ChatRoomMouseAdapter extends MouseAdapter {

        String chatRoomName;

        public ChatRoomMouseAdapter(String chatRoomName) {
            this.chatRoomName = chatRoomName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (ClientApplication.chatPanelMap.containsKey(chatRoomName)) {
                JOptionPane.showMessageDialog(null,
                        "chat room is already opened.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ChatFrame chatFrame = new ChatFrame(chatRoomName);
            ClientApplication.chatPanelMap.put(chatRoomName, chatFrame.getChatPanel()); // 채팅방 화면 관리
            ClientApplication.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.getChatRoomUserListPanel()); // 채팅방 사용자 리스트 관리

            ClientApplication.sender.sendMessage(new EnterChatRequest(chatRoomName, ClientApplication.me.getId()));
        }
    }
}
