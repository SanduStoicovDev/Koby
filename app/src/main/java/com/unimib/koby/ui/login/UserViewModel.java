package com.unimib.koby.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.unimib.koby.data.repository.user.IUserRepository;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.User;

public class UserViewModel extends ViewModel {

    private final IUserRepository repository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public UserViewModel(IUserRepository repository) {
        this.repository = repository;
    }

    // PUBLIC OBSERVABLES --------------------------------------------------------------------
    public LiveData<Boolean> getLoading() { return loading; }

    // LOGIN -------------------------------------------------------------------------------
    public LiveData<Result> login(String email, String password) {
        loading.setValue(true);
        LiveData<Result> source = repository.login(email, password);
        return Transformations.map(source, r -> { loading.postValue(false); return r; });
    }

    // GOOGLE LOGIN ------------------------------------------------------------------------
    public LiveData<Result> googleLogin(String idToken) {
        loading.setValue(true);
        LiveData<Result> source = repository.loginWithGoogle(idToken);
        return Transformations.map(source, r -> { loading.postValue(false); return r; });
    }

    // REGISTRAZIONE -----------------------------------------------------------------------
    public LiveData<Result> register(String name, String email, String password) {
        loading.setValue(true);
        LiveData<Result> source = repository.register(name, email, password);
        return Transformations.map(source, r -> { loading.postValue(false); return r; });
    }

    // LOGOUT ------------------------------------------------------------------------------
    public LiveData<Result> logout() {
        loading.setValue(true);
        LiveData<Result> source = repository.logout();
        return Transformations.map(source, r -> { loading.postValue(false); return r; });
    }

    // GETTER DEL MODEL --------------------------------------------------------------------
    public User getLoggedUser() { return repository.getLoggedUser(); }
}