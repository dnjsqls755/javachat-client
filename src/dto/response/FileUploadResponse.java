package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class FileUploadResponse extends DTO {
    private final boolean success;
    private final String message;
    private final long messageId;

    public FileUploadResponse(boolean success, String message, long messageId) {
        super(DtoType.FILE_UPLOAD_RESULT);
        this.success = success;
        this.message = message;
        this.messageId = messageId;
    }

    public FileUploadResponse(String data) {
        super(DtoType.FILE_UPLOAD_RESULT);
        String[] parts = data.split("\\|", 3);
        this.success = parts.length > 0 && "true".equals(parts[0]);
        this.messageId = parts.length > 1 ? Long.parseLong(parts[1]) : 0;
        this.message = parts.length > 2 ? parts[2] : "";
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public long getMessageId() { return messageId; }

    @Override
    public String toString() {
        return super.toString() + success + "|" + messageId + "|" + message;
    }
}
