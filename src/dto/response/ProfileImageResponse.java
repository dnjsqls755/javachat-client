package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class ProfileImageResponse extends DTO {
    private final String userId;
    private final String imageData;
    private final String name;
    private final String gender;
    private final String birthDate;

    public ProfileImageResponse(String message) {
        super(DtoType.PROFILE_IMAGE_RESULT);
        String[] parts = message.split("\\|", 5);
        this.userId = parts.length > 0 ? parts[0] : "";
        this.imageData = parts.length > 1 ? parts[1] : "DEFAULT";
        this.name = parts.length > 2 ? parts[2] : "";
        this.gender = parts.length > 3 ? parts[3] : "";
        this.birthDate = parts.length > 4 ? parts[4] : "";
    }

    public String getUserId() {
        return userId;
    }

    public String getImageData() {
        return imageData;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }

    public boolean hasImage() {
        return imageData != null && !"DEFAULT".equals(imageData) && !imageData.isEmpty();
    }

    public java.awt.image.BufferedImage getImage() {
        if (!hasImage()) {
            return null;
        }
        try {
            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageBytes);
            return javax.imageio.ImageIO.read(bis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
