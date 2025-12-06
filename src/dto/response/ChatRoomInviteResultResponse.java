package dto.response;

public class ChatRoomInviteResultResponse {
    private final String message;
    private final boolean success;

    public ChatRoomInviteResultResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ChatRoomInviteResultResponse(String messageStr) {
        String[] parts = messageStr.split("\\|", 2);
        this.message = parts.length > 0 ? parts[0] : "";
        this.success = parts.length > 1 ? Boolean.parseBoolean(parts[1]) : false;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
