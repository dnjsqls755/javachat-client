package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class FindIdResponse extends DTO {
    private boolean success;
    private String userId;
    private String message;

    public FindIdResponse(boolean success, String userId, String message) {
        super(DtoType.FIND_ID_RESULT);
        this.success = success;
        this.userId = userId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return super.toString() + success + ":" + userId + ":" + message;
    }
}
