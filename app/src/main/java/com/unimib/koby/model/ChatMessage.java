package com.unimib.koby.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Objects;

/**
 * Rappresenta un singolo messaggio in chat (user o assistant).
 * Compatibile con Firestore (costruttore vuoto + @ServerTimestamp).
 */
public class ChatMessage {

    /* ------------  campi  ------------ */

    @DocumentId
    private String id;                 // id documento Firestore

    private String role;               // "user" | "assistant"
    private String content;            // testo del messaggio

    /** Timestamp gestito dal server; Firestore lo sovrascriverà alla write. */
    @ServerTimestamp
    private Timestamp createdAt;

    /* ------------  costruttori  ------------ */

    /** Necessario per Firestore. */
    public ChatMessage() {}

    /** Costruttore di comodo lato client. */
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.createdAt = Timestamp.now();     // valore provvisorio
    }

    public ChatMessage(String role, String content, String uid, Timestamp now) {
        this.role = role;
        this.content = content;
        this.createdAt = now;
    }

    /* ------------  getter / setter  ------------ */

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    /** Ritorna il timestamp Firestore grezzo. */
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** Data Java convenzionale, se serve. */
    public java.util.Date getCreatedAtDate() {
        return createdAt != null ? createdAt.toDate() : null;
    }

    /**
     * Epoch-millis usato da DiffUtil per capire se due item sono lo stesso.
     * NB: via `toDate().getTime()` perché `Timestamp` non ha `getTime()`.
     */
    public long getTimestamp() {
        return createdAt != null ? createdAt.toDate().getTime() : 0L;
    }

    /* ------------  equals / hashCode  ------------ */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(role, that.role) &&
                Objects.equals(content, that.content) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, content, createdAt);
    }
}
