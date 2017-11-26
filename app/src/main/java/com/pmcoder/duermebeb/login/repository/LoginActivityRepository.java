package com.pmcoder.duermebeb.login.repository;

import com.pmcoder.duermebeb.login.model.LoginActivity;

/**
 * Created by pmcoder on 11/11/17.
 */

public interface LoginActivityRepository {


    void newAuthStateListener();
    void logInEmail(LoginActivity applicationContext, String username, String password);

    void removeAuthStateListener();
}
