package com.example.rohanspc.attendancemanagement.Models;

public class GraphData {
    String Date;
    int presentCount;
    int x,y;

    public GraphData(){

    }

    public GraphData(String date, int presentCount, int x, int y) {
        Date = date;
        this.presentCount = presentCount;
        this.x = x;
        this.y = y;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "GraphData{" +
                "Date='" + Date + '\'' +
                ", presentCount=" + presentCount +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
