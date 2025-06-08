package com.unimib.koby.data.repository.space;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.SpaceStudy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestisce l'upload del PDF e il salvataggio dei metadati.
 */
public class SpaceStudyRepository implements ISpaceStudyRepository {

    private static final String COLLECTION = "spaceStudies";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    public LiveData<Result> uploadSpaceStudy(Uri pdfUri, String title) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        if (pdfUri == null) {
            live.setValue(new Result.Error("PDF URI is null"));
            return live;
        }
        String fileName = "pdfs/" + UUID.randomUUID() + ".pdf";
        StorageReference pdfRef = storageRef.child(fileName);

        pdfRef.putFile(pdfUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return pdfRef.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                            FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";
                    SpaceStudy space = new SpaceStudy(null, title, uri.toString(), uid,
                            com.google.firebase.Timestamp.now());
                    firestore.collection(COLLECTION).add(space)
                            .addOnSuccessListener(docRef -> {
                                live.setValue(new Result.Success<>(space));
                            })
                            .addOnFailureListener(e -> live.setValue(new Result.Error(e.getMessage())));
                })
                .addOnFailureListener(e -> live.setValue(new Result.Error(e.getMessage())));
        return live;
    }

    @Override
    public LiveData<List<SpaceStudy>> fetchMySpaces() {
        MutableLiveData<List<SpaceStudy>> live = new MutableLiveData<>();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) { live.setValue(new ArrayList<>()); return live; }

        firestore.collection(COLLECTION)
                .whereEqualTo("ownerUid", uid)
                .addSnapshotListener((snap, err) -> {
                    if (err != null) return;
                    List<SpaceStudy> list = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        SpaceStudy s = d.toObject(SpaceStudy.class);
                        if (s != null)
                            s = new SpaceStudy(d.getId(), s.getTitle(),
                                    s.getPdfUrl(), s.getOwnerUid(), s.getCreatedAt());
                        list.add(s);
                    }
                    live.setValue(list);
                });
        return live;
    }
}