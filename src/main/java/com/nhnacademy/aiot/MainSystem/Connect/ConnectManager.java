package com.nhnacademy.aiot.MainSystem.Connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageReceiver;
import com.nhnacademy.aiot.Room.System.RoomManager;

public class ConnectManager {
    private static ConnectManager instance;
    private ServerSocket serverSocket;
    static List<Connect> connects = new LinkedList<>();

    private ConnectManager() {
        super();
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            ConnectReceiver connectReceiver = new ConnectReceiver(serverSocket);
            connectReceiver.start();
            System.out.println("Server started.");
        } catch (IOException e) {
            System.out.println("Failed to create a server socket.");
            e.printStackTrace();
        }
    }

    public static ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManager();
        }
        return instance;
    }

    public Connect getConnect(MessageReceiver messageReceiver) {
        for (Connect connect : connects) {
            if (connect.getMessageReceiver() == messageReceiver) {
                return connect;
            }
        }
        System.out.println("Failed to find a connect.");
        return null;
    }

    public List<Connect> getConnects() {
        return connects;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void disconnectConnect(Connect connect) {
        if (connect.getName() != null) {
            System.out.println(connect.getUserInetAdressAndPort()
                    + " [" + connect.getName() + "]" + " disconnected.");
        } else {
            System.out.println(connect.getUserInetAdressAndPort()
                    + " disconnected.");
        }
        RoomManager.getInstance().removeUserEveryRoom(connect);
        connect.closeSocket();
        connects.remove(connect);
    }
}
