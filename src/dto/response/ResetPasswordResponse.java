package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class ResetPasswordResponse extends DTO {
    private boolean success;
    private String message;

    public ResetPasswordResponse(boolean success, String message) {
        super(DtoType.RESET_PASSWORD_RESULT);
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return super.toString() + success + ":" + message;
    }
}
