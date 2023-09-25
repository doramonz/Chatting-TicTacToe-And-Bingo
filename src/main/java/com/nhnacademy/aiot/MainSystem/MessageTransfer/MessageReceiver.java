package com.nhnacademy.aiot.MainSystem.MessageTransfer;

import java.io.IOException;
import java.io.InputStream;

import com.nhnacademy.aiot.MainSystem.Connect.ConnectManager;

public class MessageReceiver implements Runnable {
    Thread thread;
    InputStream inputStream;

    public MessageReceiver(InputStream inputStream) {
        super();
        thread = new Thread(this);
        this.inputStream = inputStream;
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (!thread.isInterrupted()) {
            try {
                int length = inputStream.read(buffer);
                if (length == -1) {
                    throw new IOException();
                }
                String message = new String(buffer, 0, length);
                System.out.println(
                        ConnectManager.getInstance().getConnect(this).getUserInetAdressAndPort() + " : " + message);
                MessageManager.getInstance().processMessage(message, ConnectManager.getInstance().getConnect(this));
            } catch (IOException e) {
                ConnectManager.getInstance().disconnectConnect(ConnectManager.getInstance().getConnect(this));
                thread.interrupt();
            }
        }
    }
}
