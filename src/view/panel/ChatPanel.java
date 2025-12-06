package view.panel;

import app.Application;
import dto.request.MessageRequest;
import dto.type.MessageType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatPanel extends JPanel implements ActionListener {

    String chatRoomName;

    JTextPane chatTextPane = new JTextPane();  // JTextArea 대신 JTextPane 사용
    StyledDocument doc;

    JTextField msgTextF = new JTextField(50);
    JButton sendBtn = new JButton("전송");
    JComboBox<String> whisperCombo = new JComboBox<>();
    DefaultComboBoxModel<String> whisperModel = new DefaultComboBoxModel<>();


    // 카카오톡 스타일 색상
    private static final Color BACKGROUND_COLOR = new Color(178, 199, 217);  // 연한 파란색 배경
    private static final Color MY_MESSAGE_COLOR = new Color(255, 235, 51);   // 노란색 (내 메시지)
    private static final Color OTHER_MESSAGE_COLOR = Color.WHITE;            // 흰색 (상대방 메시지)
    private static final Color SYSTEM_MESSAGE_COLOR = new Color(100, 100, 100);  // 회색 (시스템 메시지)
    private static final Color WHISPER_COLOR = new Color(220, 240, 255); // 귓속말 배경

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatPanel(JFrame frame, String chatRoomName) {
        setLayout(null);
        this.chatRoomName = chatRoomName;

        // 배경색 설정
        setBackground(BACKGROUND_COLOR);

        // 채팅 메시지 영역 (스크롤)
        chatTextPane.setEditable(false);
        chatTextPane.setBackground(BACKGROUND_COLOR);
        chatTextPane.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        doc = chatTextPane.getStyledDocument();
        
        JScrollPane scrPane = new JScrollPane(chatTextPane);
        scrPane.setBounds(10, 10, 380, 430);
        scrPane.setBorder(BorderFactory.createEmptyBorder());
        scrPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrPane);


        // 귓속말 콤보박스
        whisperCombo.setModel(whisperModel);
        whisperCombo.setBounds(10, 450, 90, 40);
        whisperCombo.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        whisperCombo.addItem("전체");
        add(whisperCombo);

        // 메시지 입력 필드
        msgTextF.setBounds(110, 450, 130, 40);
        msgTextF.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        msgTextF.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(msgTextF);

        // 이모티콘 버튼
        JButton emojiBtn = new JButton("😀");
        emojiBtn.setBounds(250, 450, 40, 40);
        emojiBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        emojiBtn.setBackground(Color.WHITE);
        emojiBtn.setBorderPainted(true);
        emojiBtn.setFocusPainted(false);
        emojiBtn.addActionListener(e -> showEmojiPicker());
        add(emojiBtn);

        // 전송 버튼
        sendBtn.setBounds(300, 450, 90, 40);
        sendBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        sendBtn.setBackground(new Color(255, 235, 51));
        sendBtn.setForeground(new Color(60, 30, 30));
        sendBtn.setBorderPainted(false);
        sendBtn.setFocusPainted(false);
        sendBtn.addActionListener(this);
        add(sendBtn);

        frame.add(this);
        setBounds(10, 10, 400, 500);
    }

    public void addMessage(MessageType messageType, String userName, String message) {
        try {
            String timeStamp = timeFormat.format(new Date());
            switch (messageType) {
                case ENTER:
                case EXIT:
                    addSystemMessage(message);
                    break;
                case CHAT:
                    addChatMessage(userName, message, timeStamp, userName.equals(Application.me.getNickName()));
                    break;
                case WHISPER:
                    addWhisperMessage(userName, message, timeStamp);
                    break;
            }
            chatTextPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 기존 저장된 기록(과거 시간 포함)을 추가하기 위한 메서드
    public void addHistoryMessage(String userName, String message, String time) {
        try {
            boolean isMyMessage = Application.me != null && userName.equals(Application.me.getNickName());
            addChatMessage(userName, message, time, isMyMessage);
            chatTextPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addSystemMessage(String message) throws BadLocationException {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setForeground(attrs, SYSTEM_MESSAGE_COLOR);
        StyleConstants.setFontSize(attrs, 11);
        StyleConstants.setItalic(attrs, true);
        
        int start = doc.getLength();
        doc.insertString(doc.getLength(), "\n" + message + "\n", attrs);
        doc.setParagraphAttributes(start, doc.getLength() - start, attrs, false);
    }
    
    private void addChatMessage(String userName, String message, String time, boolean isMyMessage) throws BadLocationException {
        SimpleAttributeSet nameAttrs = new SimpleAttributeSet();
        SimpleAttributeSet msgAttrs = new SimpleAttributeSet();
        SimpleAttributeSet timeAttrs = new SimpleAttributeSet();
        
        StyleConstants.setFontSize(nameAttrs, 11);
        StyleConstants.setFontSize(msgAttrs, 13);
        StyleConstants.setFontSize(timeAttrs, 9);
        StyleConstants.setForeground(timeAttrs, new Color(120, 120, 120));
        
        doc.insertString(doc.getLength(), "\n", null);
        
        if (isMyMessage) {
            // 내 메시지 (오른쪽 정렬)
            StyleConstants.setAlignment(msgAttrs, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setBackground(msgAttrs, MY_MESSAGE_COLOR);
            
            int start = doc.getLength();
            String fullMsg = time + "  " + message + " ";
            doc.insertString(doc.getLength(), fullMsg + "\n", msgAttrs);
            doc.setParagraphAttributes(start, fullMsg.length(), msgAttrs, false);
            
        } else {
            // 상대방 메시지 (왼쪽 정렬)
            StyleConstants.setAlignment(msgAttrs, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(nameAttrs, new Color(60, 60, 60));
            StyleConstants.setBackground(msgAttrs, OTHER_MESSAGE_COLOR);
            
            int start = doc.getLength();
            doc.insertString(doc.getLength(), userName + "\n", nameAttrs);
            
            String fullMsg = message + "  " + time;
            doc.insertString(doc.getLength(), fullMsg + "\n", msgAttrs);
            doc.setParagraphAttributes(start, doc.getLength() - start, msgAttrs, false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = msgTextF.getText().trim();
        if (!message.isEmpty()) {
            String nickname = Application.me.getNickName();
            if (nickname == null || nickname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "닉네임 정보가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String target = (String) whisperCombo.getSelectedItem();
            if (target != null && !"전체".equals(target)) {
                Application.sender.sendMessage(new MessageRequest(MessageType.WHISPER, chatRoomName, nickname + "," + target, message));
                System.out.println("[귓속말 전송] 방: " + chatRoomName + ", 대상: " + target + ", 내용: " + message);
            } else {
                Application.sender.sendMessage(new MessageRequest(MessageType.CHAT, chatRoomName, nickname, message));
                System.out.println("[전송] 방: " + chatRoomName + ", 내용: " + message);
            }
        }
        msgTextF.setText("");
        msgTextF.requestFocus();
    }

    // 귓속말 메시지 표시
    private void addWhisperMessage(String userField, String message, String time) throws BadLocationException {
        String[] users = userField.split(",", 2);
        String from = users.length > 0 ? users[0].trim() : "";
        String to = users.length > 1 ? users[1].trim() : "";
        String me = Application.me != null ? Application.me.getNickName() : "";

        boolean amSender = from.equals(me);
        boolean amReceiver = to.equals(me);

        String label;
        if (amSender) {
            label = to.isEmpty() ? "대상에게 귓속말: " : to + "님에게 귓속말: ";
        } else if (amReceiver) {
            label = from + "님으로부터 귓속말: ";
        } else {
            // fallback 표시
            label = from + " → " + to + " 귓속말: ";
        }

        SimpleAttributeSet nameAttrs = new SimpleAttributeSet();
        SimpleAttributeSet msgAttrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(nameAttrs, 11);
        StyleConstants.setFontSize(msgAttrs, 13);
        StyleConstants.setBackground(msgAttrs, WHISPER_COLOR);
        StyleConstants.setItalic(msgAttrs, true);

        doc.insertString(doc.getLength(), "\n", null);
        doc.insertString(doc.getLength(), label, nameAttrs);
        doc.insertString(doc.getLength(), message + "  " + time + "\n", msgAttrs);
    }

    // 채팅방 참여자 목록으로 귓속말 대상 갱신
    public void updateWhisperTargets(List<String> users) {
        whisperModel.removeAllElements();
        whisperModel.addElement("전체");
        for (String u : users) {
            if (!u.equals(Application.me.getNickName())) {
                whisperModel.addElement(u);
            }
        }
    }

    private void showEmojiPicker() {
        String[][] emojiData = {
            {"😀", "웃음"}, {"😂", "기쁨"}, {"😍", "하트"}, {"😢", "울음"}, {"😡", "화남"}, {"😎", "멋짐"},
            {"😱", "비명"}, {"😊", "행복"}, {"😉", "윙크"}, {"😭", "통곡"}, {"😘", "키스"}, {"😐", "무표정"},
            {"❤️", "하트"}, {"👍", "좋아"}, {"👎", "싫어"}, {"👏", "박수"}, {"🙏", "기도"}, {"🎉", "축하"},
            {"🎂", "케이크"}, {"🎁", "선물"}, {"⭐", "별"}, {"💩", "똥"}, {"🐶", "강아지"}, {"🐱", "고양이"}
        };
        
        Color[] colors = {
            new Color(255, 220, 100), new Color(255, 200, 150), new Color(255, 150, 200), 
            new Color(200, 220, 255), new Color(255, 100, 100), new Color(100, 100, 100),
            new Color(255, 200, 100), new Color(255, 240, 150), new Color(255, 220, 180),
            new Color(220, 230, 255), new Color(255, 180, 200), new Color(200, 200, 200),
            new Color(255, 100, 100), new Color(180, 220, 180), new Color(180, 180, 220),
            new Color(220, 200, 150), new Color(230, 220, 200), new Color(255, 200, 100),
            new Color(255, 210, 210), new Color(200, 180, 150), new Color(255, 255, 150),
            new Color(160, 120, 80), new Color(220, 200, 180), new Color(255, 220, 230)
        };
        
        JPanel emojiPanel = new JPanel(new GridLayout(4, 6, 5, 5));
        emojiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < emojiData.length; i++) {
            final String emoji = emojiData[i][0];
            final String label = emojiData[i][1];
            
            JButton btn = new JButton(label);
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(50, 50));
            btn.setBackground(colors[i]);
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            btn.addActionListener(e -> {
                String nickname = Application.me.getNickName();
                if (nickname != null && !nickname.isEmpty()) {
                    Application.sender.sendMessage(new MessageRequest(MessageType.CHAT, chatRoomName, nickname, emoji));
                    SwingUtilities.getWindowAncestor(emojiPanel).dispose();
                }
            });
            emojiPanel.add(btn);
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "이모티콘 선택", true);
        dialog.add(emojiPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    

}

