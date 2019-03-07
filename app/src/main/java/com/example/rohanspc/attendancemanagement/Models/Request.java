package com.example.rohanspc.attendancemanagement.Models;

public class Request {
    private String fromUserID;
    private String toUserID;
    private String ClassroomID;
    private String state;
    private String requestID;
    private String fromUserName;
    private String classroomName;

    public Request(){

    }

    public Request(String fromUserID, String toUserID, String classroomID, String state, String requestID, String fromUserName, String classroomName) {
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        ClassroomID = classroomID;
        this.state = state;
        this.requestID = requestID;
        this.fromUserName = fromUserName;
        this.classroomName = classroomName;
    }

    public String getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getClassroomID() {
        return ClassroomID;
    }

    public void setClassroomID(String classroomID) {
        ClassroomID = classroomID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    @Override
    public String toString() {
        return "Request{" +
                "fromUserID='" + fromUserID + '\'' +
                ", toUserID='" + toUserID + '\'' +
                ", ClassroomID='" + ClassroomID + '\'' +
                ", state='" + state + '\'' +
                ", requestID='" + requestID + '\'' +
                ", fromUserName='" + fromUserName + '\'' +
                ", classroomName='" + classroomName + '\'' +
                '}';
    }
}
