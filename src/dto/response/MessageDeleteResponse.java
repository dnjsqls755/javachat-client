package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class MessageDeleteResponse extends DTO {
    private final String chatRoomName;
    private final long messageId;

    public MessageDeleteResponse(String message) {
        super(DtoType.MESSAGE_DELETE);
        String[] parts = message.split("\\|", 2);
        this.chatRoomName = parts.length > 0 ? parts[0] : "";
        this.messageId = parts.length > 1 ? Long.parseLong(parts[1]) : 0;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public long getMessageId() {
        return messageId;
    }
}
