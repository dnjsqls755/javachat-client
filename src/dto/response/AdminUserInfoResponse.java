package dto.response;

import dto.request.DTO;
import dto.type.DtoType;

public class AdminUserInfoResponse extends DTO {
    private final String userId;
    private final String nickname;
    private final String email;
    private final String phone;
    private final String address;
    private final String detailAddress;
    private final String postalCode;
    private final String gender;
    private final String birthDate;

    public AdminUserInfoResponse(String userId, String nickname, String email, String phone,
                                  String address, String detailAddress, String postalCode,
                                  String gender, String birthDate) {
        super(DtoType.ADMIN_USER_INFO_RESULT);
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public AdminUserInfoResponse(String message) {
        super(DtoType.ADMIN_USER_INFO_RESULT);
        String[] parts = message.split("\\|", 9);
        this.userId = parts.length > 0 ? parts[0] : "";
        this.nickname = parts.length > 1 ? parts[1] : "";
        this.email = parts.length > 2 ? parts[2] : "";
        this.phone = parts.length > 3 ? parts[3] : "";
        this.address = parts.length > 4 ? parts[4] : "";
        this.detailAddress = parts.length > 5 ? parts[5] : "";
        this.postalCode = parts.length > 6 ? parts[6] : "";
        this.gender = parts.length > 7 ? parts[7] : "";
        this.birthDate = parts.length > 8 ? parts[8] : "";
    }

    @Override
    public String toString() {
        return super.toString() + userId + "|" + nickname + "|" + email + "|" + phone + "|" + 
               address + "|" + detailAddress + "|" + postalCode + "|" + gender + "|" + birthDate;
    }

    public String getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDetailAddress() { return detailAddress; }
    public String getPostalCode() { return postalCode; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
}
