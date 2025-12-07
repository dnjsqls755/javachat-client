package dto.request;

import dto.type.DtoType;

public class FileUploadRequest extends DTO {
    private final String chatRoomName;
    private final String senderId;
    private final String fileName;
    private final String mimeType;
    private final long fileSize;
    private final byte[] fileData;

    public FileUploadRequest(String chatRoomName, String senderId, String fileName, 
                            String mimeType, long fileSize, byte[] fileData) {
        super(DtoType.FILE_UPLOAD);
        this.chatRoomName = chatRoomName;
        this.senderId = senderId;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileData = fileData;
    }

    public String getChatRoomName() { return chatRoomName; }
    public String getSenderId() { return senderId; }
    public String getFileName() { return fileName; }
    public String getMimeType() { return mimeType; }
    public long getFileSize() { return fileSize; }
    public byte[] getFileData() { return fileData; }

    @Override
    public String toString() {
        return super.toString() + chatRoomName + "|" + senderId + "|" + fileName + "|" + 
               mimeType + "|" + fileSize;
    }

    public String toStringWithData() {
        return toString() + "|" + java.util.Base64.getEncoder().encodeToString(fileData);
    }
}
