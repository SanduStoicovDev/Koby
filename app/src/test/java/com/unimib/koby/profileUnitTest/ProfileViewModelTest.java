package com.unimib.koby.profileUnitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.model.Result;
import com.unimib.koby.ui.profile.ProfileViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProfileViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule(); // esegue LiveData in modo sincrono

    private UserRepository mockRepo;
    private ProfileViewModel viewModel;

    @Before
    public void setUp() {
        mockRepo = mock(UserRepository.class);
    }

    @Test
    public void loadProfilePicture_setsInitialPhotoUrl() {
        // Mock uri e LiveData di ritorno
        Uri fakeUri = Uri.parse("https://example.com/photo.jpg");
        MutableLiveData<Uri> live = new MutableLiveData<>();
        live.setValue(fakeUri);

        when(mockRepo.loadProfilePicture()).thenReturn(live);

        viewModel = new ProfileViewModel(mockRepo);
        assertEquals(fakeUri, viewModel.getPhotoUrl().getValue());
    }

    @Test
    public void uploadPhoto_updatesPhotoUrlOnSuccess() {
        Uri inputUri = Uri.parse("file://temp.jpg");
        Uri uploadedUri = Uri.parse("https://cdn.koby.it/image.jpg");

        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(new Result.Success<>(uploadedUri));

        when(mockRepo.loadProfilePicture()).thenReturn(new MutableLiveData<>()); // evita null
        when(mockRepo.uploadProfilePicture(inputUri)).thenReturn(resultLiveData);

        viewModel = new ProfileViewModel(mockRepo);
        viewModel.uploadPhoto(inputUri);

        assertEquals(uploadedUri, viewModel.getPhotoUrl().getValue());
    }

    @Test
    public void uploadPhoto_doesNotUpdatePhotoUrlOnFailure() {
        Uri inputUri = Uri.parse("file://temp.jpg");
        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(new Result.Error("Errore"));

        when(mockRepo.loadProfilePicture()).thenReturn(new MutableLiveData<>());
        when(mockRepo.uploadProfilePicture(inputUri)).thenReturn(resultLiveData);

        viewModel = new ProfileViewModel(mockRepo);
        viewModel.uploadPhoto(inputUri);

        assertNull(viewModel.getPhotoUrl().getValue()); // Non aggiorna in caso di errore
    }
}
