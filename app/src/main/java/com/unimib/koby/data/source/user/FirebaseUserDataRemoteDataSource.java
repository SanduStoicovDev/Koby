package com.unimib.koby.data.source.remote.user;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.model.User;

/**
 * Implementazione minimale Firestore per i dati utente.
 */
public class FirebaseUserDataRemoteDataSource implements BaseUserDataRemoteDataSource {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public Task<Void> saveUser(User user) {
        if (user == null || user.getEmail() == null) {
            return Tasks.forException(new IllegalArgumentException("User or email can't be null"));
        }
        return firestore.collection("users")
                .document(user.getEmail())
                .set(user);
    }

    @Override
    public Task<User> fetchUser(String uid) {
        return firestore.collection("users")
                .document(uid)
                .get()
                .continueWith(task -> {
                    DocumentSnapshot snap = task.getResult();
                    return (snap != null && snap.exists()) ? snap.toObject(User.class) : null;
                });
    }
}
