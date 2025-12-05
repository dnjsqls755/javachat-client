package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class FindPasswordResponse extends DTO {
    private boolean success;
    private String message;

    public FindPasswordResponse(boolean success, String message) {
        super(DtoType.FIND_PASSWORD_RESULT);
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
