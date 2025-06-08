package com.unimib.koby.model;

import com.google.firebase.Timestamp;

import java.util.Objects;

/**
 * Metadati di un "space study" (documento PDF caricato dall'utente).
 */
public class SpaceStudy {
    private String id;          // generato da Firestore (document ID)
    private String title;
    private String pdfUrl;      // URL firmato (Cloud Storage)
    private String ownerUid;    // uid Firebase dell'utente
    private Timestamp createdAt;

    public SpaceStudy() { /* Needed by Firestore */ }

    public SpaceStudy(String id, String title, String pdfUrl, String ownerUid, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.pdfUrl = pdfUrl;
        this.ownerUid = ownerUid;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPdfUrl() { return pdfUrl; }
    public String getOwnerUid() { return ownerUid; }
    public Timestamp getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SpaceStudy that = (SpaceStudy) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(pdfUrl, that.pdfUrl) && Objects.equals(ownerUid, that.ownerUid) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, pdfUrl, ownerUid, createdAt);
    }
}
