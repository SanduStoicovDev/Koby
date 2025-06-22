package com.unimib.koby.data.source.user;

import android.net.Uri;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimib.koby.model.User;

/**
 * RemoteDataSource che gestisce Firestore (dati base utente) + Firebase Storage (foto profilo).
 */
public class FirebaseUserDataRemoteDataSource implements BaseUserDataRemoteDataSource {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

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

    @Override
    public Task<Uri> uploadProfileImage(String uid, Uri localUri) {
        if (uid == null || localUri == null) {
            return Tasks.forException(new IllegalArgumentException("uid or uri null"));
        }
        StorageReference ref = storage.getReference()
                .child("profilePictures")
                .child(uid + ".jpg");

        return ref.putFile(localUri)
                .continueWithTask(t -> t.isSuccessful()
                        ? ref.getDownloadUrl()
                        : Tasks.forException(t.getException()));
    }

    @Override
    public Task<Uri> fetchProfileImageUrl(String uid) {
        if (uid == null) return Tasks.forResult(null);
        StorageReference ref = storage.getReference()
                .child("profilePictures")
                .child(uid + ".jpg");
        return ref.getDownloadUrl();
    }
}