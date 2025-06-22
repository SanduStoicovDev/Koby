package com.unimib.koby.ui.profile;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.model.Result;

public class ProfileViewModel extends ViewModel {

    private final UserRepository repo;
    private final MutableLiveData<Uri> photoUrl = new MutableLiveData<>();

    public ProfileViewModel(UserRepository repo) {
        this.repo = repo;
        // carica all'avvio la foto corrente (se presente)
        repo.loadProfilePicture().observeForever(photoUrl::setValue);
    }

    public LiveData<Uri> getPhotoUrl() { return photoUrl; }

    public void uploadPhoto(Uri localUri) {
        repo.uploadProfilePicture(localUri).observeForever(r -> {
            if (r instanceof Result.Success) {
                photoUrl.setValue((Uri) ((Result.Success<?>) r).getData());
            }
        });
    }
}