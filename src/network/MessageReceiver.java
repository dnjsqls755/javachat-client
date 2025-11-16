package network;

import app.Application;
import domain.ChatRoom;
import dto.response.*;
import dto.type.DtoType;
import view.frame.LobbyFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageReceiver extends Thread {

    Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = reader.readLine();
                if (str == null) {
                    try {
                        socket.close();
                        System.out.println(Application.me.getNickName() + "'s socket is closed.");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("disconnect");
                    System.exit(1);
                }
                System.out.println(str);
                String[] token = str.split(":");

                try {
                    DtoType type = DtoType.valueOf(token[0]);
                    String message = token.length > 1 ? token[1] : "";
                    processReceivedMessage(type, message);
                } catch (IllegalArgumentException e) {
                    System.out.println("정의되지 않은 DtoType: " + token[0]);
                    // 예외 처리: 무시하거나 사용자에게 알림
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
        System.out.println(message);

        switch (type) {
            case LOGIN:
                InitResponse initRes = new InitResponse(message);
                Application.chatRooms = initRes.getChatRooms();
                Application.users = initRes.getUsers();

                LobbyFrame.chatRoomUserListPanel.paintChatUsers(Application.users);
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
                break;

            case MESSAGE:
                MessageResponse messageRes = new MessageResponse(message);
                Application.chatPanelMap.get(messageRes.getChatRoomName()).addMessage(messageRes.getMessageType(), messageRes.getUserName(), messageRes.getMessage());
                break;

            case CREATE_CHAT:
                CreateChatRoomResponse createChatRoomResponse = new CreateChatRoomResponse(message);
                String chatRoomName = createChatRoomResponse.getName();

                ChatRoom newChatRoom = new ChatRoom(chatRoomName);
                Application.chatRooms.add(newChatRoom);

                LobbyFrame.chatRoomListPanel.addChatRoomLabel(chatRoomName);
                break;

            case USER_LIST:
                UserListResponse userListRes = new UserListResponse(message);
                Application.chatRoomUserListPanelMap.get(userListRes.getChatRoomName()).paintChatUsers(userListRes.getUsers());
                break;

            case CHAT_ROOM_LIST:
                ChatRoomListResponse chatRoomListRes = new ChatRoomListResponse(message);
                Application.chatRooms = chatRoomListRes.getChatRooms();
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
                break;
        }
    }
}
