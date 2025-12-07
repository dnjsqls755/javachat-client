package dto.request;

import dto.type.DtoType;

public class FileDownloadRequest extends DTO {
    private final long messageId;
    private final String chatRoomName;

    public FileDownloadRequest(long messageId, String chatRoomName) {
        super(DtoType.FILE_DOWNLOAD);
        this.messageId = messageId;
        this.chatRoomName = chatRoomName;
    }

    public long getMessageId() { return messageId; }
    public String getChatRoomName() { return chatRoomName; }

    @Override
    public String toString() {
        return super.toString() + messageId + "|" + chatRoomName;
    }
}
