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
    private static final Font SECTION_FONT = new Font("Malgun Gothic", Font.BOLD, 13);
    private static final Font BODY_FONT = new Font("Malgun Gothic", Font.PLAIN, 13);

    private final DefaultListModel<ChatRoom> publicRoomModel = new DefaultListModel<>();
    private final DefaultListModel<ChatRoom> directRoomModel = new DefaultListModel<>();
    private final JList<ChatRoom> publicRoomList = new JList<>(publicRoomModel);
    private final JList<ChatRoom> directRoomList = new JList<>(directRoomModel);

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
        JPanel area = new JPanel();
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        // 일반 채팅방 섹션
        JPanel publicSection = new JPanel(new BorderLayout());
        publicSection.setOpaque(false);
        publicSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel publicLabel = new JLabel("일반 채팅");
        publicLabel.setFont(SECTION_FONT);
        publicLabel.setForeground(new Color(90, 90, 90));
        publicLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        publicRoomList.setBorder(BorderFactory.createEmptyBorder());
        publicRoomList.setBackground(Color.WHITE);
        publicRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        publicRoomList.setCellRenderer(new ChatRoomRenderer());
        publicRoomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = publicRoomList.locationToIndex(e.getPoint());
                if (index >= 0 && e.getClickCount() == 2) {
                    ChatRoom room = publicRoomModel.getElementAt(index);
                    enterRoom(room.getName());
                }
            }
        });

        JScrollPane publicScroll = new JScrollPane(publicRoomList);
        publicScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder()
        ));
        publicScroll.getVerticalScrollBar().setUnitIncrement(12);

        publicSection.add(publicLabel, BorderLayout.NORTH);
        publicSection.add(publicScroll, BorderLayout.CENTER);

        // 1:1 채팅 섹션
        JPanel directSection = new JPanel(new BorderLayout());
        directSection.setOpaque(false);
        directSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel directLabel = new JLabel("1:1 채팅");
        directLabel.setFont(SECTION_FONT);
        directLabel.setForeground(new Color(90, 90, 90));
        directLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        directRoomList.setBorder(BorderFactory.createEmptyBorder());
        directRoomList.setBackground(Color.WHITE);
        directRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        directRoomList.setCellRenderer(new DirectChatRenderer());
        directRoomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = directRoomList.locationToIndex(e.getPoint());
                if (index >= 0 && e.getClickCount() == 2) {
                    ChatRoom room = directRoomModel.getElementAt(index);
                    enterRoom(room.getName());
                }
            }
        });

        JScrollPane directScroll = new JScrollPane(directRoomList);
        directScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder()
        ));
        directScroll.getVerticalScrollBar().setUnitIncrement(12);

        directSection.add(directLabel, BorderLayout.NORTH);
        directSection.add(directScroll, BorderLayout.CENTER);

        area.add(publicSection);
        area.add(Box.createVerticalStrut(10));
        area.add(directSection);

        return area;
    }

    public void paintChatRoomList() {
        publicRoomModel.clear();
        directRoomModel.clear();
        
        if (Application.me == null) return;
        
        for (ChatRoom chatRoom : Application.chatRooms) {
            String roomName = chatRoom.getName();
            
            // 로비는 제외
            if (Application.LOBBY_CHAT_NAME.equals(roomName)) {
                continue;
            }
            
            // 1:1 채팅방 (DM-로 시작)
            if (roomName.startsWith("DM-")) {
                // 현재 사용자가 포함된 1:1 채팅방만 표시
                if (roomName.contains(Application.me.getId())) {
                    directRoomModel.addElement(chatRoom);
                }
            } else {
                // 일반 채팅방
                publicRoomModel.addElement(chatRoom);
            }
        }
    }

    public void addChatRoom(String chatRoomName) {
        if (Application.LOBBY_CHAT_NAME.equals(chatRoomName)) {
            return;
        }
        
        if (Application.me == null) return;
        
        // 1:1 채팅방
        if (chatRoomName.startsWith("DM-")) {
            // 현재 사용자가 포함된 경우만 추가
            if (chatRoomName.contains(Application.me.getId())) {
                boolean exists = false;
                for (int i = 0; i < directRoomModel.size(); i++) {
                    if (directRoomModel.get(i).getName().equals(chatRoomName)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    directRoomModel.addElement(new ChatRoom(chatRoomName));
                }
            }
        } else {
            // 일반 채팅방
            boolean exists = false;
            for (int i = 0; i < publicRoomModel.size(); i++) {
                if (publicRoomModel.get(i).getName().equals(chatRoomName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                publicRoomModel.addElement(new ChatRoom(chatRoomName));
            }
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

    private static class DirectChatRenderer extends JPanel implements ListCellRenderer<ChatRoom> {
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel title = new JLabel();

        DirectChatRenderer() {
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            setBackground(Color.WHITE);

            avatar.setPreferredSize(new Dimension(46, 46));
            avatar.setOpaque(true);
            avatar.setBackground(new Color(200, 220, 240));
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
            
            // DM-userId1-userId2 형식에서 상대방 ID 추출
            if (name.startsWith("DM-") && Application.me != null) {
                String[] parts = name.substring(3).split("-");
                String otherUserId = "";
                
                if (parts.length == 2) {
                    otherUserId = parts[0].equals(Application.me.getId()) ? parts[1] : parts[0];
                }
                
                // 상대방의 닉네임 찾기
                String displayName = otherUserId;
                for (domain.User u : Application.users) {
                    if (u.getId().equals(otherUserId)) {
                        displayName = u.getNickName() != null && !u.getNickName().isEmpty() 
                                    ? u.getNickName() : otherUserId;
                        break;
                    }
                }
                
                // 친구 목록에서도 확인
                if (displayName.equals(otherUserId)) {
                    for (domain.User u : Application.friends) {
                        if (u.getId().equals(otherUserId)) {
                            displayName = u.getNickName() != null && !u.getNickName().isEmpty() 
                                        ? u.getNickName() : otherUserId;
                            break;
                        }
                    }
                }
                
                title.setText(displayName);
                avatar.setText(displayName.isEmpty() ? "?" : displayName.substring(0, 1));
            } else {
                title.setText(name);
                avatar.setText(name.isEmpty() ? "?" : name.substring(0, 1));
            }

            if (isSelected) {
                setBackground(new Color(235, 245, 255));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}
