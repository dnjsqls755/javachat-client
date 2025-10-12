package dto.request;

import dto.type.DtoType;

public class LoginRequest extends DTO {

    String id;

    String pw;

    public LoginRequest(String id, String pw) {
        super(DtoType.LOGIN);

        this.id = id;
        this.pw = pw;
    }

    @Override
    public String toString() {
        return super.toString() + id + "," + pw;
    }
}
