package com.nhnacademy.aiot;

import com.nhnacademy.aiot.MainSystem.System.ServerManager;

public class TicTacToe {
    
    public static void main(String[] args) {
        int port = 12343;
        ServerManager.getInstance().start(port);
    }
    
}
