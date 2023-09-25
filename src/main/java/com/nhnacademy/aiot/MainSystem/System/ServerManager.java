package com.nhnacademy.aiot.MainSystem.System;

import com.nhnacademy.aiot.TicTacToe;
import com.nhnacademy.aiot.MainSystem.Connect.ConnectManager;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageManager;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.MessageSender;
import com.nhnacademy.aiot.Room.Bingo.BingoManager;
import com.nhnacademy.aiot.Room.Chat.ChatManager;
import com.nhnacademy.aiot.Room.Main.MainManager;
import com.nhnacademy.aiot.Room.TicTacToe.TicTacToeManager;

public class ServerManager implements Runnable {
    private ConnectManager connectManager;
    private MessageSender messageSender;
    private MessageManager messageManager;
    private MainManager mainManager;
    private ChatManager chatRoomManager;
    private TicTacToeManager ticTacToeManager;
    private BingoManager bingoManager;
    private static ServerManager instance;
    private Thread thread;

    private ServerManager() {
        super();
        thread = new Thread(this);
        connectManager = ConnectManager.getInstance();
        messageSender = MessageSender.getInstance();
        messageManager = MessageManager.getInstance();
        mainManager = MainManager.getInstance();
        chatRoomManager = ChatManager.getInstance();
        ticTacToeManager = TicTacToeManager.getInstance();
        bingoManager = BingoManager.getInstance();
    }

    public static ServerManager getInstance() {
        if (instance == null)
            instance = new ServerManager();
        return instance;
    }

    public void start(int port) {
        connectManager.start(port);
        messageSender.start();
        thread.start();
    }

    public void stop(){
        thread.interrupt();
    }

    @Override
    public void run() {
        while (true) {

        }
    }

}
