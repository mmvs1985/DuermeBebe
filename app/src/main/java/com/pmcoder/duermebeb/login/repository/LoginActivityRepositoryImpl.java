package com.pmcoder.duermebeb.login.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.login.model.LoginActivity;
import com.pmcoder.duermebeb.login.presenter.LoginActivityPresenter;
import com.pmcoder.duermebeb.login.presenter.LoginActivityPresenterImpl;

/**
 * Created by pmcoder on 11/11/17.
 */

public class LoginActivityRepositoryImpl implements LoginActivityRepository {

    private LoginActivityPresenter presenter;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedPreferences preferences;
    private LoginActivity loginActivity;

    public LoginActivityRepositoryImpl (LoginActivityPresenterImpl presenter, LoginActivity loginActivity){

        this.presenter = presenter;
        this.loginActivity = loginActivity;
        firebaseAuth = FirebaseAuth.getInstance();
        preferences = loginActivity.getSharedPreferences("User-Data", Context.MODE_PRIVATE);
    }

    @Override
    public void newAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                    if(user.isEmailVerified()) {

                        if (!preferences.getString("UID", "").equals(user.getUid())){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("UID", user.getUid());
                            editor.putString("NAME", user.getDisplayName());
                            editor.putString("EMAIL", user.getEmail());
                            editor.apply();
                        }

                        presenter.goToMainActivity();

                        if (user.getDisplayName() != null) {
                            presenter
                                    .makeToast(loginActivity.getResources().getString(R.string.hello)
                                                    + " "
                                                    + user.getDisplayName(),
                                            Toast.LENGTH_LONG);
                        } else{
                            presenter.makeToast(R.string.welcome, Toast.LENGTH_SHORT);
                        }
                    }else {
                        presenter.makeToast(R.string.confirmEmail, Toast.LENGTH_LONG);
                        presenter.enableViews();
                        user.sendEmailVerification();
                        firebaseAuth.signOut();
                    }
                }else{
                    Log.i("SESION", "Sesión cerrada");
                }
            }
        };


        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void logInEmail(final LoginActivity context, String username, String password) {

        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(context,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            if (presenter.isOnline(context)){
                                Log.i("LoginFailure", task.getException().getMessage());

                                if (task.getException().getMessage()
                                        .contains(loginActivity.getString(R.string.userNotExistsError))){
                                    presenter.makeToast(loginActivity
                                            .getString(R.string.userNotExists),
                                            Toast.LENGTH_SHORT);
                                }else {
                                    presenter.makeSnackbar(R.string.wrongUsernameOrPassword,
                                            Snackbar.LENGTH_SHORT);
                                }
                                presenter.enableViews();
                            }else {
                                presenter.makeSnackbar(R.string.connectionFailed,
                                        Snackbar.LENGTH_SHORT);
                                presenter.enableViews();
                            }
                            presenter.enableViews();
                        }
                    }
                }).addOnFailureListener(context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("LoginFailure", e.getMessage());
            }
        });
    }

    @Override
    public void removeAuthStateListener() {

        firebaseAuth.removeAuthStateListener(mAuthListener);
    }
}
