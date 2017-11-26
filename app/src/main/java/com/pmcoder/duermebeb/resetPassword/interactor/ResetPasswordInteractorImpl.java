package com.pmcoder.duermebeb.resetPassword.interactor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.pmcoder.duermebeb.resetPassword.model.ResetPasswordActivity;

/**
 * Created by pmcoder on 11/11/17.
 */

public class ResetPasswordInteractorImpl implements ResetPasswordInteractor {
    @Override
    public boolean isOnline(ResetPasswordActivity activity) {

        ConnectivityManager connectivity = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivity != null;
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
