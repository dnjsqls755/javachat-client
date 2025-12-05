package dto.response;

import domain.User;

import java.util.ArrayList;
import java.util.List;

public class FriendListResponse {
    private final List<User> friends = new ArrayList<>();

    public FriendListResponse(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String[] userValues = message.split("\\\\|");
        for (String value : userValues) {
            if (value.isEmpty()) {
                continue;
            }
            String[] pieces = value.split(",");
            if (pieces.length >= 2) {
                User u = new User(pieces[0], pieces[1]);
                friends.add(u);
            }
        }
    }

    public List<User> getFriends() {
        return friends;
    }
}
