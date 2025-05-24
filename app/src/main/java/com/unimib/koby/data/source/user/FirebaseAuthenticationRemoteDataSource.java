package com.unimib.koby.data.source.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;

/**
 * Implementazione Firebase del datasource di autenticazione.
 */
public class FirebaseAuthenticationRemoteDataSource implements BaseUserAuthenticationRemoteDataSource {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public Task<AuthResult> loginWithEmail(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    @Override
    public Task<AuthResult> loginWithCredential(AuthCredential credential) {
        return firebaseAuth.signInWithCredential(credential);
    }

    @Override
    public Task<AuthResult> registerWithEmail(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    @Override
    public void logout() { firebaseAuth.signOut(); }

    @Override
    public FirebaseUser getCurrentUser() { return firebaseAuth.getCurrentUser(); }
}