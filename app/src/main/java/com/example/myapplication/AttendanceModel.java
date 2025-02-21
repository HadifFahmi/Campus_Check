package com.example.myapplication;

import com.google.android.gms.tasks.Task;

public class AttendanceModel {
    private String id;
    private String name;
    private String user;
    private String timestamp;
    private String date;

    public AttendanceModel(String id, String name, String user, String timestamp, String date) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.timestamp = timestamp;
        this.date = date;
    }

    public AttendanceModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String studentname) {
        this.name = studentname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String username) {
        this.user = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

