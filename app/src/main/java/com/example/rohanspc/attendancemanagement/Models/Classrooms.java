package com.example.rohanspc.attendancemanagement.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Classrooms implements Serializable{
    private String name;
    private String id;
    private ArrayList<String> subjects;
    private String author;


    public Classrooms(){


    }

    public Classrooms(String name, String id, ArrayList<String> subjects, String author) {
        this.name = name;
        this.id = id;
        this.subjects = subjects;
        this.author = author;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Classrooms{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", subjects=" + subjects +
                ", author='" + author + '\'' +
                '}';
    }
}
