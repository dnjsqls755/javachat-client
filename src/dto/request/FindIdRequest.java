package dto.request;

import dto.type.DtoType;

public class FindIdRequest extends DTO {
    private String name;
    private String email;

    public FindIdRequest(String name, String email) {
        super(DtoType.FIND_ID);
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return super.toString() + name + ":" + email;
    }
}
