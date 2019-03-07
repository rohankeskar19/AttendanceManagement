package com.example.rohanspc.attendancemanagement.Models;

public class Notification {
    String title;
    String description;
    int hour;
    int miinute;
    String date;

    public Notification(){

    }

    public Notification(String title, String description, int hour, int miinute, String date) {
        this.title = title;
        this.description = description;
        this.hour = hour;
        this.miinute = miinute;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMiinute() {
        return miinute;
    }

    public void setMiinute(int miinute) {
        this.miinute = miinute;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", hour=" + hour +
                ", miinute=" + miinute +
                ", date=" + date +
                '}';
    }
}
