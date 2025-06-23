package com.unimib.koby.firebaseUnitTest;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.koby.data.source.user.FirebaseAuthenticationRemoteDataSource;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseAuthenticationRemoteDataSourceTest {

    @Test
    public void loginWithEmail_delegatesToFirebaseAuth() {
        Task<AuthResult> mockTask = mock(Task.class);
        FirebaseAuth fakeAuth = mock(FirebaseAuth.class);
        when(fakeAuth.signInWithEmailAndPassword("mail","pwd")).thenReturn(mockTask);

        try (MockedStatic<FirebaseAuth> stat = mockStatic(FirebaseAuth.class)) {
            stat.when(FirebaseAuth::getInstance).thenReturn(fakeAuth);

            FirebaseAuthenticationRemoteDataSource ds = new FirebaseAuthenticationRemoteDataSource();
            Task<AuthResult> result = ds.loginWithEmail("mail","pwd");

            verify(fakeAuth).signInWithEmailAndPassword("mail","pwd");
            assertThat(result).isSameInstanceAs(mockTask);
        }
    }

    @Test
    public void logout_invokesSignOut() {
        FirebaseAuth fakeAuth = mock(FirebaseAuth.class);
        try (MockedStatic<FirebaseAuth> stat = mockStatic(FirebaseAuth.class)) {
            stat.when(FirebaseAuth::getInstance).thenReturn(fakeAuth);

            new FirebaseAuthenticationRemoteDataSource().logout();
            verify(fakeAuth).signOut();
        }
    }

    @Test
    public void getCurrentUser_returnsFirebaseUser() {
        FirebaseUser fUser = mock(FirebaseUser.class);
        FirebaseAuth fakeAuth = mock(FirebaseAuth.class);
        when(fakeAuth.getCurrentUser()).thenReturn(fUser);

        try (MockedStatic<FirebaseAuth> stat = mockStatic(FirebaseAuth.class)) {
            stat.when(FirebaseAuth::getInstance).thenReturn(fakeAuth);

            FirebaseUser res = new FirebaseAuthenticationRemoteDataSource().getCurrentUser();
            assertThat(res).isSameInstanceAs(fUser);
        }
    }
}
