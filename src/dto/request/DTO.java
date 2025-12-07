package dto.request;

import dto.type.DtoType;

public abstract class DTO {
    protected DtoType type;

    public DTO(DtoType type) {
        this.type = type;
    }

    public DtoType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ":";
    }
}
