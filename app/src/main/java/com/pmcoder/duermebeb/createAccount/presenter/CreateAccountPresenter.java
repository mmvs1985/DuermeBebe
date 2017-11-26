package com.pmcoder.duermebeb.createAccount.presenter;

import com.pmcoder.duermebeb.createAccount.model.CreateAccountActivity;

/**
 * Created by pmcoder on 11/11/17.
 */

public interface CreateAccountPresenter {

    boolean isOnline(CreateAccountActivity activity);

    void goToLoginActivity();

    void newUser(String email, String password, String name);

    void finishActivity();

    void createAccountError(Exception e);
}
