package com.unimib.koby.loginUnitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.koby.data.repository.user.IUserRepository;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;
import com.unimib.koby.ui.login.UserViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule(); // forza LiveData a lavorare sincrono

    @Mock
    private IUserRepository mockRepository;

    private UserViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new UserViewModel(mockRepository);
    }

    @Test
    public void login_success_returnsUser() {
        // Arrange
        MutableLiveData<Result> expectedLiveData = new MutableLiveData<>();
        User fakeUser = new User("Mario", "mario@email.com", "token123");
        expectedLiveData.setValue(new Result.Success<>(fakeUser));

        when(mockRepository.login("mario@email.com", "password123")).thenReturn(expectedLiveData);

        // Act
        LiveData<Result> resultLiveData = viewModel.login("mario@email.com", "password123");

        // Assert
        assertNotNull(resultLiveData.getValue());
        assertTrue(resultLiveData.getValue() instanceof Result.Success);

        User resultUser = (User) ((Result.Success<?>) resultLiveData.getValue()).getData();
        assertEquals("Mario", resultUser.getName());
        assertEquals("mario@email.com", resultUser.getEmail());
    }

    @Test
    public void login_error_returnsError() {
        // Arrange
        MutableLiveData<Result> errorLiveData = new MutableLiveData<>();
        errorLiveData.setValue(new Result.Error("Credenziali errate"));

        when(mockRepository.login("x@y.it", "sbagliata")).thenReturn(errorLiveData);

        // Act
        LiveData<Result> resultLiveData = viewModel.login("x@y.it", "sbagliata");

        // Assert
        assertNotNull(resultLiveData.getValue());
        assertTrue(resultLiveData.getValue() instanceof Result.Error);
        assertEquals("Credenziali errate", ((Result.Error) resultLiveData.getValue()).getMessage());
    }

    @Test
    public void login_setsLoadingFalseAfterCompletion() {
        // Arrange
        MutableLiveData<Result> expectedLiveData = new MutableLiveData<>();
        expectedLiveData.setValue(new Result.Success<>(new User("Test", "email@email.com", "token")));
        when(mockRepository.login(anyString(), anyString())).thenReturn(expectedLiveData);

        // Act
        viewModel.login("email@email.com", "password");

        // Assert
        assertFalse(Boolean.TRUE.equals(viewModel.getLoading().getValue()));
    }
}
