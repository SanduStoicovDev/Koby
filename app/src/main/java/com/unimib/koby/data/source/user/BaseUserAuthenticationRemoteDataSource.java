package com.unimib.koby.data.source.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Contratto per le chiamate di autenticazione remote.
 */
public interface BaseUserAuthenticationRemoteDataSource {
    Task<AuthResult> loginWithEmail(String email, String password);
    Task<AuthResult> loginWithCredential(AuthCredential credential);
    Task<AuthResult> registerWithEmail(String email, String password);
    void logout();
    FirebaseUser getCurrentUser();
}