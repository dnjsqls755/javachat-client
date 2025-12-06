package dto.request;

import dto.type.DtoType;

public class AdminUserInfoRequest extends DTO {
    private final String userId;

    public AdminUserInfoRequest(String userId) {
        super(DtoType.ADMIN_USER_INFO);
        this.userId = userId;
    }

    @Override
    public String toString() {
        return super.toString() + userId;
    }
}
