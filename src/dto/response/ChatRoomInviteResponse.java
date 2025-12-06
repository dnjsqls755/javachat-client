package dto.response;

public class ChatRoomInviteResponse {
    private final String roomName;
    private final String senderNickname;
    private final String senderUserId;

    public ChatRoomInviteResponse(String roomName, String senderUserId, String senderNickname) {
        this.roomName = roomName;
        this.senderUserId = senderUserId;
        this.senderNickname = senderNickname;
    }

    public ChatRoomInviteResponse(String message) {
        String[] parts = message.split("\\|", 3);
        this.roomName = parts.length > 0 ? parts[0] : "";
        this.senderUserId = parts.length > 1 ? parts[1] : "";
        this.senderNickname = parts.length > 2 ? parts[2] : "";
    }

    public String getRoomName() { return roomName; }
    public String getSenderUserId() { return senderUserId; }
    public String getSenderNickname() { return senderNickname; }
}
