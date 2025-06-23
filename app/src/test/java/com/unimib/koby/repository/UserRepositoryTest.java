package com.unimib.koby.repository;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageException;
import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.data.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.koby.data.source.user.BaseUserDataRemoteDataSource;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;

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
 * Test JVM (nessun device necessario) per UserRepository.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, manifest = Config.NONE)
public class UserRepositoryTest {

    /* Esegue LiveData immediatamente */
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private BaseUserAuthenticationRemoteDataSource authRemote;
    private BaseUserDataRemoteDataSource        dataRemote;
    private UserRepository                       repo;

    /* Stubs condivisi ----------------------------------------------------- */
    private FirebaseUser fUser;

    @Before
    public void setUp() {
        authRemote = mock(BaseUserAuthenticationRemoteDataSource.class);
        dataRemote = mock(BaseUserDataRemoteDataSource.class);

        // FirebaseUser stub di base (displayName / email / photoUrl null)
        fUser = mock(FirebaseUser.class);
        when(fUser.getDisplayName()).thenReturn("Mario");
        when(fUser.getEmail())      .thenReturn("mario@example.com");
        when(fUser.getUid())        .thenReturn("UID123");

        /* Costruttore del repository usa getCurrentUser() */
        when(authRemote.getCurrentUser()).thenReturn(fUser);
        repo = new UserRepository(authRemote, dataRemote);
    }

    /* ------------------------------------------------------------------ */
    /* 1. logout() azzera cache e restituisce Result.Success ------------- */
    /* ------------------------------------------------------------------ */
    @Test
    public void logout_clearsCacheAndEmitsSuccess() throws Exception {
        // (pre-condizione) cachedUser non nullo
        assertThat(repo.getLoggedUser()).isNotNull();

        Result res = getOrAwait(repo.logout());
        assertThat(res).isInstanceOf(Result.Success.class);
        assertThat(repo.getLoggedUser()).isNull();

        verify(authRemote).logout();
    }

    /* ------------------------------------------------------------------ */
    /* 2. uploadProfilePicture senza utente → Result.Error --------------- */
    /* ------------------------------------------------------------------ */
    @Test
    public void uploadProfilePicture_noUser_returnsError() throws Exception {
        when(authRemote.getCurrentUser()).thenReturn(null);     // nessun login
        Uri local = Uri.parse("file://tmp.jpg");

        Result res = getOrAwait(repo.uploadProfilePicture(local));

        assertThat(res).isInstanceOf(Result.Error.class);
        verify(dataRemote, never()).uploadProfileImage(any(), any());
    }

    /* ------------------------------------------------------------------ */
    /* 3. loadProfilePicture con photoUrl già presente ------------------- */
    /* ------------------------------------------------------------------ */
    @Test
    public void loadProfilePicture_existingUrl_returnsThatUrl() throws Exception {
        Uri remote = Uri.parse("https://cdn.site/pic.jpg");
        when(fUser.getPhotoUrl()).thenReturn(remote);

        Uri emitted = getOrAwait(repo.loadProfilePicture());

        assertThat(emitted).isEqualTo(remote);
        verify(dataRemote, never()).fetchProfileImageUrl(any());
    }

    @Test
    public void loadProfilePicture_objectNotFound_returnsNull() throws Exception {
        // — 0. l'utente non ha photoUrl già salvata
        when(fUser.getPhotoUrl()).thenReturn(null);

        // — 1. creeremo un Task che fallirà DOPO il return
        TaskCompletionSource<Uri> tcs = new TaskCompletionSource<>();

        // — 2. eccezione finta con codice 404
        StorageException ex404 = mock(StorageException.class);
        when(ex404.getErrorCode()).thenReturn(StorageException.ERROR_OBJECT_NOT_FOUND);

        // — 3. stub del remote
        when(dataRemote.fetchProfileImageUrl(any())).thenReturn(tcs.getTask());

        // — 4. avvio del metodo da testare
        MutableLiveData<Uri> live = repo.loadProfilePicture();

        // — 5. simuliamo il fail *dopo* che il repo ha registrato i listener
        new android.os.Handler(android.os.Looper.getMainLooper())
                .post(() -> tcs.setException(ex404));

        // — 6. facciamo girare la message-queue (Robolectric)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();

        // — 7. asserzione
        Uri emitted = getOrAwait(live);
        assertThat(emitted).isNull();
    }


    /* ================================================================== */
    /* Helper: attende LiveData sincrona                                   */
    /* ================================================================== */
    private static <T> T getOrAwait(MutableLiveData<T> live)
            throws InterruptedException, TimeoutException {

        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        live.observeForever(v -> {
            data[0] = v;
            latch.countDown();
        });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new TimeoutException("LiveData non emessa in tempo");
        }
        //noinspection unchecked
        return (T) data[0];
    }
}
