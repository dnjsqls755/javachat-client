package dto.request;

import dto.type.DtoType;

public class ChatRoomInviteRequest extends DTO {
    private final String roomName;
    private final String senderUserId;
    private final String targetNickname;

    public ChatRoomInviteRequest(String roomName, String senderUserId, String targetNickname) {
        super(DtoType.CHAT_ROOM_INVITE);
        this.roomName = roomName;
        this.senderUserId = senderUserId;
        this.targetNickname = targetNickname;
    }

    @Override
    public String toString() {
        return super.toString() + roomName + "|" + senderUserId + "|" + targetNickname;
    }

    public String getRoomName() { return roomName; }
    public String getSenderUserId() { return senderUserId; }
    public String getTargetNickname() { return targetNickname; }
}
