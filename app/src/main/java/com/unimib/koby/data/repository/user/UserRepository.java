package com.unimib.koby.data.repository.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.Task;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;

/** Default Firebase‑backed implementation of {@link IUserRepository}. */
public class UserRepository implements IUserRepository {

    private final FirebaseAuth firebaseAuth;
    private User cachedUser;

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        if (fUser != null) {
            cachedUser = new User(fUser.getDisplayName(), fUser.getEmail(), null);
        }
    }

    public UserRepository(BaseUserAuthenticationRemoteDataSource authRemoteDS, BaseUserDataRemoteDataSource userRemoteDS, FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public MutableLiveData<Result> login(String email, String password) {
        MutableLiveData<Result> liveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> handleAuthTask(task, liveData));
        return liveData;
    }

    @Override
    public MutableLiveData<Result> loginWithGoogle(String idToken) {
        MutableLiveData<Result> liveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(task -> handleAuthTask(task, liveData));
        return liveData;
    }

    @Override
    public MutableLiveData<Result> register(String name, String email, String password) {
        MutableLiveData<Result> liveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        firebaseAuth.getCurrentUser().updateProfile(
                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build());
                    handleAuthTask(task, liveData);
                });
        return liveData;
    }

    @Override
    public User getLoggedUser() {
        return cachedUser;
    }

    @Override
    public MutableLiveData<Result> logout() {
        MutableLiveData<Result> liveData = new MutableLiveData<>();
        firebaseAuth.signOut();
        cachedUser = null;
        liveData.setValue(new Result.UserResponseSuccess(null));
        return liveData;
    }

    @Override
    public void signUp(String email, String password) {

    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signInWithGoogle(String token) {

    }

    private void handleAuthTask(@NonNull Task<AuthResult> task, MutableLiveData<Result> liveData) {
        if (task.isSuccessful()) {
            FirebaseUser fUser = firebaseAuth.getCurrentUser();
            cachedUser = new User(fUser.getDisplayName(), fUser.getEmail(), null);
            liveData.setValue(new Result.UserResponseSuccess(cachedUser));
        } else {
            liveData.setValue(new Result.Error(task.getException() != null ?
                    task.getException().getMessage() : "Authentication error"));
        }
    }
}
