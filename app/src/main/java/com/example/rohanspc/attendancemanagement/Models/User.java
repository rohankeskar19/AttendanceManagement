package com.example.rohanspc.attendancemanagement.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{

    private String name;
    private String email;
    private String userID;
    private String accountType;
    private ArrayList<String> classroomID;


    public User(String name, String email, String userID, String accountType,  ArrayList<String> classroomID) {
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.accountType = accountType;
        this.classroomID = classroomID;
    }

    public User(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public ArrayList<String> getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(ArrayList<String> classroomID) {
        this.classroomID = classroomID;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userID='" + userID + '\'' +
                ", accountType='" + accountType + '\'' +
                ", classroomID=" + classroomID +
                '}';
    }
}
