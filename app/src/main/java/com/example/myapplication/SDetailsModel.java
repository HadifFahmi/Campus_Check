package com.example.myapplication;

public class SDetailsModel {
    private String fullName;
    private String username;
    private String studentid;
    private String email;
    private String phoneNumber;
    private String inOutStatus;
    private String universityName;
    private String fieldOfStudy;
    private String division;

    // Constructor
    public SDetailsModel(String fullName, String username, String studentid,
                         String email, String phoneNumber, String inOutStatus, String universityName,
                         String fieldOfStudy, String division) {
        this.fullName = fullName;
        this.username = username;
        this.studentid = studentid;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.inOutStatus = inOutStatus;
        this.universityName = universityName;
        this.fieldOfStudy = fieldOfStudy;
        this.division = division;
    }

    // Default Constructor
    public SDetailsModel() {}

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStudentId() { return studentid; }
    public void setStudentId(String studentid) { this.studentid = studentid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getInOutStatus() { return inOutStatus; }
    public void setInOutStatus(String inOutStatus) { this.inOutStatus = inOutStatus; }

    public String getUniversityName() { return universityName; }
    public void setUniversityName(String universityName) { this.universityName = universityName; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    @Override
    public String toString() {
        return "SDetailsModel{" +
                "fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", studentid='" + studentid + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", inOutStatus='" + inOutStatus + '\'' +
                ", universityName='" + universityName + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", division='" + division + '\'' +
                '}';
    }
}

