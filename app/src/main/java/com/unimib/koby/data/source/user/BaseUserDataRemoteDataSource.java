package com.unimib.koby.data.source.user;

import com.google.android.gms.tasks.Task;
import com.unimib.koby.model.User;

/**
 * CRUD sui dati profilo utente oltre l’autenticazione.
 */
public interface BaseUserDataRemoteDataSource {
    Task<Void> saveUser(User user);
    Task<User> fetchUser(String uid);
}
