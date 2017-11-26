package com.pmcoder.duermebeb.login.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.login.presenter.*;
import com.pmcoder.duermebeb.main.model.MainActivityImpl;
import com.pmcoder.duermebeb.createAccount.model.CreateAccountActivity;
import com.pmcoder.duermebeb.resetPassword.model.ResetPasswordActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        LoginActivityInt {

    private EditText username;
    private EditText password;
    private Button loginBtn;
    private ImageView facebookButton;
    private LoginActivityPresenter presenter;
    private View view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginActivityPresenterImpl(this);
        SharedPreferences preferences = getSharedPreferences("User-Data", Context.MODE_PRIVATE);

        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(this);

        facebookButton = (ImageView) findViewById(R.id.facebookButton);

        TextView addAccount = (TextView) findViewById(R.id.createaccount_button);
        addAccount.setOnClickListener(this);

        TextView resetPassword = (TextView) findViewById(R.id.recoverypassword_button);
        resetPassword.setOnClickListener(this);

        username = (EditText) findViewById(R.id.oldusername);
        username.setText(preferences.getString("EMAIL", ""));
        password = (EditText) findViewById(R.id.oldpass);


    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.newAuthStateListener();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.login_button:
                view = v;
                loginEmail();

            break;
            case R.id.createaccount_button:
                intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);

            break;
            case R.id.recoverypassword_button:
                intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            break;
        }
    }

    private void loginEmail(){

        String user = username.getText().toString().trim();
        String pass = password.getText().toString();

        if (user.equals("")){return;}
        if (!user.contains("@")) {return;}
        if (pass.equals("")){return;}

        username.setEnabled(false);
        password.setEnabled(false);
        loginBtn.setEnabled(false);

        presenter.logInEmail(this, user, pass);
    }

    @Override
    public void onStop(){
        super.onStop();

        presenter.removeAuthStateListener();
    }

    @Override
    public void goToMainActivity() {

        startActivity(new Intent(getApplicationContext(), MainActivityImpl.class));
        finish();
    }

    @Override
    public void makeToast(int message, int toastDuration) {

        Toast.makeText(getApplication(), message, toastDuration)
                .show();
    }

    @Override
    public void makeToast(String message, int toastDuration) {

        Toast.makeText(getApplication(), message, toastDuration)
                .show();
    }

    @Override
    public void makeSnackbar(int message, int duration) {

        Snackbar.make(view, message, duration).show();
    }

    @Override
    public void enableViews() {
        username.setEnabled(true);
        password.setEnabled(true);
        loginBtn.setEnabled(true);
    }
}
