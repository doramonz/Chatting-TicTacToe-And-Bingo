package com.nhnacademy.aiot.Room.Bingo;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.GameState;
import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.Room;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class BingoRoom implements Room, Runnable {
    private static final int MAX_USER_COUNT = 10;
    private Thread thread;
    private String name;
    private final MenuType menuType = MenuType.BINGO;
    private List<Connect> userList = new LinkedList<>();
    private Boolean isGameStart = false;
    private Connect turnUser;
    private GameState gameState = GameState.WAITING;
    private List<BingoUserInfo> BingoUserInfoList = new LinkedList<>();
    private int playingUserCount = 0;
    private int BingoBoardSize = 0;
    private int[] isSelectNumber;

    public BingoRoom(String name) {
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
            while (getCurrentUserCount() == getPlayingUserCount() && !thread.isInterrupted()) {
                if (gameState == GameState.SELECTORDER) {
                    selectOrder();
                }
                if (gameState == GameState.MAKEBOARD) {
                }
                if (gameState == GameState.PLAYING) {

                }
                if (gameState == GameState.END) {
                }
            }
            if (getCurrentUserCount() != getPlayingUserCount()
                    && (gameState == GameState.PLAYING || gameState == GameState.SELECTORDER)) {
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
            connect.setMenuType(MenuType.BINGO);
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
        if (getCurrentUserCount() == 1) {
            TransferManager.getInstance().sendMessage("Need more than one user.", connect, UserType.SERVER);
            return;
        }
        if (isGameStart()) {
            TransferManager.getInstance().sendMessage("The game is already started.", connect, UserType.SERVER);
            return;
        }
        playingUserCount = getCurrentUserCount();
        isGameStart = true;
        gameState = GameState.SELECTORDER;
        for (Connect c : userList) {
            BingoUserInfoList.add(new BingoUserInfo(c));
        }
        sendMessageAll("Enter Bingo Board Size", UserType.SERVER);
    }

    public void selectOrder() {
        if (BingoUserInfoList.size() != getPlayingUserCount()) {
            return;
        }
        if (getBingoBoardSize() == 0) {
            return;
        }
        sendMessageAll("Board Size : " + getBingoBoardSize(), UserType.SERVER);
        List<Integer> order = new LinkedList<>();
        for (int i = 0; i < BingoUserInfoList.size(); i++) {
            order.add(i + 1);
        }
        System.out.println(order.size());
        for (int i = 0; i < BingoUserInfoList.size(); i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, order.size());
            BingoUserInfoList.get(i).setOrder(order.get(randomNum));
            order.remove(randomNum);
        }

        for (int i = 0; i < BingoUserInfoList.size(); i++) {
            TransferManager.getInstance().sendMessage("Your order is " + BingoUserInfoList.get(i).getOrder(),
                    BingoUserInfoList.get(i).getConnect(), UserType.SERVER);
        }
        sendMessageAll("enter the number to make board" + " 1 ~ " + getBingoBoardSize() * getBingoBoardSize(),
                UserType.SERVER);
        sendMessageAll("Ex) 1 3 2 5 8 7 9 4 6", UserType.SERVER);
        gameState = GameState.MAKEBOARD;
        isSelectNumber = new int[getBingoBoardSize() * getBingoBoardSize()];
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<BingoUserInfo> getUserInfoList() {
        return BingoUserInfoList;
    }

    public BingoUserInfo getUserInfo(Connect connect) {
        for (BingoUserInfo UserInfo : BingoUserInfoList) {
            if (UserInfo.getConnect() == connect)
                return UserInfo;
        }
        throw new NullPointerException("User " + connect.getName() + " is not exist.");
    }

    public void restartGame(Connect connect) {
        isGameStart = false;
        BingoUserInfoList.clear();
        turnUser = null;
        playingUserCount = 0;
        setBindoBoardSize(0);
        startGame(connect);
    }

    public Boolean isBingo(Connect connect) {
        Boolean[][] checkBoard = getUserInfo(connect).getCheckBoard();
        for (int i = 0; i < getBingoBoardSize(); i++) {
            for (int j = 0; j < getBingoBoardSize(); j++) {
                if (!getUserInfo(connect).getCheckBoard()[i][j]) {
                    break;
                }
                if (j == getBingoBoardSize() - 1) {
                    return true;
                }
            }
        }
        for (int i = 0; i < getBingoBoardSize(); i++) {
            for (int j = 0; j < getBingoBoardSize(); j++) {
                if (!checkBoard[j][i]) {
                    break;
                }
                if (j == getBingoBoardSize() - 1) {
                    return true;
                }
            }
        }
        Boolean crossCheck = true;
        for (int i = 0; i < getBingoBoardSize(); i++) {
            crossCheck &= checkBoard[i][i];
        }
        if (crossCheck) {
            return true;
        }
        crossCheck = true;
        for (int i = 0; i < getBingoBoardSize(); i++) {
            crossCheck &= checkBoard[i][getBingoBoardSize() - i - 1];
        }
        if (crossCheck) {
            return true;
        }
        return false;
    }

    public void selectNumber(Connect connect, int number) {
        if (isSelected(number)) {
            TransferManager.getInstance().sendMessage("Already selected number", connect, UserType.SERVER);
            return;
        }
        isSelectNumber[number - 1] = 1;
        sendMessageAll(connect.getName() + " selected " + number, connect, UserType.SERVER);
        for (BingoUserInfo bingoUserInfo : BingoUserInfoList) {
            int[][] board = bingoUserInfo.getBoard();
            for (int i = 0; i < getBingoBoardSize(); i++) {
                for (int j = 0; j < getBingoBoardSize(); j++) {
                    if (board[i][j] == number) {
                        bingoUserInfo.getCheckBoard()[i][j] = true;
                    }
                }
            }
        }
        Boolean bingo = false;
        for (BingoUserInfo bingoUserInfo : BingoUserInfoList) {
            if (isBingo(bingoUserInfo.getConnect())) {
                bingo = true;
                TransferManager.getInstance().sendMessage("You Make Bingo", bingoUserInfo.getConnect(),
                        UserType.SERVER);
                sendMessageAll(bingoUserInfo.getConnect().getName() + " is Bingo", bingoUserInfo.getConnect(),
                        UserType.SERVER);
                sendMessageAll(
                        "\n" + getBoard(bingoUserInfo.getConnect()),
                        bingoUserInfo.getConnect(), UserType.SERVER);
            }
        }
        if (bingo) {
            gameState = GameState.END;
            sendMessageAll("Game is ended", UserType.SERVER);
            sendMessageAll("Enter !RESTART to restart game", UserType.SERVER);
            return;
        }
        for (BingoUserInfo bingoUserInfo : BingoUserInfoList) {
            TransferManager.getInstance().sendMessage(getBoard(bingoUserInfo.getConnect()), bingoUserInfo.getConnect(),
                    UserType.SERVER);
        }
        turnUser = getNextUser();
        TransferManager.getInstance().sendMessage("Your Turn. !Select <Number>", turnUser, UserType.SERVER);
        sendMessageAll("Turn is " + turnUser.getName(), turnUser, UserType.SERVER);
    }

    private Connect getNextUser() {
        int turn;
        if (getUserInfo(turnUser).getOrder() == BingoUserInfoList.size()) {
            turn = 1;
        } else {
            turn = getUserInfo(turnUser).getOrder() + 1;
        }
        for (BingoUserInfo UserInfo : BingoUserInfoList) {
            if (UserInfo.getOrder() == turn) {
                return UserInfo.getConnect();
            }
        }
        throw new NullPointerException("next user is not exist.");
    }

    public String getBoard(Connect connect) {
        String board = "";
        for (int i = 0; i < getBingoBoardSize(); i++) {
            for (int j = 0; j < getBingoBoardSize(); j++) {
                if (!getUserInfo(connect).getCheckBoard()[i][j]) {
                    board += " " + getUserInfo(connect).getBoard()[i][j] + " ";
                } else {
                    board += " X ";
                }
            }
            board += "\n";
        }
        return board;
    }

    public Connect getTurnUser() {
        return turnUser;
    }

    public int getPlayingUserCount() {
        return playingUserCount;
    }

    public void setBindoBoardSize(int size) {
        BingoBoardSize = size;
    }

    public int getBingoBoardSize() {
        return BingoBoardSize;
    }

    public Boolean checkMakedBoard() {
        for (BingoUserInfo UserInfo : BingoUserInfoList) {
            if (UserInfo.getBoard() == null) {
                return false;
            }
        }
        return true;
    }

    public void makeBoard(String message, Connect connect) {
        String[] parseMessages = message.split(" ");
        int[] numbers = new int[getBingoBoardSize() * getBingoBoardSize()];
        if (parseMessages.length != getBingoBoardSize() * getBingoBoardSize()) {
            TransferManager.getInstance().sendMessage(
                    "Enter " + getBingoBoardSize() * getBingoBoardSize() + " number", connect,
                    UserType.SERVER);
            return;
        }
        for (int i = 0; i < getBingoBoardSize() * getBingoBoardSize(); i++) {
            if (Integer.parseInt(parseMessages[i]) > getBingoBoardSize() * getBingoBoardSize()
                    || Integer.parseInt(parseMessages[i]) < 1) {
                TransferManager.getInstance().sendMessage(
                        "Enter the 1 ~ " + getBingoBoardSize() * getBingoBoardSize() + " Number", connect,
                        UserType.SERVER);
                return;
            }
            if (numbers[Integer.parseInt(parseMessages[i]) - 1] != 0) {
                TransferManager.getInstance().sendMessage("Duplicate Number", connect, UserType.SERVER);
                return;
            }
            numbers[Integer.parseInt(parseMessages[i]) - 1] = Integer.parseInt(parseMessages[i]);
        }
        int[][] board = new int[getBingoBoardSize()][getBingoBoardSize()];
        int index = 0;
        for (int i = 0; i < getBingoBoardSize(); i++) {
            for (int j = 0; j < getBingoBoardSize(); j++) {
                board[i][j] = Integer.parseInt(parseMessages[index]);
                index++;
            }
        }
        getUserInfo(connect).setBoard(board);
        sendMessageAll(connect.getName() + " made board", connect, UserType.SERVER);
        TransferManager.getInstance().sendMessage("Your board is Made", connect, UserType.SERVER);
        TransferManager.getInstance().sendMessage(getBoard(connect), connect, UserType.SERVER);
        if (checkMakedBoard()) {
            gameState = GameState.PLAYING;
            turnUser = userList.get(0);
            sendMessageAll("Game is started", UserType.SERVER);
            sendMessageAll("Turn is " + turnUser.getName(), turnUser, UserType.SERVER);
            TransferManager.getInstance().sendMessage("Your Turn. !Select <Number>", turnUser, UserType.SERVER);
        }
    }

    public Boolean isSelected(int number) {
        if (isSelectNumber[number - 1] == 0) {
            return false;
        }
        return true;
    }
}