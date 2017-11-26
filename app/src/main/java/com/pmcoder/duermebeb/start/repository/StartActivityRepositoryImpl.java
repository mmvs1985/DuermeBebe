package com.pmcoder.duermebeb.start.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;
import com.pmcoder.duermebeb.start.model.StartActivity;
import com.pmcoder.duermebeb.start.presenter.*;

/**
 * Created by pmcoder on 10/11/17.
 */

public class StartActivityRepositoryImpl implements StartActivityRepository {

    private SharedPreferences preferences;
    private StartActivityPresenter presenter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public StartActivityRepositoryImpl(StartActivityPresenterImpl presenter, StartActivity activity){

        this.presenter = presenter;
        preferences = activity.getSharedPreferences("User-Data", Context.MODE_PRIVATE);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void newAuthStateListener() {

        final SharedPreferences.Editor editor = preferences.edit();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {

                    if (!preferences.getString("UID", "").equals(user.getUid())) {

                        editor.putString("UID", user.getUid());
                        if(user.getDisplayName() != null) editor.putString("NAME", user.getUid());
                        if(user.getEmail() != null) editor.putString("EMAIL", user.getEmail());
                        editor.apply();
                    }

                    presenter.goToMainActivity();

                } else {

                    presenter.goToLoginActivity();
                }
            }
        };

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void removeAuthStateListener() {
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
