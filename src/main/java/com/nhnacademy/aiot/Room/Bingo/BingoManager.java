package com.nhnacademy.aiot.Room.Bingo;

import java.rmi.UnexpectedException;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.GameState;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageManager;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class BingoManager {
    private static BingoManager instance;

    private BingoManager() {
        super();
    }

    public static BingoManager getInstance() {
        if (instance == null) {
            instance = new BingoManager();
        }
        return instance;
    }

    public String showEnteredUser(Connect connect) {
        String userList = "-------------------------------------------\n               User List           \n-------------------------------------------\nNumber\tName\n";
        for (int i = 0; i < connect.getRoom().getCurrentUserCount(); i++) {
            userList += i + "\t" + connect.getRoom().getUser(i).getName() + "\n";
        }
        userList += "-------------------------------------------\n";
        return userList;
    }

    public void createRoom(String name, Connect connect) {
        BingoRoom BingoRoom = new BingoRoom(name);
        try {
            BingoRoom.addUser(connect);
            RoomManager.getInstance().addRoom(BingoRoom);
            BingoRoom.start();
        } catch (UnexpectedException e) {
            System.out.println(e.getMessage());
        }
    }

    public BingoRoom getRoom(Connect connect) {
        return (BingoRoom) connect.getRoom();
    }

    public void processMessage(String message, Connect connect) {
        String[] parseMessages = MessageManager.getInstance().parseMessage(message);
        if (parseMessages[0].toUpperCase().equals("!HELP")) {
            TransferManager.getInstance().sendMessage(
                    "------Help------\n@USERNAME send message to user\n!List Show Entered Users\n!START Start game\n!RESTART Restart game!SELECT <NUMBER> Select Tile\n!Exit exit the room",
                    connect, UserType.SERVER);
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
        } else if (parseMessages[0].toUpperCase().equals("!START")) {
            if (getRoom(connect).getGameState() == GameState.WAITING) {
                getRoom(connect).startGame(connect);
            } else {
                if (getRoom(connect).getGameState() == GameState.SELECTORDER
                        || getRoom(connect).getGameState() == GameState.PLAYING) {
                    TransferManager.getInstance().sendMessage("Game is already started", connect, UserType.SERVER);
                } else if (getRoom(connect).getGameState() == GameState.END) {
                    TransferManager.getInstance().sendMessage("Game is ended. Enter !Restart", connect,
                            UserType.SERVER);
                }
            }
        } else if (parseMessages[0].toUpperCase().equals("!RESTART")) {
            if (getRoom(connect).getGameState() == GameState.PLAYING
                    || getRoom(connect).getGameState() == GameState.END) {
                getRoom(connect).restartGame(connect);
            } else {
                TransferManager.getInstance().sendMessage("Restart can be used at playing or end", connect,
                        UserType.SERVER);
            }

        } else if (parseMessages[0].toUpperCase().equals("!SELECT")) {
            if (getRoom(connect).getGameState() == GameState.PLAYING) {
                if (getRoom(connect).isTurn(connect)) {
                    if (Integer.parseInt(parseMessages[1]) > 0 && Integer.parseInt(parseMessages[1]) < 26) {
                        getRoom(connect).selectNumber(connect, Integer.parseInt(parseMessages[1]));
                    } else {
                        TransferManager.getInstance().sendMessage("Wrong input. Tile number must be (1~25)", connect,
                                UserType.SERVER);
                    }
                } else {
                    TransferManager.getInstance().sendMessage("It's not your turn", connect,
                            UserType.SERVER);
                }
            } else {
                TransferManager.getInstance().sendMessage("Game is not started", connect, UserType.SERVER);
            }
        } else {
            if (getRoom(connect).getGameState() == GameState.SELECTORDER) {
                if (Integer.parseInt(parseMessages[0]) > 2 && Integer.parseInt(parseMessages[0]) < 21) {
                    getRoom(connect).setBindoBoardSize(Integer.parseInt(parseMessages[0]));
                } else {
                    TransferManager.getInstance().sendMessage("Wrong input. Board size must be (3~20)", connect,
                            UserType.SERVER);
                }
            } else if (getRoom(connect).getGameState() == GameState.MAKEBOARD) {
                getRoom(connect).makeBoard(message, connect);
            } else {
                getRoom(connect).sendMessageAll(connect.getName() + " : " + message, connect,
                        UserType.CLIENT);
            }
        }
    }
}