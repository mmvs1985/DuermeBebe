package com.pmcoder.duermebeb.resetPassword.presenter;

import com.pmcoder.duermebeb.resetPassword.interactor.ResetPasswordInteractor;
import com.pmcoder.duermebeb.resetPassword.interactor.ResetPasswordInteractorImpl;
import com.pmcoder.duermebeb.resetPassword.model.ResetPasswordActivity;

/**
 * Created by pmcoder on 11/11/17.
 */

public class ResetPasswordPresenterImpl implements ResetPasswordPresenter {

    private ResetPasswordInteractor interactor;

    public ResetPasswordPresenterImpl (){

        interactor = new ResetPasswordInteractorImpl();
    }

    @Override
    public boolean isOnline(ResetPasswordActivity activity) {
        return interactor.isOnline(activity);
    }
}
