package dto.request;

import dto.type.DtoType;

public class AdminRoomMembersRequest extends DTO {
    private final String roomName;

    public AdminRoomMembersRequest(String roomName) {
        super(DtoType.ADMIN_ROOM_MEMBERS);
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return super.toString() + roomName;
    }
}
