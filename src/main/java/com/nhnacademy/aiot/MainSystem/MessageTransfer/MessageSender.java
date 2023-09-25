package com.nhnacademy.aiot.MainSystem.MessageTransfer;

import java.util.Scanner;

import com.nhnacademy.aiot.MainSystem.Enum.UserType;

public class MessageSender implements Runnable {
    private static MessageSender instance;
    private Thread thread;
    private Scanner scanner;

    private MessageSender() {
        super();
        thread = new Thread(this);
        scanner = new Scanner(System.in);
    }

    public static MessageSender getInstance() {
        if (instance == null)
            instance = new MessageSender();
        return instance;
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            String message = scanner.nextLine();
            TransferManager.getInstance().sendMessageAll(message, null, UserType.SERVER);
        }
    }
}
