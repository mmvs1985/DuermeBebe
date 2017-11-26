package com.pmcoder.duermebeb.login.model;


/**
 * Created by pmcoder on 11/11/17.
 */

public interface LoginActivityInt {

    void goToMainActivity ();
    void makeToast(int message, int toastDuration);
    void makeToast(String message, int toastDuration);
    void makeSnackbar(int message, int duration);
    void enableViews();
}
