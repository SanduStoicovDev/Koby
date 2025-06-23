package com.unimib.koby.loginUnitTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import androidx.lifecycle.ViewModel;

import com.unimib.koby.data.repository.user.IUserRepository;
import com.unimib.koby.ui.login.UserViewModel;
import com.unimib.koby.ui.login.UserViewModelFactory;

import org.junit.Before;
import org.junit.Test;

public class UserViewModelFactoryTest {

    private IUserRepository mockRepository;
    private UserViewModelFactory factory;

    @Before
    public void setUp() {
        mockRepository = mock(IUserRepository.class);
        factory = new UserViewModelFactory(mockRepository);
    }

    @Test
    public void create_returnsUserViewModelInstance() {
        ViewModel vm = factory.create(UserViewModel.class);

        assertNotNull("ViewModel non dovrebbe essere null", vm);
        assertTrue("L'istanza dovrebbe essere di tipo UserViewModel", vm instanceof UserViewModel);
    }
}
