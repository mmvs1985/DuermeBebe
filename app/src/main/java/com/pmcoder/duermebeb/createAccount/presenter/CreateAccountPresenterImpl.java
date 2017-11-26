package com.pmcoder.duermebeb.createAccount.presenter;

import android.view.View;

import com.pmcoder.duermebeb.createAccount.interactor.CreateAccountInteractor;
import com.pmcoder.duermebeb.createAccount.interactor.CreateAccountInteractorImpl;
import com.pmcoder.duermebeb.createAccount.model.CreateAccountActivity;
import com.pmcoder.duermebeb.createAccount.model.CreateAccountActivityInt;
import com.pmcoder.duermebeb.createAccount.repository.CreateAccountRepository;
import com.pmcoder.duermebeb.createAccount.repository.CreateAccountRepositoryImpl;

/**
 * Created by pmcoder on 11/11/17.
 */

public class CreateAccountPresenterImpl implements CreateAccountPresenter {

    private CreateAccountInteractor interactor;
    private CreateAccountActivityInt createAccountActivity;
    private CreateAccountRepository repository;


    public CreateAccountPresenterImpl (CreateAccountActivity createAccoutActivity){

        interactor = new CreateAccountInteractorImpl();
        repository = new CreateAccountRepositoryImpl(this, createAccoutActivity);
        this.createAccountActivity = createAccoutActivity;
    }

    @Override
    public boolean isOnline(CreateAccountActivity activity) {

        return interactor.isOnline(activity);
    }

    @Override
    public void goToLoginActivity() {
        createAccountActivity.goToLoginActivity();
    }

    @Override
    public void newUser(String email, String password, String name) {
        repository.newUSer(email, password, name);
    }

    @Override
    public void finishActivity() {
        createAccountActivity.finishActivity();
    }

    @Override
    public void createAccountError(Exception e) {
        createAccountActivity.createAccountError(e);
    }
}
