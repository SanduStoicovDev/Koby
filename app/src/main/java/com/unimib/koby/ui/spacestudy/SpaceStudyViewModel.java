package com.unimib.koby.ui.spacestudy;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.unimib.koby.data.repository.space.ISpaceStudyRepository;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.SpaceStudy;

import java.util.List;

public class SpaceStudyViewModel extends ViewModel {

    private final ISpaceStudyRepository repo;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private LiveData<List<SpaceStudy>> mySpaces;          //  ←  campo cache

    public SpaceStudyViewModel(ISpaceStudyRepository repo) { this.repo = repo; }

    public LiveData<Boolean> getLoading() { return loading; }

    public LiveData<Result> upload(Uri pdfUri, String title) {
        loading.setValue(true);
        return Transformations.map(repo.uploadSpaceStudy(pdfUri, title),
                r -> { loading.postValue(false); return r; });
    }

    /** Ritorna (e memoizza) la lista dei miei SpaceStudy. */
    public LiveData<List<SpaceStudy>> getMySpaces() {      //  ←  METODO MANCANTE
        if (mySpaces == null) mySpaces = repo.fetchMySpaces();
        return mySpaces;
    }
}