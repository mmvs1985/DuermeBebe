package com.pmcoder.duermebeb.login.presenter;

import com.pmcoder.duermebeb.login.model.LoginActivity;

/**
 * Created by pmcoder on 11/11/17.
 */

public interface LoginActivityPresenter {

    void newAuthStateListener();

    void goToMainActivity();

    void makeToast(int message, int toastDuration);

    void makeToast(String message, int toastDuration);

    void removeAuthStateListener();

    void logInEmail(LoginActivity applicationContext, String user, String pass);

    void makeSnackbar(int message, int duration);

    void enableViews();

    boolean isOnline(LoginActivity context);
}
