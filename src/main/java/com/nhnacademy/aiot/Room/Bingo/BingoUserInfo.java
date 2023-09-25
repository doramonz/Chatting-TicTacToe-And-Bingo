package com.nhnacademy.aiot.Room.Bingo;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;

public class BingoUserInfo {
    private Connect connect;
    private int order;
    private int[][] board;
    private Boolean[][] checkBoard;

    public BingoUserInfo(Connect connect) {
        super();
        this.connect = connect;
    }

    public Connect getConnect() {
        return connect;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
        checkBoard = new Boolean[board.length][board.length];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)
                checkBoard[i][j] = false;
    }

    public Boolean[][] getCheckBoard() {
        return checkBoard;
    }
}
