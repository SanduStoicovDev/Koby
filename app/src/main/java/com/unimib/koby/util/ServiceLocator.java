package com.unimib.koby.util;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.unimib.koby.data.repository.user.IUserRepository;
import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.FirebaseAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.data.source.remote.user.FirebaseUserDataRemoteDataSource;

/**
 * Service Locator che costruisce e fornisce i singleton di cui ha bisogno
 * l’applicazione.  Ora compatibile con il costruttore a **tre argomenti** di
 * {@link UserRepository} (AuthDS, UserDS, FirebaseAuth).
 */
public class ServiceLocator {

    private static ServiceLocator instance;

    private IUserRepository userRepository;
    private BaseUserAuthenticationRemoteDataSource authRemoteDS;
    private BaseUserDataRemoteDataSource         userRemoteDS;

    private ServiceLocator() {}

    public static synchronized ServiceLocator getInstance() {
        if (instance == null) instance = new ServiceLocator();
        return instance;
    }

    public synchronized IUserRepository getUserRepository(Application application) {
        if (userRepository == null) {
            if (authRemoteDS == null) authRemoteDS = new FirebaseAuthenticationRemoteDataSource();
            if (userRemoteDS == null) userRemoteDS = new FirebaseUserDataRemoteDataSource();
            // Costruttore a 3 argomenti richiesto dalla tua classe UserRepository
            userRepository = new UserRepository(authRemoteDS, userRemoteDS, FirebaseAuth.getInstance());
        }
        return userRepository;
    }
}