package com.nhnacademy.aiot.Room.TicTacToe;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;

public class TicUserInfo {
    private Connect connect;
    private int order;
    private String stringOrder;

    public TicUserInfo(Connect connect) {
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

    public String getStringOrder() {
        return stringOrder;
    }

    public void setStringOrder(String stringOrder) {
        this.stringOrder = stringOrder;
    }
}
