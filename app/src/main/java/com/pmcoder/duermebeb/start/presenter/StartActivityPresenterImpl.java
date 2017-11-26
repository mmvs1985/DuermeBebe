package com.pmcoder.duermebeb.start.presenter;

import com.pmcoder.duermebeb.start.model.StartActivity;
import com.pmcoder.duermebeb.start.repository.StartActivityRepository;
import com.pmcoder.duermebeb.start.repository.StartActivityRepositoryImpl;

/**
 * Created by pmcoder on 10/11/17.
 */

public class StartActivityPresenterImpl implements StartActivityPresenter {

    private StartActivity startActivity;
    private StartActivityRepository repository;

    public StartActivityPresenterImpl(StartActivity startActivity){
        this.startActivity = startActivity;
        repository = new StartActivityRepositoryImpl(this, startActivity);

    }

    @Override
    public void newAuthStateListener() {
        repository.newAuthStateListener();
    }

    @Override
    public void goToMainActivity() {
        startActivity.goToMainActivity();
    }

    @Override
    public void goToLoginActivity() {
        startActivity.goToLoginActivity();
    }

    @Override
    public void removeAuthStateListener() {
        repository.removeAuthStateListener();
    }
}
