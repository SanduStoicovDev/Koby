package com.unimib.koby.util;

import android.app.Application;

import com.unimib.koby.data.repository.user.IUserRepository;
import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.FirebaseAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.data.source.user.FirebaseUserDataRemoteDataSource;

public class ServiceLocator {

    private static ServiceLocator instance;

    private IUserRepository userRepository;
    private BaseUserAuthenticationRemoteDataSource authRemoteDS;
    private BaseUserDataRemoteDataSource         userRemoteDS;

    private ServiceLocator() { }

    public static synchronized ServiceLocator getInstance() {
        if (instance == null) instance = new ServiceLocator();
        return instance;
    }

    public synchronized IUserRepository getUserRepository(Application application) {
        if (userRepository == null) {
            if (authRemoteDS == null)  authRemoteDS  = new FirebaseAuthenticationRemoteDataSource();
            if (userRemoteDS == null)  userRemoteDS  = new FirebaseUserDataRemoteDataSource();
            userRepository = new UserRepository(authRemoteDS, userRemoteDS);
        }
        return userRepository;
    }
}