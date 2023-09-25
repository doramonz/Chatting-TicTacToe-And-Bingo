package com.nhnacademy.aiot.Room.Chat;

import java.rmi.UnexpectedException;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageManager;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class ChatManager {
    private static ChatManager instance;

    private ChatManager() {
        super();
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void createRoom(String roomName, Connect connect) {
        ChatRoom chatRoom = new ChatRoom(roomName);
        try {
            chatRoom.addUser(connect);
            RoomManager.getInstance().addRoom(chatRoom);
            chatRoom.start();
        } catch (UnexpectedException e) {
            System.out.println(e.getMessage());
        }
    }

    public String showEnteredUser(Connect connect) {
        String userList = "-------------------------------------------\n               User List           \n-------------------------------------------\nNumber\tName\n";
        for (int i = 0; i < connect.getRoom().getCurrentUserCount(); i++) {
            userList += i + "\t" + connect.getRoom().getUser(i).getName() + "\n";
        }
        userList += "-------------------------------------------\n";
        return userList;
    }

    public void messageProcess(String message, Connect connect) {
        String[] parseMessages = MessageManager.getInstance().parseMessage(message);
        if (parseMessages[0].toUpperCase().equals("!HELP")) {
            TransferManager.getInstance().sendMessage(
                    "------Help------\n@USERNAME send message to user\n!List Show Entered Users\n!Exit exit the room",
                    connect,
                    UserType.SERVER);
        } else if (parseMessages[0].startsWith("@")) {
            String userName = parseMessages[0].substring(1);
            for (Connect user : connect.getRoom().getUserList()) {
                if (user.getName().equals(userName)) {
                    TransferManager.getInstance().sendMessage(connect.getName() + " : " + parseMessages[1], user,
                            UserType.CLIENT);
                    return;
                }
            }
            TransferManager.getInstance().sendMessage("User " + userName + " is not exist.", connect, UserType.SERVER);
        } else if (parseMessages[0].toUpperCase().equals("!LIST")) {
            TransferManager.getInstance().sendMessage(showEnteredUser(connect), connect, UserType.SERVER);
        } else if (parseMessages[0].toUpperCase().equals("!EXIT")) {
            connect.getRoom().removeUser(connect);
            TransferManager.getInstance().sendMessage("Exit the room", connect, UserType.SERVER);
        } else {
            ((ChatRoom) connect.getRoom()).sendMessageAll(connect.getName() + " : " + message, connect,
                    UserType.CLIENT);
        }
    }
}
