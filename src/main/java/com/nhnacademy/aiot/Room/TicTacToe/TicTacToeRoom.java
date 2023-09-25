package com.nhnacademy.aiot.Room.TicTacToe;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.GameState;
import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.Room;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class TicTacToeRoom implements Room, Runnable {
    private static final int MAX_USER_COUNT = 2;
    private Thread thread;
    private String name;
    private final MenuType menuType = MenuType.TICTACTOE;
    private List<Connect> userList = new LinkedList<>();
    private Boolean isGameStart = false;
    private Connect turnUser;
    private GameState gameState = GameState.WAITING;
    private List<TicUserInfo> ticUserInfoList = new LinkedList<>();
    private int[][] board = new int[3][3];

    public TicTacToeRoom(String name) {
        super();
        this.name = name;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!thread.isInterrupted() && getCurrentUserCount() > 0) {
            while (getCurrentUserCount() == 2 && !thread.isInterrupted()) {
                if (gameState == GameState.SELECTORDER) {
                    selectOrder();
                }
                if (gameState == GameState.PLAYING) {

                }
                if (gameState == GameState.END) {
                }
            }
            if (getCurrentUserCount() != 2&&(gameState==GameState.PLAYING||gameState==GameState.SELECTORDER)) {
                sendMessageAll("The other user left the room", UserType.SERVER);
                sendMessageAll("Enter !RESTART to restart game", UserType.SERVER);
                gameState = GameState.END;
            }
        }
        System.out.println("Room " + getName() + " is closed.");
        RoomManager.getInstance().removeRoom(this);
        stop();
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getCurrentUserCount() {
        return userList.size();
    }

    @Override
    public List<Connect> getUserList() {
        return userList;
    }

    @Override
    public void addUser(Connect connect) throws UnexpectedException {
        if (userList.size() < MAX_USER_COUNT) {
            userList.add(connect);
            connect.setMenuType(MenuType.TICTACTOE);
            connect.setRoom(this);
            TransferManager.getInstance().sendMessage("Welcome to " + getName() + " room.", connect, UserType.SERVER);
            TransferManager.getInstance().sendMessage("If you need help please enter !help", connect, UserType.SERVER);
            sendMessageAll(connect.getName() + " entered the room", connect, UserType.SERVER);
        } else {
            throw new UnexpectedException("The room is full.");
        }
    }

    @Override
    public Connect getUser(int index) {
        return userList.get(index);
    }

    @Override
    public Connect getUser(String name) {
        for (Connect connect : userList) {
            if (connect.getName().equals(name))
                return connect;
        }
        throw new NullPointerException("User " + name + " is not exist.");
    }

    @Override
    public void removeUser(Connect connect) {
        if (userList.contains(connect)) {
            userList.remove(connect);
            connect.setRoom(null);
            connect.setMenuType(MenuType.MAIN);
            sendMessageAll(connect.getName() + " left the room", connect, UserType.SERVER);
        }
    }

    @Override
    public void sendMessageAll(String message, Connect fromConnect, UserType userType) {
        for (Connect connect : userList) {
            if (connect != fromConnect)
                TransferManager.getInstance().sendMessage(message, connect, userType);
        }
    }

    @Override
    public void sendMessageAll(String message, UserType userType) {
        for (Connect c : userList) {
            TransferManager.getInstance().sendMessage(message, c, userType);
        }
    }

    @Override
    public int getMaxUserCount() {
        return MAX_USER_COUNT;
    }

    @Override
    public MenuType getMenuType() {
        return menuType;
    }

    public Boolean isTurn(Connect connect) {
        return turnUser == connect;
    }

    public Boolean isGameStart() {
        return isGameStart;
    }

    public void startGame(Connect connect) {
        if (getCurrentUserCount() != MAX_USER_COUNT) {
            TransferManager.getInstance().sendMessage("The room is not full.", connect, UserType.SERVER);
            return;
        }
        if (isGameStart()) {
            TransferManager.getInstance().sendMessage("The game is already started.", connect, UserType.SERVER);
            return;
        }
        isGameStart = true;
        gameState = GameState.SELECTORDER;
        for (Connect c : userList) {
            ticUserInfoList.add(new TicUserInfo(c));
        }
        sendMessageAll("Enter 'Rock','Scissor','Paper' To select Order", UserType.SERVER);
    }

    public void selectOrder() {
        if (ticUserInfoList.size() != 2) {
            return;
        }
        for (TicUserInfo ticUserInfo : ticUserInfoList) {
            if (ticUserInfo.getStringOrder() == null) {
                return;
            }
        }
        TicUserInfo user1 = ticUserInfoList.get(0);
        TicUserInfo user2 = ticUserInfoList.get(1);

        if (user1.getStringOrder().equals("ROCK")) {
            if (user2.getStringOrder().equals("ROCK")) {
                sendMessageAll("Draw! Retry", UserType.SERVER);
                ticUserInfoList.get(0).setStringOrder(null);
                ticUserInfoList.get(1).setStringOrder(null);
                return;
            } else if (user2.getStringOrder().equals("PAPER")) {
                sendMessageAll(user2.getConnect().getName() + " Win", UserType.SERVER);
                gameState = GameState.PLAYING;
                user2.setOrder(1);
                user1.setOrder(2);
            } else if (user2.getStringOrder().equals("SCISSOR")) {
                sendMessageAll(user1.getConnect().getName() + " Win", UserType.SERVER);
                gameState = GameState.PLAYING;
                user1.setOrder(1);
                user2.setOrder(2);
            } else {
            }
        } else if (user1.getStringOrder().equals("PAPER")) {
            if (user2.getStringOrder().equals("ROCK")) {
                sendMessageAll(user1.getConnect().getName() + " Win", UserType.SERVER);
                gameState = GameState.PLAYING;
                user1.setOrder(1);
                user2.setOrder(2);
            } else if (user2.getStringOrder().equals("PAPER")) {
                sendMessageAll("Draw! Retry", UserType.SERVER);
                ticUserInfoList.get(0).setStringOrder(null);
                ticUserInfoList.get(1).setStringOrder(null);
                return;
            } else if (user2.getStringOrder().equals("SCISSOR")) {
                sendMessageAll(user2.getConnect().getName() + " Win", UserType.SERVER);
                gameState = GameState.PLAYING;
                user2.setOrder(1);
                user1.setOrder(2);
            } else {
            }
        } else if (user1.getStringOrder().equals("SCISSOR")) {
            if (user2.getStringOrder().equals("ROCK")) {
                sendMessageAll(user2.getConnect().getName() + " Win", UserType.SERVER);
                gameState = GameState.PLAYING;
                user2.setOrder(1);
                user1.setOrder(2);
            } else if (user2.getStringOrder().equals("PAPER")) {
                sendMessageAll(user1.getConnect().getName() + " Win", UserType.SERVER);
                gameState = GameState.PLAYING;
                user1.setOrder(1);
                user2.setOrder(2);
            } else if (user2.getStringOrder().equals("SCISSOR")) {
                sendMessageAll("Draw! Retry", UserType.SERVER);
                ticUserInfoList.get(0).setStringOrder(null);
                ticUserInfoList.get(1).setStringOrder(null);
                return;
            } else {
            }
        } else {
        }
        for (TicUserInfo ticUserInfo : ticUserInfoList) {
            if (ticUserInfo.getOrder() == 1) {
                turnUser = ticUserInfo.getConnect();
                TransferManager.getInstance().sendMessage("You are first", turnUser, UserType.SERVER);
            } else {
                TransferManager.getInstance().sendMessage("You are second", ticUserInfo.getConnect(), UserType.SERVER);
            }
        }
        sendMessageAll("enter !Set <x> <y>", UserType.SERVER);
        sendMessageAll(printTile(), UserType.SERVER);
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<TicUserInfo> getTicUserInfoList() {
        return ticUserInfoList;
    }

    public TicUserInfo getUserInfo(Connect connect) {
        for (TicUserInfo ticUserInfo : ticUserInfoList) {
            if (ticUserInfo.getConnect() == connect)
                return ticUserInfo;
        }
        throw new NullPointerException("User " + connect.getName() + " is not exist.");
    }

    public void restartGame(Connect connect) {
        isGameStart = false;
        ticUserInfoList.clear();
        turnUser = null;
        board = new int[3][3];
        startGame(connect);
    }

    public void setTile(Connect connect, int x, int y) {
        if (isTurn(connect)) {
            if (board[x - 1][y - 1] == 0) {
                board[x - 1][y - 1] = getUserInfo(connect).getOrder();
                sendMessageAll(printTile(), UserType.SERVER);
                if (isWin()) {
                    sendMessageAll(connect.getName() + " Win", UserType.SERVER);
                    sendMessageAll("Enter !RESTART to restart game", UserType.SERVER);
                    gameState = GameState.END;
                } else if (isDraw()) {
                    sendMessageAll("Draw", connect, UserType.SERVER);
                    gameState = GameState.END;
                } else {
                    turnUser = getAnotherUser(connect);
                    TransferManager.getInstance().sendMessage("Your turn", turnUser, UserType.SERVER);
                }
            } else {
                TransferManager.getInstance().sendMessage("The tile is already set", connect, UserType.SERVER);
            }
        } else {
            TransferManager.getInstance().sendMessage("It's not your turn", connect, UserType.SERVER);
        }
    }

    private Connect getAnotherUser(Connect connect) {
        for (Connect c : userList) {
            if (c != connect) {
                return c;
            }
        }
        throw new NullPointerException("User " + connect.getName() + " is not exist.");
    }

    private boolean isDraw() {
        for (int[] y : board) {
            for (int x : y) {
                if (x == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] != 0) {
                return true;
            }
            if (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] != 0) {
                return true;
            }
        }
        if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] != 0) {
            return true;
        }
        if (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] != 0) {
            return true;
        }
        return false;
    }

    public String printTile() {
        String tile = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    tile += "[ ]";
                } else if (board[i][j] == 1) {
                    tile += "[O]";
                } else if (board[i][j] == 2) {
                    tile += "[X]";
                }
            }
            tile += "\n";
        }
        return tile;
    }

    public Connect getTurnUser() {
        return turnUser;
    }
}