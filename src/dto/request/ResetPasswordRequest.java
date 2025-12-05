package dto.request;

import dto.type.DtoType;

public class ResetPasswordRequest extends DTO {
    private String id;
    private String newPassword;

    public ResetPasswordRequest(String id, String newPassword) {
        super(DtoType.RESET_PASSWORD);
        this.id = id;
        this.newPassword = newPassword;
    }

    public String getId() {
        return id;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return super.toString() + id + ":" + newPassword;
    }
}
