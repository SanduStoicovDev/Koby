package com.unimib.koby.data.repository.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;

public class UserRepository implements IUserRepository {

    private final BaseUserAuthenticationRemoteDataSource authRemote;
    private final BaseUserDataRemoteDataSource userRemote;
    private User cachedUser;

    public UserRepository(BaseUserAuthenticationRemoteDataSource authRemote,
                          BaseUserDataRemoteDataSource userRemote) {
        this.authRemote = authRemote;
        this.userRemote = userRemote;
        FirebaseUser fUser = authRemote.getCurrentUser();
        if (fUser != null) cachedUser = new User(fUser.getDisplayName(), fUser.getEmail(), null);
    }

    @Override
    public MutableLiveData<Result> login(String email, String password) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        authRemote.loginWithEmail(email, password)
                .addOnCompleteListener(task -> handleAuthTask(task, live));
        return live;
    }

    @Override
    public MutableLiveData<Result> loginWithGoogle(String idToken) {
        MutableLiveData<Result> live = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authRemote.loginWithCredential(credential)
                .addOnCompleteListener(task -> handleAuthTask(task, live));
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
                            fUser.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build());
                            cachedUser = new User(name, email, null);
                            userRemote.saveUser(cachedUser);
                        }
                    }
                    handleAuthTask(task, live);
                });
        return live;
    }

    @Override public User getLoggedUser() { return cachedUser; }

    @Override
    public MutableLiveData<Result> logout() {
        MutableLiveData<Result> live = new MutableLiveData<>();
        authRemote.logout();
        cachedUser = null;
        live.setValue(new Result.UserResponseSuccess(null));
        return live;
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

    private void handleAuthTask(@NonNull Task<AuthResult> task, MutableLiveData<Result> live) {
        if (task.isSuccessful()) {
            FirebaseUser fUser = authRemote.getCurrentUser();
            cachedUser = new User(fUser.getDisplayName(), fUser.getEmail(), null);
            live.setValue(new Result.UserResponseSuccess(cachedUser));
        } else {
            live.setValue(new Result.Error(task.getException() != null ?
                    task.getException().getMessage() : "Authentication error"));
        }
    }
}