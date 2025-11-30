package view.frame;

import app.Application;
import domain.ChatRoom;
import domain.User;
import dto.request.AdminBanRequest;
import dto.request.AdminForceExitRequest;
import dto.request.AdminForceLogoutRequest;
import dto.request.AdminInitRequest;
import dto.request.AdminMessageDeleteRequest;
import dto.request.AdminMessageSearchRequest;
import dto.request.AdminRoomDeleteRequest;
import dto.response.AdminMessageSearchResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.SwingUtilities;

public class AdminFrame extends JFrame implements ActionListener {

    private final DefaultTableModel userModel = new DefaultTableModel(new Object[]{"아이디", "닉네임", "역할", "접속중", "차단됨"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex >= 3) return Boolean.class;
            return String.class;
        }
    };
    private final JTable userTable = new JTable(userModel);
    private final JTextField forceRoomField = new JTextField(12);

    private final DefaultTableModel roomModel = new DefaultTableModel(new Object[]{"채팅방", "인원"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable roomTable = new JTable(roomModel);

    private final DefaultTableModel messageModel = new DefaultTableModel(new Object[]{"메시지ID", "채팅방", "닉네임", "내용", "전송시간"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Long.class : String.class;
        }
    };
    private final JTable messageTable = new JTable(messageModel);
    private final JTextField searchNicknameField = new JTextField(12);
    private final JTextField searchRoomField = new JTextField(12);

    public AdminFrame() {
        super("관리자 대시보드");
        setSize(920, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("사용자 관리", buildUserPanel());
        tabs.addTab("채팅방 관리", buildRoomPanel());
        tabs.addTab("메시지 관리", buildMessagePanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(userTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton refreshBtn = new JButton("새로고침");
        JButton logoutBtn = new JButton("강제 로그아웃");
        JButton banBtn = new JButton("차단/해제");
        JButton forceExitBtn = new JButton("강제 퇴장");
        controls.add(refreshBtn);
        controls.add(logoutBtn);
        controls.add(banBtn);
        controls.add(new JLabel("채팅방:"));
        controls.add(forceRoomField);
        controls.add(forceExitBtn);

        refreshBtn.setActionCommand("refresh_users");
        logoutBtn.setActionCommand("force_logout");
        banBtn.setActionCommand("toggle_ban");
        forceExitBtn.setActionCommand("force_exit");

        refreshBtn.addActionListener(this);
        logoutBtn.addActionListener(this);
        banBtn.addActionListener(this);
        forceExitBtn.addActionListener(this);

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(roomTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton refreshBtn = new JButton("새로고침");
        JButton deleteBtn = new JButton("채팅방 삭제");
        refreshBtn.setActionCommand("refresh_rooms");
        deleteBtn.setActionCommand("delete_room");
        refreshBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        controls.add(refreshBtn);
        controls.add(deleteBtn);

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(messageTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filters.add(new JLabel("닉네임:"));
        filters.add(searchNicknameField);
        filters.add(new JLabel("채팅방:"));
        filters.add(searchRoomField);
        JButton searchBtn = new JButton("검색");
        JButton deleteBtn = new JButton("메시지 삭제");
        searchBtn.setActionCommand("search_messages");
        deleteBtn.setActionCommand("delete_message");
        searchBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        filters.add(searchBtn);
        filters.add(deleteBtn);

        panel.add(filters, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "refresh_users":
            case "refresh_rooms":
                Application.sender.sendMessage(new AdminInitRequest());
                break;
            case "force_logout":
                String userId = getSelectedUserId();
                if (userId != null) {
                    Application.sender.sendMessage(new AdminForceLogoutRequest(userId));
                }
                break;
            case "toggle_ban":
                int row = userTable.getSelectedRow();
                if (row >= 0) {
                    String targetId = userModel.getValueAt(row, 0).toString();
                    boolean banned = Boolean.TRUE.equals(userModel.getValueAt(row, 4));
                    Application.sender.sendMessage(new AdminBanRequest(targetId, !banned));
                } else {
                    showWarning("먼저 사용자를 선택하세요.");
                }
                break;
            case "force_exit":
                String selectedUser = getSelectedUserId();
                if (selectedUser == null) break;
                String roomName = forceRoomField.getText().trim();
                if (roomName.isEmpty()) {
                    showWarning("채팅방 이름을 입력하세요.");
                    break;
                }
                Application.sender.sendMessage(new AdminForceExitRequest(selectedUser, roomName));
                break;
            case "delete_room":
                int roomRow = roomTable.getSelectedRow();
                if (roomRow >= 0) {
                    String room = roomModel.getValueAt(roomRow, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(this, "\"" + room + "\" 채팅방을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Application.sender.sendMessage(new AdminRoomDeleteRequest(room));
                    }
                } else {
                    showWarning("먼저 채팅방을 선택하세요.");
                }
                break;
            case "search_messages":
                Application.sender.sendMessage(new AdminMessageSearchRequest(
                        searchNicknameField.getText().trim(),
                        searchRoomField.getText().trim()
                ));
                break;
            case "delete_message":
                int msgRow = messageTable.getSelectedRow();
                if (msgRow >= 0) {
                    Object idObj = messageModel.getValueAt(msgRow, 0);
                    long msgId = (idObj instanceof Number) ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                    int confirm = JOptionPane.showConfirmDialog(this, "메시지 ID " + msgId + "를 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Application.sender.sendMessage(new AdminMessageDeleteRequest(msgId));
                        // 삭제 후 자동으로 검색 결과 새로고침
                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Application.sender.sendMessage(new AdminMessageSearchRequest(
                                    searchNicknameField.getText().trim(),
                                    searchRoomField.getText().trim()
                                ));
                            }
                        }, 500);
                    }
                } else {
                    showWarning("먼저 메시지를 선택하세요.");
                }
                break;
            default:
                break;
        }
    }

    private String getSelectedUserId() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            showWarning("먼저 사용자를 선택하세요.");
            return null;
        }
        return userModel.getValueAt(row, 0).toString();
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "알림", JOptionPane.WARNING_MESSAGE);
    }

    public void updateUsers(List<User> users) {
        SwingUtilities.invokeLater(() -> {
            userModel.setRowCount(0);
            for (User u : users) {
                userModel.addRow(new Object[]{u.getId(), u.getNickName(), u.getRole(), u.isOnline(), u.isBanned()});
            }
        });
    }

    public void updateChatRooms(List<ChatRoom> rooms) {
        SwingUtilities.invokeLater(() -> {
            roomModel.setRowCount(0);
            for (ChatRoom r : rooms) {
                roomModel.addRow(new Object[]{r.getName(), r.getMemberCount()});
            }
        });
    }

    public void updateMessages(List<AdminMessageSearchResponse.AdminMessage> messages) {
        SwingUtilities.invokeLater(() -> {
            messageModel.setRowCount(0);
            for (AdminMessageSearchResponse.AdminMessage m : messages) {
                messageModel.addRow(new Object[]{m.id, m.roomName, m.nickname, m.content, m.sentAt});
            }
        });
    }
}
