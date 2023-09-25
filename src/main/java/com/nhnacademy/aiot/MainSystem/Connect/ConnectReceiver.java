package com.nhnacademy.aiot.MainSystem.Connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectReceiver implements Runnable {
    Thread thread;
    ServerSocket serverSocket;

    public ConnectReceiver(ServerSocket serverSocket) {
        super();
        thread = new Thread(this);
        this.serverSocket = serverSocket;
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + " : " + socket.getLocalPort() + " connected.");
                Connect connect = new Connect(socket);
                ConnectManager.getInstance().getConnects().add(connect);
            } catch (IOException e) {
                System.out.println("Failed to accept a socket.");
                e.printStackTrace();
            }
        }
    }
}
