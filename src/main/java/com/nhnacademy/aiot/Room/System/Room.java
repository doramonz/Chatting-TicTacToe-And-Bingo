package com.nhnacademy.aiot.Room.System;

import java.rmi.UnexpectedException;
import java.util.List;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.MenuType;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;

public interface Room {
    public String getName();

    public void start();

    public void stop();

    public void setName(String name);

    public int getCurrentUserCount();

    public List<Connect> getUserList();

    public void addUser(Connect connect) throws UnexpectedException;

    public Connect getUser(int index);

    public Connect getUser(String name);

    public void removeUser(Connect connect);

    public void sendMessageAll(String message, Connect fromConnect, UserType userType);

    public void sendMessageAll(String message, UserType userType);

    public int getMaxUserCount();

    public MenuType getMenuType();
}
