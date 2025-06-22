package com.unimib.koby.ui.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.unimib.koby.data.repository.user.UserRepository;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository repo;

    public ProfileViewModelFactory(UserRepository repo) { this.repo = repo; }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(repo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}