package com.nhnacademy.aiot.Room.Chat;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.Room;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class ChatRoom implements Room, Runnable {
    private List<Connect> userList;
    private Thread thread;
    private String name;
    private final MenuType menuType = MenuType.CHAT;
    private static final int MAX_USER_COUNT = 10;

    public ChatRoom(String name) {
        super();
        thread = new Thread(this);
        userList = new LinkedList<>();
        this.name = name;
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!thread.isInterrupted() && getCurrentUserCount() > 0) {

        }
        System.out.println("Room " + getName() + " is closed.");
        RoomManager.getInstance().removeRoom(this);
        stop();
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
            connect.setMenuType(MenuType.CHAT);
            connect.setRoom(this);
            TransferManager.getInstance().sendMessage("Welcome to " + getName() + " room.", connect, UserType.SERVER);
            TransferManager.getInstance().sendMessage("If you need help please enter !help", connect, UserType.SERVER);
            sendMessageAll(connect.getName() + " entered the room", connect, UserType.SERVER);
        } else {
            TransferManager.getInstance().sendMessage("The room is full.", connect, UserType.SERVER);
            throw new UnexpectedException("The room is full.");
        }
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
        for (Connect connect : getUserList()) {
            if (connect != fromConnect)
                TransferManager.getInstance().sendMessage(message, connect, userType);
        }
    }

    @Override
    public void sendMessageAll(String message, UserType userType) {
        for (Connect connect : getUserList()) {
            TransferManager.getInstance().sendMessage(message, connect, userType);
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
}