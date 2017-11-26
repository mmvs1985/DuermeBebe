package com.pmcoder.duermebeb.start.presenter;

/**
 * Created by pmcoder on 10/11/17.
 */

public interface StartActivityPresenter {

    void newAuthStateListener();
    void goToMainActivity();
    void goToLoginActivity();
    void removeAuthStateListener();
}
