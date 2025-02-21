package com.example.myapplication;

public   class StudentModel {
    public  String id, name, user, pass;

    public StudentModel(String id, String name, String user, String pass) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.pass = pass;
    }

    public StudentModel( ) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
