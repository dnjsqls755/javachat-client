package view.panel;

import app.Application;
import domain.ChatRoom;
import dto.request.EnterChatRequest;
import view.frame.ChatFrame;
import view.frame.LobbyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatRoomListPanel extends JPanel {

    private static final Color BACKGROUND = new Color(245, 242, 235);
    private static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 20);
    private static final Font BODY_FONT = new Font("Malgun Gothic", Font.PLAIN, 13);

    private final DefaultListModel<ChatRoom> roomModel = new DefaultListModel<>();
    private final JList<ChatRoom> roomList = new JList<>(roomModel);

    public ChatRoomListPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildListArea(), BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel chatTitle = new JLabel("채팅");
        chatTitle.setFont(TITLE_FONT);
        titlePanel.add(chatTitle);

        JPanel icons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        icons.setOpaque(false);
        JButton newChatBtn = createGhostButton("+", "새 채팅 만들기");
        newChatBtn.addActionListener(e -> {
            if (LobbyFrame.createChatFrame != null) {
                LobbyFrame.createChatFrame.setVisible(true);
            }
        });
        icons.add(newChatBtn);
        icons.add(createGhostButton("\uD83D\uDD0E", "검색"));
        icons.add(createGhostButton("\uD83D\uDCE9", "메일"));
        icons.add(createGhostButton("\u2630", "메뉴"));

        top.add(titlePanel, BorderLayout.WEST);
        top.add(icons, BorderLayout.EAST);

        header.add(top);
        return header;
    }

    private JComponent buildListArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        roomList.setBorder(BorderFactory.createEmptyBorder());
        roomList.setBackground(Color.WHITE);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new ChatRoomRenderer());
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = roomList.locationToIndex(e.getPoint());
                if (index >= 0 && e.getClickCount() == 2) {
                    ChatRoom room = roomModel.getElementAt(index);
                    enterRoom(room.getName());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder()
        ));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    public void paintChatRoomList() {
        roomModel.clear();
        for (ChatRoom chatRoom : Application.chatRooms) {
            if (!Application.LOBBY_CHAT_NAME.equals(chatRoom.getName())) {
                roomModel.addElement(chatRoom);
            }
        }
    }

    public void addChatRoom(String chatRoomName) {
        if (Application.LOBBY_CHAT_NAME.equals(chatRoomName)) {
            return;
        }
        boolean exists = false;
        for (int i = 0; i < roomModel.size(); i++) {
            if (roomModel.get(i).getName().equals(chatRoomName)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            roomModel.addElement(new ChatRoom(chatRoomName));
        }
    }

    private void enterRoom(String chatRoomName) {
        if (Application.me == null || Application.me.getId() == null) {
            JOptionPane.showMessageDialog(null, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Application.chatPanelMap.containsKey(chatRoomName)) {
            JOptionPane.showMessageDialog(null, "이미 열려있는 채팅방입니다.", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

                ChatFrame chatFrame = new ChatFrame(chatRoomName);
        Application.chatFrameMap.put(chatRoomName, chatFrame);
        Application.chatPanelMap.put(chatRoomName, chatFrame.getChatPanel());
        Application.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.getChatRoomUserListPanel());
        Application.sender.sendMessage(new EnterChatRequest(chatRoomName, Application.me.getId()));
    }

    private JButton createGhostButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        btn.setToolTipText(tooltip);
        btn.setForeground(new Color(80, 80, 80));
        return btn;
    }

    private static class ChatRoomRenderer extends JPanel implements ListCellRenderer<ChatRoom> {
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel title = new JLabel();

        ChatRoomRenderer() {
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            setBackground(Color.WHITE);

            avatar.setPreferredSize(new Dimension(46, 46));
            avatar.setOpaque(true);
            avatar.setBackground(new Color(230, 228, 223));
            avatar.setForeground(new Color(80, 80, 80));
            avatar.setFont(new Font("Arial", Font.BOLD, 14));

            title.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
            title.setForeground(new Color(30, 30, 30));

            add(avatar, BorderLayout.WEST);
            add(title, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ChatRoom> list, ChatRoom value, int index, boolean isSelected, boolean cellHasFocus) {
            String name = value != null ? value.getName() : "";
            title.setText(name);
            avatar.setText(name.isEmpty() ? "?" : name.substring(0, 1));

            if (isSelected) {
                setBackground(new Color(245, 243, 236));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}
