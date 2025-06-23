package com.unimib.koby.profileUnitTest;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

import androidx.lifecycle.ViewModel;
import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.ui.profile.ProfileViewModel;
import com.unimib.koby.ui.profile.ProfileViewModelFactory;

import org.junit.Before;
import org.junit.Test;

/**
 * Verifica che:
 * 1. la factory restituisca un'istanza di ProfileViewModel
 * 2. lanci IllegalArgumentException per classi sconosciute.
 *
 * Dipendenze di test in build.gradle(.kts):
 *   testImplementation("junit:junit:4.13.2")
 *   testImplementation("com.google.truth:truth:1.4.4")
 *   testImplementation("org.mockito:mockito-core:5.11.0")
 */
public class ProfileViewModelFactoryTest {

    private UserRepository repo;
    private ProfileViewModelFactory factory;

    @Before
    public void setUp() {
        repo = mock(UserRepository.class);
        factory = new ProfileViewModelFactory(repo);
    }

    @Test
    public void create_WithProfileViewModelClass_returnsInstance() {
        ProfileViewModel vm = factory.create(ProfileViewModel.class);

        assertThat(vm).isNotNull();
        assertThat(vm).isInstanceOf(ProfileViewModel.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_WithUnknownClass_throwsException() {
        factory.create(UnknownVm.class);   // deve lanciare
    }

    private static class UnknownVm extends ViewModel {}
}
