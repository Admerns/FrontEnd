package com.example.entity;

import java.util.ArrayList;

public class Event {
    String user_token;
    String title;
    boolean privacy;
    String category;
    String description;
    boolean isVirtual;
    String location;
    ArrayList<Session> sessions;

    int id;
    String event_token;
    String time;


    public Event(String user_token, String title, boolean privacy, String category, String description, boolean isVirtual, String location, ArrayList<Session> sessions) {
        this.user_token = user_token;
        this.title = title;
        this.privacy = privacy;
        this.category = category;
        this.description = description;
        this.isVirtual = isVirtual;
        this.location = location;
        this.sessions = sessions;
    }

    public Event(String user_token, String title, boolean privacy, String category, String description, boolean isVirtual, String location, ArrayList<Session> sessions, int id, String event_token, String time) {
        this.user_token = user_token;
        this.title = title;
        this.privacy = privacy;
        this.category = category;
        this.description = description;
        this.isVirtual = isVirtual;
        this.location = location;
        this.sessions = sessions;
        this.id = id;
        this.event_token = event_token;
        this.time = time;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent_token() {
        return event_token;
    }

    public void setEvent_token(String event_token) {
        this.event_token = event_token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
