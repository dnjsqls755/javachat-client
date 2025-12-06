package dto.request;

import dto.type.DtoType;

public class ChatRoomInviteAcceptRequest extends DTO {
    private final String roomName;
    private final String userId;

    public ChatRoomInviteAcceptRequest(String roomName, String userId) {
        super(DtoType.CHAT_ROOM_INVITE_ACCEPT);
        this.roomName = roomName;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return super.toString() + roomName + "|" + userId;
    }

    public String getRoomName() { return roomName; }
    public String getUserId() { return userId; }
}
