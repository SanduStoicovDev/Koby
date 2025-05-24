package com.unimib.koby.data.repository.user;

import androidx.lifecycle.MutableLiveData;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;

/** Contract respected by every authentication repository.  */
public interface IUserRepository {
    MutableLiveData<Result> login(String email, String password);
    MutableLiveData<Result> loginWithGoogle(String idToken);
    MutableLiveData<Result> register(String name, String email, String password);
    User getLoggedUser();
    MutableLiveData<Result> logout();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
}
