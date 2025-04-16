package com.example.myapplication;

public class TDetailsModel {
    private String fullName;
    private String username;
    private String teacherid;
    private String email;
    private String phoneNumber;

    // Constructor
    public TDetailsModel(String fullName, String username, String teacherid, String email, String phoneNumber) {
        this.fullName = fullName;
        this.username = username;
        this.teacherid = teacherid;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Default Constructor
    public TDetailsModel() {}

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTeacherId() { return teacherid; }
    public void setTeacherId(String teacherid) { this.teacherid = teacherid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() {
        return "TDetailsModel{" +
                "fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", identification='" + teacherid + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

