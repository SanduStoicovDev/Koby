package com.unimib.koby.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class ChatMessage {
    @DocumentId
    private String id;
    private String role;      // "user" | "assistant"
    private String content;
    private Timestamp createdAt;

    public ChatMessage() {}

    public ChatMessage(String role, String content) {
        this.role=role;
        this.content=content;
        this.createdAt = Timestamp.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}