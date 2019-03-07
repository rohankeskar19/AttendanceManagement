package com.example.rohanspc.attendancemanagement.Models;

import java.io.Serializable;

public class ClassroomUser implements Serializable{
    String name;
    String userID;
    String email;
    String accountType;
    int rollNo;
    boolean present;

    public ClassroomUser(){

    }

    public ClassroomUser(String name, String userID, String email, String accountType, int rollNo, boolean present) {
        this.name = name;
        this.userID = userID;
        this.email = email;
        this.accountType = accountType;
        this.rollNo = rollNo;
        this.present = present;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public int getRollNo() {
        return rollNo;
    }

    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public String toString() {
        return "ClassroomUser{" +
                "name='" + name + '\'' +
                ", userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", accountType='" + accountType + '\'' +
                ", rollNo=" + rollNo +
                ", present=" + present +
                '}';
    }
}
