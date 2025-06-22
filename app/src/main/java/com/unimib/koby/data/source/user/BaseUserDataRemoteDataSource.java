package com.unimib.koby.data.source.user;

import android.net.Uri;
import com.google.android.gms.tasks.Task;
import com.unimib.koby.model.User;

/**
 * CRUD sui dati profilo utente oltre l’autenticazione, compresa la foto profilo.
 */
public interface BaseUserDataRemoteDataSource {
    Task<Void> saveUser(User user);
    Task<User> fetchUser(String uid);

    /** Carica la foto profilo su Firebase Storage e restituisce la downloadUrl */
    Task<Uri> uploadProfileImage(String uid, Uri localUri);

    /** Recupera la downloadUrl della foto profilo, se presente. */
    Task<Uri> fetchProfileImageUrl(String uid);
}