package dto.response;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryResponse {
    private String chatRoomName;
    private List<HistoryEntry> entries = new ArrayList<>();

    public static class HistoryEntry {
        public final String nickname;
        public final String time;
        public final String content;
        public final String messageType;      // "TEXT" or "IMAGE" or "FILE"
        public final long messageId;          // For file messages
        public final String fileName;
        public final String mimeType;
        public final long fileSize;

        // 일반 텍스트 메시지
        public HistoryEntry(String nickname, String time, String content) {
            this.nickname = nickname;
            this.time = time;
            this.content = content;
            this.messageType = "TEXT";
            this.messageId = 0;
            this.fileName = null;
            this.mimeType = null;
            this.fileSize = 0;
        }

        // 파일 메시지
        public HistoryEntry(String nickname, String time, String content, 
                          String messageType, long messageId, String fileName, String mimeType, long fileSize) {
            this.nickname = nickname;
            this.time = time;
            this.content = content;
            this.messageType = messageType;
            this.messageId = messageId;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.fileSize = fileSize;
        }
    }

    public ChatHistoryResponse(String raw) {
        // 형식: roomName|nick,time,content,msgType,msgId,fileName,mimeType,fileSize|...
        String[] parts = raw.split("\\|");
        if (parts.length > 0) {
            chatRoomName = parts[0];
        }
        for (int i = 1; i < parts.length; i++) {
            String seg = parts[i];
            String[] fields = seg.split(",", -1);  // -1: trailing empty strings 유지
            if (fields.length >= 3) {
                String nickname = fields[0];
                String time = fields[1];
                String content = fields[2];
                
                if (fields.length >= 8) {
                    // 파일 메시지
                    String messageType = fields[3];
                    long messageId = Long.parseLong(fields[4]);
                    String fileName = fields[5].isEmpty() ? null : fields[5];
                    String mimeType = fields[6].isEmpty() ? null : fields[6];
                    long fileSize = Long.parseLong(fields[7]);
                    entries.add(new HistoryEntry(nickname, time, content, messageType, messageId, fileName, mimeType, fileSize));
                } else {
                    // 텍스트 메시지
                    entries.add(new HistoryEntry(nickname, time, content));
                }
            }
        }
    }

    public String getChatRoomName() { return chatRoomName; }
    public List<HistoryEntry> getEntries() { return entries; }
}