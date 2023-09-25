package com.nhnacademy.aiot.Room.System;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;

import com.nhnacademy.aiot.MainSystem.Connect.Connect;
import com.nhnacademy.aiot.MainSystem.Enum.UserType;
import com.nhnacademy.aiot.MainSystem.MessageTransfer.TransferManager;

public class RoomManager {
    private static RoomManager instance;
    private List<Room> rooms;

    private RoomManager() {
        super();
        rooms = new LinkedList<>();
    }

    public static RoomManager getInstance() {
        if (instance == null)
            instance = new RoomManager();
        return instance;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public int getRoomCount() {
        return rooms.size();
    }

    public Room getRoom(int index) {
        return rooms.get(index);
    }

    public Boolean existRoom(String roomName){
        for(int i = 0;i<getRoomCount();i++){
            if(getRoom(i).getName().equals(roomName)){
                return true;
            }
        }
        return false;
    }

    public String getRoomList() {
        String roomList = "-------------------------------------------\n               Room List           \n-------------------------------------------\nNumber\tName\t\tType\t    User Count\n";
        for (int i = 0; i < getRoomCount(); i++) {
            roomList +=String.format("%-6d  %-14s  %-10s  %2d/%-2d\n",i,getRoom(i).getName(),getRoom(i).getMenuType().name(),getRoom(i).getCurrentUserCount(),getRoom(i).getMaxUserCount());
        }
        roomList += "-------------------------------------------\n";
        return roomList;
    }

    public void enterRoom(String name, Connect connect) {
        for (Room room : getRooms()) {
            if (room.getName().equals(name)) {
                try {
                    room.addUser(connect);
                } catch (UnexpectedException e) {
                    TransferManager.getInstance().sendMessage(e.getMessage(), connect, UserType.SERVER);
                }
                return;
            }
        }
        TransferManager.getInstance().sendMessage("There is no room.", connect, UserType.SERVER);
    }

    public void removeUserEveryRoom(Connect connect) {
        for (Room room : getRooms()) {
            room.removeUser(connect);
        }
    }
}
