package com.unimib.koby.data.repository.user;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageException;
import com.unimib.koby.R;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;

/**
 * Repository di dominio che media tra UI e datasource Firebase.
 */
public class UserRepository implements IUserRepository {

    private final BaseUserAuthenticationRemoteDataSource authRemote;
    private final BaseUserDataRemoteDataSource userRemote;
    private User cachedUser;

    public UserRepository(BaseUserAuthenticationRemoteDataSource authRemote,
                          BaseUserDataRemoteDataSource userRemote) {
        this.authRemote = authRemote;
        this.userRemote = userRemote;

        FirebaseUser fUser = authRemote.getCurrentUser();
        if (fUser != null) {
            cachedUser = new User(
                    fUser.getDisplayName(),
                    fUser.getEmail(),
                    fUser.getPhotoUrl() != null ? fUser.getPhotoUrl().toString() : null
            );
        }
    }

    /* --------------------------  Auth  -------------------------- */

    @Override
    public MutableLiveData<Result> login(String email, String password) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        authRemote.loginWithEmail(email, password)
                .addOnCompleteListener(t -> handleAuthTask(t, live));
        return live;
    }

    @Override
    public MutableLiveData<Result> loginWithGoogle(String idToken) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authRemote.loginWithCredential(credential)
                .addOnCompleteListener(t -> handleAuthTask(t, live));
        return live;
    }

    @Override
    public MutableLiveData<Result> register(String name, String email, String password) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        authRemote.registerWithEmail(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fUser = authRemote.getCurrentUser();
                        if (fUser != null) {
                            fUser.updateProfile(new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build());
                            cachedUser = new User(name, email, null);
                            userRemote.saveUser(cachedUser);
                        }
                    }
                    handleAuthTask(task, live);
                });
        return live;
    }

    @Override
    public User getLoggedUser() { return cachedUser; }

    @Override
    public MutableLiveData<Result> logout() {
        MutableLiveData<Result> live = new MutableLiveData<>();
        authRemote.logout();
        cachedUser = null;
        live.setValue(new Result.Success<>(null));
        return live;
    }

    private void handleAuthTask(@NonNull Task<AuthResult> task, MutableLiveData<Result> live) {
        if (task.isSuccessful()) {
            FirebaseUser fUser = authRemote.getCurrentUser();
            cachedUser = new User(
                    fUser.getDisplayName(),
                    fUser.getEmail(),
                    fUser.getPhotoUrl() != null ? fUser.getPhotoUrl().toString() : null
            );
            live.setValue(new Result.Success<>(cachedUser));
        } else {
            live.setValue(new Result.Error(
                    task.getException() != null ?
                            task.getException().getMessage() :
                            String.valueOf(R.string.auth_error)));
        }
    }

    /* ---------------------- Foto profilo ------------------------ */

    /**
     * Carica una nuova foto profilo su Firebase Storage e aggiorna FirebaseAuth + cache locale.
     */
    public MutableLiveData<Result> uploadProfilePicture(Uri localUri) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        FirebaseUser fUser = authRemote.getCurrentUser();
        if (fUser == null) {
            live.setValue(new Result.Error("Utente non autenticato"));
            return live;
        }

        userRemote.uploadProfileImage(fUser.getUid(), localUri)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri download = t.getResult();
                        fUser.updateProfile(new UserProfileChangeRequest.Builder()
                                .setPhotoUri(download)
                                .build());
                        if (cachedUser != null) cachedUser.setPhotoUrl(download.toString());
                        live.setValue(new Result.Success<>(download));
                    } else {
                        live.setValue(new Result.Error(
                                t.getException() != null ?
                                        t.getException().getMessage() :
                                        "Errore durante l'upload della foto"));
                    }
                });

        return live;
    }

    /**
     * Ritorna un LiveData con la DownloadUrl della foto profilo o null se l'utente non ne ha una.
     * Gestisce esplicitamente il caso in cui l'oggetto non esista su Firebase Storage per evitare il crash.
     */
    public MutableLiveData<Uri> loadProfilePicture() {
        MutableLiveData<Uri> live = new MutableLiveData<>();
        FirebaseUser fUser = authRemote.getCurrentUser();
        if (fUser == null) {
            live.setValue(null);
            return live;
        }

        // 1) Usa la photoUrl già presente in FirebaseAuth, se disponibile
        if (fUser.getPhotoUrl() != null) {
            live.setValue(fUser.getPhotoUrl());
            return live;
        }

        // 2) Altrimenti prova a recuperarla dallo Storage, gestendo il caso in cui il file non esista
        userRemote.fetchProfileImageUrl(fUser.getUid())
                .addOnSuccessListener(live::setValue) // immagine trovata
                .addOnFailureListener(e -> {
                    if (e instanceof StorageException) {
                        int code = ((StorageException) e).getErrorCode();
                        if (code == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            // L'utente non ha ancora caricato una foto: nessun problema, torniamo null
                            live.setValue(null);
                            return;
                        }
                    }
                    // Per altri errori: loggare e restituire comunque null per non bloccare l'app
                    live.setValue(null);
                });

        return live;
    }
}