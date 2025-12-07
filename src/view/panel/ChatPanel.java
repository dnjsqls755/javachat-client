package view.panel;

import app.Application;
import dto.request.MessageRequest;
import dto.type.MessageType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ChatPanel extends JPanel implements ActionListener {

    String chatRoomName;

    JTextPane chatTextPane = new JTextPane();  // JTextArea 대신 JTextPane 사용
    StyledDocument doc;

    JTextField msgTextF = new JTextField(50);
    JButton sendBtn = new JButton("전송");
    JComboBox<String> whisperCombo = new JComboBox<>();
    DefaultComboBoxModel<String> whisperModel = new DefaultComboBoxModel<>();

    // 검색 관련 컴포넌트
    JTextField searchField = new JTextField(20);
    JButton searchBtn = new JButton("검색");
    JButton prevBtn = new JButton("<");
    JButton nextBtn = new JButton(">");
    JLabel searchResultLabel = new JLabel("");
    private java.util.List<Integer> searchPositions = new java.util.ArrayList<>();
    private int currentSearchIndex = -1;
    private String lastSearchKeyword = "";
    private Highlighter highlighter;
    private Highlighter.HighlightPainter searchPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);

    // 카카오톡 스타일 색상
    private static final Color BACKGROUND_COLOR = new Color(178, 199, 217);  // 연한 파란색 배경
    private static final Color MY_MESSAGE_COLOR = new Color(255, 235, 51);   // 노란색 (내 메시지)
    private static final Color OTHER_MESSAGE_COLOR = Color.WHITE;            // 흰색 (상대방 메시지)
    private static final Color SYSTEM_MESSAGE_COLOR = new Color(100, 100, 100);  // 회색 (시스템 메시지)
    private static final Color WHISPER_COLOR = new Color(220, 240, 255); // 귓속말 배경

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // 파일 메시지 메타 저장 (다운로드 결과와 매칭용)
    private final Map<Long, FileMeta> fileMetaMap = new HashMap<>();

    private static class FileMeta {
        final long messageId;
        final String senderNickname;
        final String fileName;
        final String mimeType;
        final long fileSize;
        final String time;
        int imageInsertPosition = -1;  // 이미지 삽입 위치 (나중에 정확한 위치에 이미지 추가)

        FileMeta(long messageId, String senderNickname, String fileName, String mimeType, long fileSize, String time) {
            this.messageId = messageId;
            this.senderNickname = senderNickname;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.fileSize = fileSize;
            this.time = time;
        }
    }

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
        highlighter = chatTextPane.getHighlighter();
        
        JScrollPane scrPane = new JScrollPane(chatTextPane);
        scrPane.setBounds(10, 10, 380, 380);
        scrPane.setBorder(BorderFactory.createEmptyBorder());
        scrPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrPane);

        // 검색 패널
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(null);
        searchPanel.setBounds(10, 400, 380, 40);
        searchPanel.setBackground(new Color(240, 240, 240));
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        searchField.setBounds(5, 5, 150, 30);
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        searchPanel.add(searchField);
        
        searchBtn.setBounds(160, 5, 60, 30);
        searchBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        searchBtn.setBackground(new Color(100, 150, 255));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);
        
        prevBtn.setBounds(225, 5, 40, 30);
        prevBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        prevBtn.setEnabled(false);
        prevBtn.addActionListener(e -> moveToPrevious());
        searchPanel.add(prevBtn);
        
        nextBtn.setBounds(270, 5, 40, 30);
        nextBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        nextBtn.setEnabled(false);
        nextBtn.addActionListener(e -> moveToNext());
        searchPanel.add(nextBtn);
        
        searchResultLabel.setBounds(315, 5, 60, 30);
        searchResultLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        searchResultLabel.setForeground(new Color(80, 80, 80));
        searchPanel.add(searchResultLabel);
        
        add(searchPanel);


        // 귓속말 콤보박스
        whisperCombo.setModel(whisperModel);
        whisperCombo.setBounds(10, 450, 90, 40);
        whisperCombo.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        whisperCombo.addItem("전체");
        add(whisperCombo);

        // 메시지 입력 필드
        msgTextF.setBounds(110, 450, 90, 40);
        msgTextF.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        msgTextF.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(msgTextF);

        // 파일 전송 버튼
        JButton fileBtn = new JButton("📎");
        fileBtn.setBounds(210, 450, 40, 40);
        fileBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        fileBtn.setBackground(Color.WHITE);
        fileBtn.setBorderPainted(true);
        fileBtn.setFocusPainted(false);
        fileBtn.addActionListener(e -> showFileChooser());
        add(fileBtn);

        // 이모티콘 버튼
        JButton emojiBtn = new JButton("😀");
        emojiBtn.setBounds(255, 450, 40, 40);
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
        setBounds(10, 10, 400, 550);
    }

    public void addMessage(MessageType messageType, String userName, String message) {
        try {
            String timeStamp = timeFormat.format(new Date());
            switch (messageType) {
                case ENTER:
                case EXIT:
                case SYSTEM:
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

    // 모든 메시지를 초기화하고 새로고침
    public void clearMessages() {
        try {
            doc.remove(0, doc.getLength());
            fileMetaMap.clear();
            System.out.println("[ChatPanel] 메시지 목록 초기화");
        } catch (BadLocationException e) {
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

    // 검색 기능
    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색어를 입력하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 이전 하이라이트 제거
        highlighter.removeAllHighlights();
        searchPositions.clear();
        currentSearchIndex = -1;

        try {
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            // 모든 검색 위치 찾기
            while ((pos = text.indexOf(keyword, pos)) >= 0) {
                try {
                    highlighter.addHighlight(pos, pos + keyword.length(), searchPainter);
                    searchPositions.add(pos);
                    pos += keyword.length();
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        lastSearchKeyword = keyword;

        if (searchPositions.isEmpty()) {
            searchResultLabel.setText("0/0");
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this, "'" + keyword + "'를 찾을 수 없습니다.", "검색 결과", JOptionPane.INFORMATION_MESSAGE);
        } else {
            currentSearchIndex = 0;
            updateSearchNavigation();
            scrollToPosition(searchPositions.get(0));
        }
    }

    private void moveToNext() {
        if (searchPositions.isEmpty()) return;
        currentSearchIndex = (currentSearchIndex + 1) % searchPositions.size();
        updateSearchNavigation();
        scrollToPosition(searchPositions.get(currentSearchIndex));
    }

    private void moveToPrevious() {
        if (searchPositions.isEmpty()) return;
        currentSearchIndex = (currentSearchIndex - 1 + searchPositions.size()) % searchPositions.size();
        updateSearchNavigation();
        scrollToPosition(searchPositions.get(currentSearchIndex));
    }

    private void updateSearchNavigation() {
        searchResultLabel.setText((currentSearchIndex + 1) + "/" + searchPositions.size());
        prevBtn.setEnabled(searchPositions.size() > 1);
        nextBtn.setEnabled(searchPositions.size() > 1);
    }

    private void scrollToPosition(int pos) {
        try {
            Rectangle rect = chatTextPane.modelToView(pos);
            if (rect != null) {
                // 검색 결과를 뷰포트 중앙에 배치하기 위해 위아래 여유 공간 추가
                Rectangle viewRect = new Rectangle(rect.x, rect.y - 100, rect.width, rect.height + 200);
                chatTextPane.scrollRectToVisible(viewRect);
                chatTextPane.setCaretPosition(pos);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("파일 선택");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // 파일 크기 제한 (10MB)
        final long MAX_FILE_SIZE = 10 * 1024 * 1024;
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            
            if (selectedFile.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this, "파일 크기는 10MB를 초과할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                byte[] fileData = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                String mimeType = java.nio.file.Files.probeContentType(selectedFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                
                dto.request.FileUploadRequest uploadReq = new dto.request.FileUploadRequest(
                    chatRoomName,
                    Application.me.getId(),
                    selectedFile.getName(),
                    mimeType,
                    selectedFile.length(),
                    fileData
                );
                
                // 파일 업로드 요청 전송
                Application.sender.sendMessage(uploadReq);
                
                System.out.println("[파일 업로드] " + selectedFile.getName() + " (" + selectedFile.length() + " bytes)");
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "파일 읽기 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void addFileMessage(long messageId, String senderNickname, String fileName, 
                               String mimeType, long fileSize, String timeStamp) {
        try {
            boolean isMyMessage = Application.me != null && senderNickname.equals(Application.me.getNickName());
            boolean isImage = mimeType != null && mimeType.startsWith("image/");

            FileMeta meta = new FileMeta(messageId, senderNickname, fileName, mimeType, fileSize, timeStamp);
            fileMetaMap.put(messageId, meta);
            
            SimpleAttributeSet nameAttrs = new SimpleAttributeSet();
            SimpleAttributeSet msgAttrs = new SimpleAttributeSet();
            
            StyleConstants.setFontSize(nameAttrs, 11);
            StyleConstants.setFontSize(msgAttrs, 12);
            
            doc.insertString(doc.getLength(), "\n", null);
            
            if (isMyMessage) {
                StyleConstants.setAlignment(msgAttrs, StyleConstants.ALIGN_RIGHT);
                StyleConstants.setBackground(msgAttrs, MY_MESSAGE_COLOR);
                
                int start = doc.getLength();
                String fileInfo = (isImage ? "🖼️ " : "📎 ") + fileName;
                doc.insertString(doc.getLength(), fileInfo + "\n", msgAttrs);
                doc.setParagraphAttributes(start, fileInfo.length(), msgAttrs, false);
                
                // 이미지일 경우 삽입 위치 기록 (다음 문자 위치)
                if (isImage) {
                    meta.imageInsertPosition = doc.getLength();
                }
                
            } else {
                StyleConstants.setAlignment(msgAttrs, StyleConstants.ALIGN_LEFT);
                StyleConstants.setForeground(nameAttrs, new Color(60, 60, 60));
                StyleConstants.setBackground(msgAttrs, OTHER_MESSAGE_COLOR);
                
                int start = doc.getLength();
                doc.insertString(doc.getLength(), senderNickname + "\n", nameAttrs);
                
                String fileInfo = (isImage ? "🖼️ " : "📎 ") + fileName;
                doc.insertString(doc.getLength(), fileInfo + "\n", msgAttrs);
                doc.setParagraphAttributes(start, doc.getLength() - start, msgAttrs, false);
                
                // 이미지일 경우 삽입 위치 기록 (다음 문자 위치)
                if (isImage) {
                    meta.imageInsertPosition = doc.getLength();
                }
            }
            
            chatTextPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 이미지 파일 다운로드 결과를 UI에 렌더링 (정확한 위치에 삽입)
    public void addImagePreview(long messageId, byte[] imageBytes) {
        try {
            FileMeta meta = fileMetaMap.get(messageId);
            if (meta == null) {
                return;
            }

            boolean isMyMessage = Application.me != null && meta.senderNickname.equals(Application.me.getNickName());

            BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
            if (img == null) {
                return;
            }

            int maxWidth = 220;
            int width = img.getWidth();
            int height = img.getHeight();
            if (width > maxWidth) {
                double ratio = maxWidth / (double) width;
                width = maxWidth;
                height = (int) (height * ratio);
            }

            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);

            SimpleAttributeSet imgAttrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(imgAttrs, isMyMessage ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            if (isMyMessage) {
                StyleConstants.setBackground(imgAttrs, MY_MESSAGE_COLOR);
            } else {
                StyleConstants.setBackground(imgAttrs, OTHER_MESSAGE_COLOR);
            }

            // 기록된 삽입 위치에 이미지 추가 (파일명 바로 다음에 정확히 삽입)
            if (meta.imageInsertPosition >= 0 && meta.imageInsertPosition <= doc.getLength()) {
                try {
                    chatTextPane.setCaretPosition(meta.imageInsertPosition);
                    chatTextPane.insertIcon(icon);
                    doc.insertString(meta.imageInsertPosition + 1, "\n", null);
                    doc.setParagraphAttributes(meta.imageInsertPosition, 1, imgAttrs, false);
                } catch (Exception ex) {
                    // 위치가 문제 있으면 끝에 추가
                    int start = doc.getLength();
                    chatTextPane.setCaretPosition(start);
                    chatTextPane.insertIcon(icon);
                    doc.insertString(doc.getLength(), "\n", null);
                    doc.setParagraphAttributes(start, doc.getLength() - start, imgAttrs, false);
                }
            } else {
                // 위치 정보가 없으면 끝에 추가
                int start = doc.getLength();
                chatTextPane.setCaretPosition(start);
                chatTextPane.insertIcon(icon);
                doc.insertString(doc.getLength(), "\n", null);
                doc.setParagraphAttributes(start, doc.getLength() - start, imgAttrs, false);
            }

            chatTextPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }

    // 메시지 ID로 메시지를 찾아서 제거
    public void removeMessage(long messageId) {
        try {
            // 파일 메시지인 경우: fileMetaMap에서 찾기
            FileMeta meta = fileMetaMap.get(messageId);
            if (meta != null) {
                // 파일명을 포함하는 라인 찾기
                String fullText = doc.getText(0, doc.getLength());
                String[] lines = fullText.split("\n", -1);
                
                int currentPos = 0;
                int startRemove = -1;
                int endRemove = -1;
                
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    int lineLength = line.length() + 1;
                    
                    if (line.contains(meta.fileName) && (line.contains("🖼️") || line.contains("📎"))) {
                        startRemove = currentPos;
                        
                        // 발신자 이름이 있으면 그 줄도 제거
                        if (i > 0 && lines[i-1].equals(meta.senderNickname)) {
                            startRemove = currentPos - (lines[i-1].length() + 1);
                        }
                        
                        // 파일 메시지 라인 제거
                        endRemove = currentPos + lineLength;
                        
                        // 이미지가 있으면 다음 라인도 포함
                        if (i + 1 < lines.length && lines[i+1].length() > 0) {
                            endRemove += lines[i+1].length() + 1;
                        }
                        
                        // 앞의 빈 줄 제거
                        if (startRemove > 0 && startRemove <= fullText.length() && fullText.charAt(startRemove - 1) == '\n') {
                            startRemove--;
                        }
                        
                        break;
                    }
                    currentPos += lineLength;
                }
                
                if (startRemove >= 0 && endRemove > startRemove && endRemove <= doc.getLength()) {
                    doc.remove(startRemove, endRemove - startRemove);
                    fileMetaMap.remove(messageId);
                    System.out.println("[MESSAGE_DELETE] 파일 메시지 제거 - fileName: " + meta.fileName + ", messageId: " + messageId);
                    return;
                }
            }
            
            System.out.println("[MESSAGE_DELETE] 메시지 ID " + messageId + " 처리 완료 (파일 메시지 아님)");
            
        } catch (Exception e) {
            System.err.println("[MESSAGE_DELETE] 메시지 제거 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

}


