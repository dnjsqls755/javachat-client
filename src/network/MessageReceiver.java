package network;

import app.Application;
import domain.ChatRoom;
import dto.request.ChatRoomInviteAcceptRequest;
import dto.request.EnterChatRequest;
import dto.response.*;
import dto.type.DtoType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import view.frame.ChatFrame;
import view.frame.LobbyFrame;
import view.panel.ChatPanel;
import view.panel.ChatRoomUserListPanel;

public class MessageReceiver extends Thread {

    Socket socket;

    // 중복확인 응답 콜백
    private static volatile DuplicateCheckCallback duplicateCheckCallback;

    public interface DuplicateCheckCallback {
        void onResult(DtoType type, String message);
    }

    public static void setDuplicateCheckCallback(DuplicateCheckCallback callback) {
        duplicateCheckCallback = callback;
    }

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String str = reader.readLine();
                if (str == null) {
                    try {
                        socket.close();
                        System.out.println(Application.me != null ? Application.me.getNickName() + "'s socket is closed." : "Socket is closed.");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("disconnect");
                    System.exit(1);
                }
                System.out.println(str);
                int sep = str.indexOf(':');
                String typeToken = sep >= 0 ? str.substring(0, sep) : str;
                String payload = sep >= 0 ? str.substring(sep + 1) : "";

                try {
                    DtoType type = DtoType.valueOf(typeToken);
                    String message = payload;
                    processReceivedMessage(type, message);
                } catch (IllegalArgumentException e) {
                    System.out.println("알 수 없는 DtoType: " + typeToken);
                }

                Thread.sleep(300);
            }
        }
        catch (Exception e) {
            try {
                System.out.println("socket error (can't get socket input stream)");
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    private void processReceivedMessage(DtoType type, String message) {
        System.out.println("[" + type + "] 메시지 수신: " + message);

        switch (type) {
            case LOGIN_FAIL:
                if (Application.loginFrame != null) {
                    SwingUtilities.invokeLater(() -> Application.loginFrame.handleLoginFailure("아이디 또는 비밀번호가 잘못되었습니다."));
                }
                break;

            case LOGIN_BANNED:
                if (Application.loginFrame != null) {
                    SwingUtilities.invokeLater(() -> Application.loginFrame.handleLoginFailure("관리자에 의해 차단된 계정입니다."));
                }
                break;

            case LOGIN:
                InitResponse initRes = new InitResponse(message);
                Application.chatRooms = initRes.getChatRooms();
                Application.users = initRes.getUsers();

                for (domain.User u : Application.users) {
                    if (u.getId().equals(Application.me.getId())) {
                        Application.me.setNickName(u.getNickName());
                        Application.me.setRole(u.getRole());
                        Application.me.setOnline(u.isOnline());
                        Application.me.setBanned(u.isBanned());
                        System.out.println("??? ??: " + Application.me.getId() + " (" + Application.me.getNickName() + ")");
                        break;
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    if (Application.isAdmin()) {
                        if (Application.adminFrame == null) {
                            Application.adminFrame = new view.frame.AdminFrame();
                        }
                        Application.adminFrame.setVisible(true);
                        Application.sender.sendMessage(new dto.request.AdminInitRequest());
                        if (Application.lobbyFrame != null) {
                            Application.lobbyFrame.setVisible(false);
                        }
                    } else if (Application.lobbyFrame != null) {
                        Application.lobbyFrame.setVisible(true);
                    }
                    if (Application.loginFrame != null) {
                        Application.loginFrame.dispose();
                    }
                    if (LobbyFrame.chatRoomListPanel != null) {
                        LobbyFrame.chatRoomListPanel.paintChatRoomList();
                    }
                });
                if (LobbyFrame.chatRoomListPanel != null) {
                    LobbyFrame.chatRoomListPanel.paintChatRoomList();
                }
                break;

            case MESSAGE: {
                MessageResponse messageRes = new MessageResponse(message);
                ChatPanel chatPanel = Application.chatPanelMap.get(messageRes.getChatRoomName());
                if (chatPanel != null) {
                    chatPanel.addMessage(messageRes.getMessageType(), messageRes.getUserName(), messageRes.getMessage());
                    System.out.println("[MESSAGE] 메시지 수신 - 방 " + messageRes.getChatRoomName() + ", 타입 " + messageRes.getMessageType() + ", 필드 " + messageRes.getUserName());
                } else {
                    System.out.println("[WARNING] 채팅 패널을 찾을 수 없음: " + messageRes.getChatRoomName());
                }
                break;
            }

            case CREATE_CHAT:
                CreateChatRoomResponse createChatRoomResponse = new CreateChatRoomResponse(message);
                String chatRoomName = createChatRoomResponse.getName();

                boolean exists = Application.chatRooms.stream()
                    .anyMatch(room -> room.getName().equals(chatRoomName));

                if (!exists) {
                    ChatRoom newChatRoom = new ChatRoom(chatRoomName);
                    Application.chatRooms.add(newChatRoom);
                    System.out.println("[CREATE_CHAT] 새 채팅방 추가: " + chatRoomName);
                    if (LobbyFrame.chatRoomListPanel != null) {
                        LobbyFrame.chatRoomListPanel.addChatRoom(chatRoomName);
                    }
                }
                break;

            case ADMIN_USER_LIST:
                AdminUserListResponse adminUsers = new AdminUserListResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.updateUsers(adminUsers.getUsers());
                }
                break;

            case ADMIN_CHATROOM_LIST:
                AdminChatRoomListResponse adminRooms = new AdminChatRoomListResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.updateChatRooms(adminRooms.getChatRooms());
                }
                break;

            case ADMIN_MESSAGE_RESULT:
                AdminMessageSearchResponse adminMessages = new AdminMessageSearchResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.updateMessages(adminMessages.getMessages());
                }
                break;

            case ADMIN_ROOM_MEMBERS_RESULT:
                AdminRoomMembersResponse membersRes = new AdminRoomMembersResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.showRoomMembers(membersRes.getRoomName(), membersRes.getMembers());
                }
                break;

            case ADMIN_ACTION_RESULT:
                AdminActionResultResponse adminAction = new AdminActionResultResponse(message);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, adminAction.getMessage(),
                                adminAction.isSuccess() ? "??" : "??",
                                adminAction.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
                break;

            case ADMIN_USER_INFO_RESULT:
                AdminUserInfoResponse userInfoRes = new AdminUserInfoResponse(message);
                if (Application.adminFrame != null) {
                    SwingUtilities.invokeLater(() -> 
                        Application.adminFrame.showUserInfoDialog(
                            userInfoRes.getUserId(), userInfoRes.getName(), userInfoRes.getNickname(), userInfoRes.getEmail(), 
                            userInfoRes.getPhone(), userInfoRes.getAddress(), userInfoRes.getDetailAddress(),
                            userInfoRes.getPostalCode(), userInfoRes.getGender(), userInfoRes.getBirthDate()));
                }
                break;

            case FORCE_LOGOUT:
                SwingUtilities.invokeLater(() -> {
                    String notice = (message == null || message.isEmpty()) ? "???? ?? ?????????." : message;
                    JOptionPane.showMessageDialog(null, notice, "??", JOptionPane.WARNING_MESSAGE);
                    try { socket.close(); } catch (Exception ignored) { }
                    System.exit(0);
                });
                break;

            case FORCE_EXIT:
                String[] exitParts = message.split("\\|", 2);
                String forcedRoom = exitParts.length > 0 ? exitParts[0] : "";
                String reason = exitParts.length > 1 ? exitParts[1] : "????? ???????.";
                closeChatRoom(forcedRoom, reason);
                break;

            case USER_LIST: {
                UserListResponse userListRes = new UserListResponse(message);
                if ("Lobby".equals(userListRes.getChatRoomName())) {
                    Application.users = userListRes.getUsers();
                    System.out.println("[USER_LIST] 로비 사용자 목록 업데이트 (" + Application.users.size() + ")");
                } else {
                    ChatRoomUserListPanel userListPanel = Application.chatRoomUserListPanelMap.get(userListRes.getChatRoomName());
                    if (userListPanel != null) {
                        userListPanel.paintChatUsers(userListRes.getUsers());
                        System.out.println("[USER_LIST] 사용자 목록 업데이트: " + userListRes.getChatRoomName() + " (" + userListRes.getUsers().size() + ")");
                    } else {
                        System.out.println("[WARNING] 사용자 목록 패널을 찾을 수 없음: " + userListRes.getChatRoomName());
                    }
                    // 귓속말 대상 콤보 갱신
                    ChatPanel chatPanel = Application.chatPanelMap.get(userListRes.getChatRoomName());
                    if (chatPanel != null) {
                        java.util.List<String> nicks = new java.util.ArrayList<>();
                        for (domain.User u : userListRes.getUsers()) {
                            nicks.add(u.getNickName());
                        }
                        chatPanel.updateWhisperTargets(nicks);
                    }
                }
                break;
            }

            case CHAT_ROOM_LIST:
                ChatRoomListResponse chatRoomListRes = new ChatRoomListResponse(message);
                Application.chatRooms = chatRoomListRes.getChatRooms();
                if (LobbyFrame.chatRoomListPanel != null) {
                    LobbyFrame.chatRoomListPanel.paintChatRoomList();
                }
                break;

            case CHAT_HISTORY:
                ChatHistoryResponse historyRes = new ChatHistoryResponse(message);
                final String roomName = historyRes.getChatRoomName();
                final java.util.List<ChatHistoryResponse.HistoryEntry> entries = historyRes.getEntries();
                
                System.out.println("[CHAT_HISTORY] 히스토리 수신: " + roomName + " (" + entries.size() + "개)");
                
                // UI 스레드에서 안전하게 실행
                SwingUtilities.invokeLater(() -> {
                    ChatPanel historyPanel = Application.chatPanelMap.get(roomName);
                    if (historyPanel != null) {
                        // 패널이 이미 존재하면 바로 추가
                        for (ChatHistoryResponse.HistoryEntry entry : entries) {
                            historyPanel.addHistoryMessage(entry.nickname, entry.content, entry.time);
                        }
                        System.out.println("[CHAT_HISTORY] 이전 대화 로드 완료: " + roomName + " (" + entries.size() + "개)");
                    } else {
                        // 패널이 없으면 즉시 생성 후 적용 (레이스 방지)
                        System.out.println("[CHAT_HISTORY] 패널이 없어 자동 생성: " + roomName);
                        if (!Application.chatPanelMap.containsKey(roomName)) {
                            ChatFrame chatFrame = new ChatFrame(roomName);
                            Application.chatFrameMap.put(roomName, chatFrame);
                            Application.chatPanelMap.put(roomName, chatFrame.getChatPanel());
                            Application.chatRoomUserListPanelMap.put(roomName, chatFrame.getChatRoomUserListPanel());
                        }
                        ChatPanel createdPanel = Application.chatPanelMap.get(roomName);
                        if (createdPanel != null) {
                            for (ChatHistoryResponse.HistoryEntry entry : entries) {
                                createdPanel.addHistoryMessage(entry.nickname, entry.content, entry.time);
                            }
                            System.out.println("[CHAT_HISTORY] 자동 생성 후 로드 완료: " + roomName + " (" + entries.size() + "개)");
                        } else {
                            // 최후의 보루로 재시도 기간 연장
                            new Thread(() -> {
                                for (int retries = 0; retries < 50; retries++) { // 최대 2.5초 대기
                                    try {
                                        Thread.sleep(50);
                                        ChatPanel panel = Application.chatPanelMap.get(roomName);
                                        if (panel != null) {
                                            final ChatPanel finalPanel = panel;
                                            SwingUtilities.invokeLater(() -> {
                                                for (ChatHistoryResponse.HistoryEntry entry : entries) {
                                                    finalPanel.addHistoryMessage(entry.nickname, entry.content, entry.time);
                                                }
                                                System.out.println("[CHAT_HISTORY] 지연 로드 완료: " + roomName + " (" + entries.size() + "개)");
                                            });
                                            return;
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                System.err.println("[ERROR] 패널을 찾을 수 없음 (타임아웃): " + roomName);
                            }).start();
                        }
                    }
                });
                break;

            case FRIEND_LIST:
                FriendListResponse friendListResponse = new FriendListResponse(message);
                Application.friends = friendListResponse.getFriends();
                if (LobbyFrame.friendListPanel != null) {
                    LobbyFrame.friendListPanel.setFriends(Application.friends);
                    System.out.println("[FRIEND_LIST] 친구 목록 업데이트 (" + Application.friends.size() + ")");
                }
                break;

            case FRIEND_ADD_RESULT:
            case FRIEND_REMOVE_RESULT:
                FriendOperationResponse opRes = new FriendOperationResponse(message);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, opRes.getMessage(), opRes.isSuccess() ? "알림" : "오류",
                                opRes.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE)
                );
                break;

            case PROFILE_UPDATE_RESULT:
                ProfileUpdateResponse profileRes = new ProfileUpdateResponse(message);
                if (profileRes.isSuccess() && Application.me != null) {
                    Application.me.setNickName(profileRes.getNickname());
                }
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, profileRes.getMessage(),
                                profileRes.isSuccess() ? "알림" : "오류",
                                profileRes.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
                break;

            case FRIEND_CHAT_INVITE:
                FriendChatInviteResponse inviteRes = new FriendChatInviteResponse(message);
                if (Application.me != null) {
                    SwingUtilities.invokeLater(() -> {
                        int choice = JOptionPane.showConfirmDialog(
                                null,
                                inviteRes.getInviterNickname() + "님의 1:1 채팅 초대를 수락하시겠습니까?",
                                "채팅 초대",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (choice == JOptionPane.YES_OPTION) {
                            // 수락: 채팅방 생성/열기 및 입장 요청
                            if (Application.chatRooms.stream().noneMatch(r -> r.getName().equals(inviteRes.getRoomName()))) {
                                Application.chatRooms.add(new ChatRoom(inviteRes.getRoomName()));
                                if (LobbyFrame.chatRoomListPanel != null) {
                                    LobbyFrame.chatRoomListPanel.addChatRoom(inviteRes.getRoomName());
                                }
                            }
                            if (!Application.chatPanelMap.containsKey(inviteRes.getRoomName())) {
                                ChatFrame chatFrame = new ChatFrame(inviteRes.getRoomName());
                                Application.chatFrameMap.put(inviteRes.getRoomName(), chatFrame);
                                Application.chatPanelMap.put(inviteRes.getRoomName(), chatFrame.getChatPanel());
                                Application.chatRoomUserListPanelMap.put(inviteRes.getRoomName(), chatFrame.getChatRoomUserListPanel());
                            }
                            Application.sender.sendRaw("FRIEND_CHAT_INVITE_ACCEPT:" + inviteRes.getRoomName() + "|" + Application.me.getId());
                            Application.sender.sendMessage(new EnterChatRequest(inviteRes.getRoomName(), Application.me.getId()));
                        } else {
                            // 거절: 서버에 거절 통보
                            Application.sender.sendRaw("FRIEND_CHAT_INVITE_DECLINE:" + inviteRes.getRoomName() + "|" + Application.me.getId());
                        }
                    });
                }
                break;

            case FRIEND_CHAT_INVITE_RESULT:
                FriendOperationResponse inviteResult = new FriendOperationResponse(message);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, inviteResult.getMessage(), inviteResult.isSuccess() ? "알림" : "알림",
                                JOptionPane.INFORMATION_MESSAGE)
                );
                break;

            case ID_OK:
            case ID_DUPLICATE:
            case NICKNAME_OK:
            case NICKNAME_DUPLICATE:
            case SIGNUP_SUCCESS:
            case SIGNUP_FAIL:
            case SIGNUP_INVALID_PASSWORD:
            case ADDRESS_RESULT:
                if (duplicateCheckCallback != null) {
                    duplicateCheckCallback.onResult(type, message);
                }
                break;

            case FIND_ID_RESULT:
                FindIdResponse findIdResponse = new FindIdResponse(
                    message.split(":")[0].equals("true"),
                    message.split(":").length > 1 ? message.split(":")[1] : "",
                    message.split(":").length > 2 ? message.split(":")[2] : ""
                );
                if (Application.findIdFrame != null) {
                    SwingUtilities.invokeLater(() -> {
                        if (findIdResponse.isSuccess()) {
                            Application.findIdFrame.handleFindIdSuccess(findIdResponse.getUserId());
                        } else {
                            Application.findIdFrame.handleFindIdFailure(findIdResponse.getMessage());
                        }
                    });
                }
                break;

            case FIND_PASSWORD_RESULT:
                FindPasswordResponse findPwResponse = new FindPasswordResponse(
                    message.split(":")[0].equals("true"),
                    message.split(":").length > 1 ? message.split(":")[1] : ""
                );
                if (Application.findPasswordFrame != null) {
                    SwingUtilities.invokeLater(() -> {
                        if (findPwResponse.isSuccess()) {
                            Application.findPasswordFrame.handleVerifySuccess();
                        } else {
                            Application.findPasswordFrame.handleVerifyFailure(findPwResponse.getMessage());
                        }
                    });
                }
                break;

            case RESET_PASSWORD_RESULT:
                ResetPasswordResponse resetPwResponse = new ResetPasswordResponse(
                    message.split(":")[0].equals("true"),
                    message.split(":").length > 1 ? message.split(":")[1] : ""
                );
                if (Application.findPasswordFrame != null) {
                    SwingUtilities.invokeLater(() -> {
                        if (resetPwResponse.isSuccess()) {
                            Application.findPasswordFrame.handleResetSuccess();
                        } else {
                            Application.findPasswordFrame.handleResetFailure(resetPwResponse.getMessage());
                        }
                    });
                }
                break;

            case CHAT_ROOM_INVITE_RECEIVED:
                ChatRoomInviteResponse inviteResponse = new ChatRoomInviteResponse(message);
                System.out.println("[CHAT_ROOM_INVITE_RECEIVED] 초대 수신 - 방: " + inviteResponse.getRoomName() + 
                                   ", 발신자: " + inviteResponse.getSenderNickname() + " (" + inviteResponse.getSenderUserId() + ")");
                
                SwingUtilities.invokeLater(() -> {
                    String inviteMessage = inviteResponse.getSenderNickname() + "님이 \"" + inviteResponse.getRoomName() + 
                                          "\" 채팅방으로 초대했습니다.\n수락하시겠습니까?";
                    int result = JOptionPane.showConfirmDialog(null, inviteMessage, "채팅방 초대", 
                                                               JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    
                    if (result == JOptionPane.YES_OPTION) {
                        // 초대 수락 - 채팅방에 입장
                        ChatRoomInviteAcceptRequest acceptReq = new ChatRoomInviteAcceptRequest(
                            inviteResponse.getRoomName(), 
                            Application.me.getId()
                        );
                        Application.sender.sendMessage(acceptReq);
                        
                        // 채팅방 입장 전에 ChatFrame 생성
                        String inviteRoomName = inviteResponse.getRoomName();
                        if (!Application.chatFrameMap.containsKey(inviteRoomName)) {
                            ChatFrame chatFrame = new ChatFrame(inviteRoomName);
                            Application.chatFrameMap.put(inviteRoomName, chatFrame);
                            Application.chatPanelMap.put(inviteRoomName, chatFrame.getChatPanel());
                            Application.chatRoomUserListPanelMap.put(inviteRoomName, chatFrame.getChatRoomUserListPanel());
                        }
                        
                        // 서버에서 USER_LIST를 받을 수 있도록 충분한 시간 대기
                        new Thread(() -> {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                            
                            // 채팅방 입장 요청 전송
                            EnterChatRequest enterReq = new EnterChatRequest(inviteRoomName, Application.me.getId());
                            Application.sender.sendMessage(enterReq);
                            
                            System.out.println("[CHAT_ROOM_INVITE_ACCEPT] 초대 수락 및 입장 - 방: " + inviteRoomName);
                        }).start();
                    } else {
                        // 초대 거절 - 특별한 메시지 없음 (암묵적 거절)
                        System.out.println("[CHAT_ROOM_INVITE_DECLINE] 초대 거절 - 방: " + inviteResponse.getRoomName());
                    }
                });
                break;

            default:
                System.out.println("[WARNING] 처리할 수 없는 메시지 타입 " + type);
                break;
            }
        
    }

    private void closeChatRoom(String roomName, String reason) {
        ChatFrame frame = Application.chatFrameMap.remove(roomName);
        if (frame != null) {
            frame.dispose();
        }
        Application.chatPanelMap.remove(roomName);
        Application.chatRoomUserListPanelMap.remove(roomName);
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, reason, "??", JOptionPane.WARNING_MESSAGE);
            if (LobbyFrame.chatRoomListPanel != null) {
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
            }
        });
    }

}