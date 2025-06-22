package com.unimib.koby.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String name;
    private String email;
    private String photoUrl;   // download URL (può essere null)

    @Exclude
    private String idToken;    // token Google (non salvato su Firestore)

    /** Necessario a Firestore */
    public User() {}

    public User(String name, String email, String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    /** Variante con idToken usa GoogleSignIn */
    public User(String name, String email, String photoUrl, String idToken) {
        this(name, email, photoUrl);
        this.idToken = idToken;
    }

    // -------- getters / setters --------
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @Exclude public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}