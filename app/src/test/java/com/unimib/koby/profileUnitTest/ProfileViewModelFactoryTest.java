package com.unimib.koby.profileUnitTest;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.ui.profile.ProfileViewModel;
import com.unimib.koby.ui.profile.ProfileViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Test unitario (JVM) con Robolectric: non serve un device/emulatore. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, manifest = Config.NONE)     // manifest NONE per più velocità
public class ProfileViewModelFactoryTest {

    private UserRepository repo;
    private ProfileViewModelFactory factory;

    @Before
    public void setUp() {
        repo = mock(UserRepository.class);
        // 👇 deve sempre restituire un LiveData (anche vuoto)
        when(repo.loadProfilePicture()).thenReturn(new MutableLiveData<Uri>());
        factory = new ProfileViewModelFactory(repo);
    }

    @Test
    public void create_withProfileViewModelClass_returnsInstance() {
        ProfileViewModel vm = factory.create(
                ProfileViewModel.class,
                CreationExtras.Empty.INSTANCE
        );

        assertThat(vm).isNotNull();
        assertThat(vm).isInstanceOf(ProfileViewModel.class);
    }

    @Test
    public void create_withUnknownClass_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(UnknownVm.class, CreationExtras.Empty.INSTANCE)
        );
    }

    private static class UnknownVm extends ViewModel {}
}
