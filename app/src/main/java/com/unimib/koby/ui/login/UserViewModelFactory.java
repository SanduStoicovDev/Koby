package com.unimib.koby.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.koby.data.repository.user.IUserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final IUserRepository repository;
    public UserViewModelFactory(IUserRepository repository) { this.repository = repository; }
    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new UserViewModel(repository);
    }
}