package dto.request;

public class JoinRequest {
    private String userId;
    private String name;
    private String password;
    private String profileImg;
    private String statusMsg;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private String detailAddress;
    private String postalCode;
    private String gender;
    private String birthDate;

    public String toMessage() {
        return String.join(",", userId, name, password, profileImg, statusMsg, nickname,
                email, phone, address, detailAddress, postalCode, gender, birthDate);
    }


public JoinRequest() {}

    // 각 필드에 대한 setter 메서드
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }
    public void setStatusMsg(String statusMsg) { this.statusMsg = statusMsg; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

}
