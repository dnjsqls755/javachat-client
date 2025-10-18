package dto.type;

public enum DtoType {
    INIT,
    LOGIN_FAIL,
    ID_CHECK,//아이디 중복확인
    NICKNAME_CHECK,//닉네임 중복
    LOGIN,
    CREATE_CHAT,
    ENTER_CHAT, EXIT_CHAT,
    MESSAGE,
    SIGNUP,
    USER_LIST, CHAT_ROOM_LIST,
}
