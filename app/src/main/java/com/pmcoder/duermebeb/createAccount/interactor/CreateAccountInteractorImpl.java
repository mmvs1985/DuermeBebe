package com.pmcoder.duermebeb.createAccount.interactor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.pmcoder.duermebeb.createAccount.model.CreateAccountActivity;

/**
 * Created by pmcoder on 11/11/17.
 */

public class CreateAccountInteractorImpl implements CreateAccountInteractor {


    @Override
    public boolean isOnline(CreateAccountActivity activity) {

        ConnectivityManager connectivity = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivity != null;
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
