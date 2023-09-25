package com.nhnacademy.aiot.Room.Main;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageManager;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.Bingo.BingoManager;
import com.nhnacademy.aiot.Room.Chat.ChatManager;
import com.nhnacademy.aiot.Room.System.RoomManager;
import com.nhnacademy.aiot.Room.TicTacToe.TicTacToeManager;

public class MainManager {
    private static MainManager instance;

    private MainManager() {
        super();
    }

    public static MainManager getInstance() {
        if (instance == null) {
            instance = new MainManager();
        }
        return instance;
    }

    public void messageProcess(String message, Connect connect) {
        String[] parseMessages = MessageManager.getInstance().parseMessage(message);
        if (parseMessages[0].toUpperCase().equals("!HELP")) {
            TransferManager.getInstance().sendMessage(
                    "------Help------\n!List : Show the opend room list.\n!Change <UserName> : Change User Name.\n!help : Show help.\n!Room <TYPE> <NAME> : Make room. (Type : Chat, TICTACTOE)\n!Exit : Exit.",
                    connect, UserType.SERVER);
        } else if (parseMessages[0].toUpperCase().equals("!LIST")) {
            TransferManager.getInstance().sendMessage(RoomManager.getInstance().getRoomList(), connect,
                    UserType.SERVER);
        } else if (parseMessages[0].toUpperCase().equals("!CHANGE")) {
            connect.setMenuType(MenuType.LOGIN);
            TransferManager.getInstance().sendMessage("Input your name : ", connect, UserType.SERVER);
        } else if (parseMessages[0].toUpperCase().equals("!HELP")) {
            TransferManager.getInstance().sendMessage(
                    "------Help------\n!List : Show the opend room list.\n!Change <UserName> : Change User Name.\n!help : Show help.\n!Room <Room Type> <Room Name> : Make chat room.(Type : Chat, TicTacToe, Bingo)\n!Enter <Room Name> : Enter the room\n!Exit : Exit.",
                    connect, UserType.SERVER);
        } else if (parseMessages[0].toUpperCase().equals("!ROOM")) {
            if (parseMessages.length == 1) {
                TransferManager.getInstance().sendMessage("Please enter the room Type.", connect, UserType.SERVER);
            } else {
                if (parseMessages[1].toUpperCase().equals("CHAT")) {
                    if (message.length() > 11) {
                        if (!RoomManager.getInstance().existRoom(message.substring(11))) {
                            ChatManager.getInstance().createRoom(message.substring(11), connect);
                        } else {
                            TransferManager.getInstance().sendMessage("The room name is already exist.", connect,
                                    UserType.SERVER);
                        }
                    } else {
                        TransferManager.getInstance().sendMessage("Please enter the room name.", connect,
                                UserType.SERVER);
                    }
                } else if (parseMessages[1].toUpperCase().equals("TICTACTOE")) {
                    if (message.length() > 16) {
                        if (!RoomManager.getInstance().existRoom(message.substring(16))) {
                            TicTacToeManager.getInstance().createRoom(message.substring(16), connect);
                        } else {
                            TransferManager.getInstance().sendMessage("The room name is already exist.", connect,
                                    UserType.SERVER);
                        }
                    } else {
                        TransferManager.getInstance().sendMessage("Please enter the room name.", connect,
                                UserType.SERVER);
                    }
                } else if (parseMessages[1].toUpperCase().equals("BINGO")) {
                    if (message.length() > 11) {
                        if (!RoomManager.getInstance().existRoom(message.substring(12))) {
                            BingoManager.getInstance().createRoom(message.substring(12), connect);
                        } else {
                            TransferManager.getInstance().sendMessage("The room name is already exist.", connect,
                                    UserType.SERVER);
                        }
                    } else {
                        TransferManager.getInstance().sendMessage("Please enter the room name.", connect,
                                UserType.SERVER);
                    }
                } else {
                    TransferManager.getInstance().sendMessage(
                            "Wrong Room Type. Enter the correct Room Type(CHAT, TICTACTOE)", connect,
                            UserType.SERVER);
                }
            }
        } else if (parseMessages[0].toUpperCase().equals("!ENTER")) {
            if (parseMessages.length == 1) {
                TransferManager.getInstance().sendMessage("Please enter the room name.", connect,
                        UserType.SERVER);
            } else {
                RoomManager.getInstance().enterRoom(message.substring(7), connect);
            }
        } else if (parseMessages[0].toUpperCase().equals("!EXIT")) {
        } else {
            TransferManager.getInstance().sendMessage("Wrong Command!!! If you need help please enter !help", connect,
                    UserType.SERVER);
        }
    }
}
