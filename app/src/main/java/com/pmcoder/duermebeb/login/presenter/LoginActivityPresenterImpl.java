package com.pmcoder.duermebeb.login.presenter;

import com.pmcoder.duermebeb.login.interactor.LoginActivityInteractor;
import com.pmcoder.duermebeb.login.interactor.LoginActivityInteractorImpl;
import com.pmcoder.duermebeb.login.model.LoginActivity;
import com.pmcoder.duermebeb.login.model.LoginActivityInt;
import com.pmcoder.duermebeb.login.repository.LoginActivityRepository;
import com.pmcoder.duermebeb.login.repository.LoginActivityRepositoryImpl;

/**
 * Created by pmcoder on 11/11/17.
 */

public class LoginActivityPresenterImpl implements LoginActivityPresenter {

    private LoginActivityRepository repository;
    private LoginActivityInt loginActivityInt;
    private LoginActivityInteractor interactor;

    public LoginActivityPresenterImpl (LoginActivity loginActivity){

        interactor = new LoginActivityInteractorImpl();
        loginActivityInt = loginActivity;
        repository = new LoginActivityRepositoryImpl(this, loginActivity);
    }

    @Override
    public void newAuthStateListener() {

        repository.newAuthStateListener();
    }

    @Override
    public void goToMainActivity() {
        loginActivityInt.goToMainActivity();
    }

    @Override
    public void makeToast(int message, int toastDuration) {

        loginActivityInt.makeToast(message, toastDuration);
    }

    @Override
    public void makeToast(String message, int toastDuration) {
        loginActivityInt.makeToast(message, toastDuration);
    }

    @Override
    public void removeAuthStateListener() {

        repository.removeAuthStateListener();
    }

    @Override
    public void logInEmail(LoginActivity applicationContext, String user, String pass) {

        repository.logInEmail(applicationContext, user, pass);
    }

    @Override
    public void makeSnackbar(int message, int duration) {
        loginActivityInt.makeSnackbar(message, duration);
    }

    @Override
    public void enableViews() {
        loginActivityInt.enableViews();
    }

    @Override
    public boolean isOnline(LoginActivity context) {
        return interactor.isOnline(context);
    }
}
