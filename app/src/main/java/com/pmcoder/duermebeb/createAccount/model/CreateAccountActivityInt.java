package com.pmcoder.duermebeb.createAccount.model;

/**
 * Created by pmcoder on 12/11/17.
 */

public interface CreateAccountActivityInt {

    void goToLoginActivity();

    void finishActivity();

    void createAccountError(Exception e);
}
