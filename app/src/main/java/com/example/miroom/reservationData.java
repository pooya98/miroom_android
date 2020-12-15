package com.example.miroom;

public class reservationData {
    private String reserve_id;
    private String date;
    private String time;
    private String layer;
    private String room_name;

    public reservationData(String reserve_id, String date, String time, String layer, String room_name) {
        this.reserve_id = reserve_id;
        this.date = date;
        this.time = time;
        this.layer = layer;
        this.room_name = room_name;
    }

    public String getReserve_id() {
        return reserve_id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLayer() {
        return layer;
    }

    public String getRoom_name() {
        return room_name;
    }
}
