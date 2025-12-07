package dto.request;

import dto.type.DtoType;

public class ProfileImageRequest extends DTO {
    private final String userId;

    public ProfileImageRequest(String userId) {
        super(DtoType.PROFILE_IMAGE);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return super.toString() + userId;
    }
}
