package com.unimib.koby.profileUnitTest;

import static com.google.common.truth.Truth.assertThat;
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
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Unit-test JVM per ProfileViewModel.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, manifest = Config.NONE)
public class ProfileViewModelTest {

    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private UserRepository repo;
    private ProfileViewModel vm;

    @Before
    public void setUp() {
        repo = mock(UserRepository.class);
    }

    /* ---------- TEST 1: caricamento iniziale ---------- */
    @Test
    public void loadProfilePicture_emitsInitialUri() throws Exception {
        Uri remote = Uri.parse("https://example.com/photo.jpg");
        MutableLiveData<Uri> live = new MutableLiveData<>(remote);

        when(repo.loadProfilePicture()).thenReturn(live);

        vm = new ProfileViewModel(repo);

        assertThat(getOrAwait(vm.getPhotoUrl())).isEqualTo(remote);
    }

    /* ---------- TEST 2: upload con successo ---------- */
    @Test
    public void uploadPhoto_success_updatesPhotoUrl() throws Exception {
        when(repo.loadProfilePicture()).thenReturn(new MutableLiveData<>());

        Uri local    = Uri.parse("file://tmp.jpg");
        Uri uploaded = Uri.parse("https://cdn.koby.it/img.jpg");

        MutableLiveData<Result> uploadLive = new MutableLiveData<>();
        when(repo.uploadProfilePicture(local)).thenReturn(uploadLive);

        vm = new ProfileViewModel(repo);
        vm.uploadPhoto(local);

        // emula la risposta del repository DOPO la chiamata
        uploadLive.postValue(new Result.Success<>(uploaded));

        assertThat(getOrAwait(vm.getPhotoUrl())).isEqualTo(uploaded);
    }

    /* ---------- TEST 3: upload con errore ---------- */
    @Test
    public void uploadPhoto_error_keepsPhotoUrlUnchanged() throws Exception {
        // 1. lo stato iniziale (es. foto già salvata sul server)
        Uri remoteUri = Uri.parse("https://example.com/old.jpg");
        MutableLiveData<Uri> initialLive = new MutableLiveData<>(remoteUri);
        when(repo.loadProfilePicture()).thenReturn(initialLive);

        // 2. stub dell'upload che produrrà un errore
        Uri local = Uri.parse("file://tmp.jpg");
        MutableLiveData<Result> uploadLive = new MutableLiveData<>();
        when(repo.uploadProfilePicture(local)).thenReturn(uploadLive);

        // 3. crea il ViewModel
        vm = new ProfileViewModel(repo);

        // 3a. valore “prima” dell’upload
        Uri before = getOrAwait(vm.getPhotoUrl());
        assertThat(before).isEqualTo(remoteUri);

        // 4. avvia upload e poi pubblica l'errore
        vm.uploadPhoto(local);
        uploadLive.postValue(new Result.Error("KO"));

        // 5. verifica che il LiveData NON sia cambiato
        Uri after = getOrAwait(vm.getPhotoUrl());
        assertThat(after).isEqualTo(before);      // invariato
    }

    /* ---------- helper LiveData sincrono ---------- */
    private static <T> T getOrAwait(final androidx.lifecycle.LiveData<T> live)
            throws InterruptedException, TimeoutException {

        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        live.observeForever(v -> {
            data[0] = v;
            latch.countDown();
        });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new TimeoutException("Valore LiveData non ricevuto.");
        }
        //noinspection unchecked
        return (T) data[0];
    }
}
