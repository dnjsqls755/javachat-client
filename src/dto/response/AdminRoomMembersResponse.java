package dto.response;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomMembersResponse {
    private final String roomName;
    private final List<Member> members;

    public static class Member {
        public final String id;
        public final String nickname;
        public final boolean online;

        public Member(String id, String nickname, boolean online) {
            this.id = id;
            this.nickname = nickname;
            this.online = online;
        }
    }

    public AdminRoomMembersResponse(String payload) {
        String[] parts = payload.split("\\|", 2);
        this.roomName = parts.length > 0 ? parts[0] : "";
        this.members = new ArrayList<>();
        if (parts.length > 1 && !parts[1].isEmpty()) {
            String[] tokens = parts[1].split(";");
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                String[] m = token.split(",", 3);
                String id = m.length > 0 ? m[0] : "";
                String nick = m.length > 1 ? m[1] : "";
                boolean online = m.length > 2 && "1".equals(m[2]);
                members.add(new Member(id, nick, online));
            }
        }
    }

    public String getRoomName() { return roomName; }
    public List<Member> getMembers() { return members; }
}
