package com.example.rohanspc.attendancemanagement.Models;

public class Attendance {

    String userID;
    String date;
    boolean present;

    public Attendance(){

    }

    public Attendance(String userID, String date, boolean present) {
        this.userID = userID;
        this.date = date;
        this.present = present;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "userID='" + userID + '\'' +
                ", date='" + date + '\'' +
                ", present=" + present +
                '}';
    }
}
