package com.unimib.koby.ui.settings;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SettingsViewModelFactory implements ViewModelProvider.Factory {
    private final Context ctx;
    public SettingsViewModelFactory(Context ctx) { this.ctx = ctx; }
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SettingsViewModel(SettingsManager.getInstance(ctx));
    }
}

