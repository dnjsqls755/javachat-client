package dto.request;

import dto.type.DtoType;

public class FindPasswordRequest extends DTO {
    private String id;
    private String email;

    public FindPasswordRequest(String id, String email) {
        super(DtoType.FIND_PASSWORD);
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return super.toString() + id + ":" + email;
    }
}
