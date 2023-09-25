package com.nhnacademy.aiot.Room.TicTacToe;

import java.rmi.UnexpectedException;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.GameState;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageManager;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class TicTacToeManager {
    private static TicTacToeManager instance;

    private TicTacToeManager() {
        super();
    }

    public static TicTacToeManager getInstance() {
        if (instance == null) {
            instance = new TicTacToeManager();
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
        TicTacToeRoom ticTacToeRoom = new TicTacToeRoom(name);
        try {
            ticTacToeRoom.addUser(connect);
            RoomManager.getInstance().addRoom(ticTacToeRoom);
            ticTacToeRoom.start();
        } catch (UnexpectedException e) {
            System.out.println(e.getMessage());
        }
    }

    public TicTacToeRoom getRoom(Connect connect) {
        return (TicTacToeRoom) connect.getRoom();
    }

    public void processMessage(String message, Connect connect) {
        String[] parseMessages = MessageManager.getInstance().parseMessage(message);
        if (parseMessages[0].toUpperCase().equals("!HELP")) {
            TransferManager.getInstance().sendMessage(
                    "------Help------\n@USERNAME send message to user\n!List Show Entered Users\n!START Start game\n!RESTART Restart game!SET <X> <Y> Select tile\n!Exit exit the room",
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

        } else if (parseMessages[0].toUpperCase().equals("!SET")) {
            if (getRoom(connect).getGameState() == GameState.PLAYING) {
                try {
                    int y = Integer.parseInt(parseMessages[1]);
                    int x = Integer.parseInt(parseMessages[2]);
                    if (x > 0 && x < 4 && y > 0 && y < 4) {
                        getRoom(connect).setTile(connect, x, y);
                    } else {
                        TransferManager.getInstance().sendMessage("Wrong input", connect, UserType.SERVER);
                    }
                } catch (NumberFormatException e) {
                    TransferManager.getInstance().sendMessage("Wrong input", connect, UserType.SERVER);
                    return;
                } catch (ArrayIndexOutOfBoundsException e) {
                    TransferManager.getInstance().sendMessage("Wrong input", connect, UserType.SERVER);
                    return;
                }
            } else {
                TransferManager.getInstance().sendMessage("Game is not started", connect, UserType.SERVER);
            }
        } else {
            if (getRoom(connect).getGameState() == GameState.SELECTORDER) {
                if (getRoom(connect).getUserInfo(connect).getStringOrder() == null) {
                    if (message.toUpperCase().equals("ROCK") || message.toUpperCase().equals("PAPER")
                            || message.toUpperCase().equals("SCISSOR")) {
                        getRoom(connect).getUserInfo(connect).setStringOrder(message.toUpperCase());
                        TransferManager.getInstance().sendMessage("You selected " + message.toUpperCase(), connect,
                                UserType.SERVER);
                    } else {
                        TransferManager.getInstance().sendMessage(
                                "Wrong Order. Enter the correct Order(ROCK, PAPER, SCISSOR)", connect,
                                UserType.SERVER);
                    }
                } else {
                    TransferManager.getInstance().sendMessage("You already selected order. Wait for another user",
                            connect, UserType.SERVER);
                }
            } else {
                getRoom(connect).sendMessageAll(connect.getName() + " : " + message, connect,
                        UserType.CLIENT);
            }
        }
    }
}