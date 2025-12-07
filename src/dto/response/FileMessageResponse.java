package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class FileMessageResponse extends DTO {
    private final String chatRoomName;
    private final String senderNickname;
    private final long messageId;
    private final String fileName;
    private final String mimeType;
    private final long fileSize;
    private final String sentAt;

    public FileMessageResponse(String message) {
        super(DtoType.FILE_MESSAGE);
        String[] parts = message.split("\\|", 7);
        this.chatRoomName = parts.length > 0 ? parts[0] : "";
        this.senderNickname = parts.length > 1 ? parts[1] : "";
        this.messageId = parts.length > 2 ? Long.parseLong(parts[2]) : 0;
        this.fileName = parts.length > 3 ? parts[3] : "";
        this.mimeType = parts.length > 4 ? parts[4] : "";
        this.fileSize = parts.length > 5 ? Long.parseLong(parts[5]) : 0;
        this.sentAt = parts.length > 6 ? parts[6] : "";
    }

    public String getChatRoomName() { return chatRoomName; }
    public String getSenderNickname() { return senderNickname; }
    public long getMessageId() { return messageId; }
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }
    public long getFileSize() { return fileSize; }
    public String getSentAt() { return sentAt; }
}
