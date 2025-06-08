package com.unimib.koby.ui.spacestudy;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.koby.data.repository.space.ISpaceStudyRepository;

public class SpaceStudyViewModelFactory implements ViewModelProvider.Factory {
    private final ISpaceStudyRepository repo;
    public SpaceStudyViewModelFactory(ISpaceStudyRepository repo) { this.repo = repo; }

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new SpaceStudyViewModel(repo);
    }
}