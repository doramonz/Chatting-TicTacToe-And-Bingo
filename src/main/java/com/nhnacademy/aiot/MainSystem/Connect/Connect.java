package com.nhnacademy.aiot.MainSystem.Connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageReceiver;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;
import com.nhnacademy.aiot.Room.System.Room;

public class Connect {
    private InputStream inputStream;
    private OutputStream outputStream;
    private MessageReceiver messageReceiver;
    private Socket socket;
    private String name = null;
    private Room room = null;
    private UserType userType = UserType.CLIENT;
    private MenuType menuType = MenuType.LOGIN;

    public Connect(Socket socket) throws IOException {
        super();
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        messageReceiver = new MessageReceiver(inputStream);
        messageReceiver.start();
        TransferManager.getInstance().sendMessage("Input your name : ", this, UserType.SERVER);
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public MessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    public String getUserInetAdressAndPort() {
        return socket.getInetAddress() + " : " + socket.getLocalPort();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.menuType = MenuType.MAIN;
        TransferManager.getInstance().sendMessage("Welcome, " + name + ". If you need help please enter !help", this,
                UserType.SERVER);
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Failed to close a socket.");
            e.printStackTrace();
        }
    }

    public UserType getUserType() {
        return userType;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }
}
