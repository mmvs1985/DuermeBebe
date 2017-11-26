package com.pmcoder.duermebeb.login.interactor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.pmcoder.duermebeb.login.model.LoginActivity;

/**
 * Created by pmcoder on 15/11/17.
 */

public class LoginActivityInteractorImpl implements LoginActivityInteractor {



    @Override
    public boolean isOnline(LoginActivity context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivity != null;
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
