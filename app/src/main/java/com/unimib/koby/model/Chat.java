package com.unimib.koby.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class Chat {
    @DocumentId
    private String id;
    private String title;
    private Timestamp createdAt;
    private String lastMessage;

    public Chat() { /* Firestore */ }

    public Chat(String title, String lastMessage) {
        this.title       = title;
        this.lastMessage = lastMessage;
        this.createdAt   = Timestamp.now();
    }

    public Chat(String title, String nuovaChat, Timestamp createdAt) {
        this.title = title;
        this.lastMessage = nuovaChat;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("title",title);
        map.put("lastMessage",lastMessage);
        map.put("createdAt",createdAt);
        return map;
    }
}