package com.nhnacademy.aiot.MainSystem.MessageTransfer;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.Room.Bingo.BingoManager;
import com.nhnacademy.aiot.Room.Chat.ChatManager;
import com.nhnacademy.aiot.Room.Main.MainManager;
import com.nhnacademy.aiot.Room.TicTacToe.TicTacToeManager;

public class MessageManager {
    private static MessageManager instance;

    private MessageManager() {
        super();
    }

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    public String[] parseMessage(String message) {
        String[] tokens = message.split(" ");
        return tokens;
    }

    public void processMessage(String message, Connect connect) {
        if (connect.getMenuType() == MenuType.LOGIN) {
            connect.setName(message);
        } else if (connect.getMenuType() == MenuType.MAIN) {
            MainManager.getInstance().messageProcess(message, connect);
        } else if (connect.getMenuType() == MenuType.CHAT) {
            ChatManager.getInstance().messageProcess(message, connect);
        } else if (connect.getMenuType() == MenuType.TICTACTOE) {
            TicTacToeManager.getInstance().processMessage(message, connect);
        } else if (connect.getMenuType() == MenuType.BINGO) {
            BingoManager.getInstance().processMessage(message, connect);
        } else {
        }
    }
}
