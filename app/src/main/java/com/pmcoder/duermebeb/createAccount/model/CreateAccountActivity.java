package com.pmcoder.duermebeb.createAccount.model;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.createAccount.presenter.CreateAccountPresenter;
import com.pmcoder.duermebeb.createAccount.presenter.CreateAccountPresenterImpl;
import com.pmcoder.duermebeb.login.model.LoginActivity;
import com.pmcoder.duermebeb.main.model.MainActivityImpl;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener,
CreateAccountActivityInt{

    private TextInputEditText etEmail, etName, etPassword, etConfPassword;
    private CreateAccountPresenter presenter;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        presenter = new CreateAccountPresenterImpl(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setTitle(R.string.createAccount);
        }catch (Exception e){
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmail = (TextInputEditText) findViewById(R.id.newemail);
        etName = (TextInputEditText) findViewById(R.id.newname);
        etPassword = (TextInputEditText) findViewById(R.id.newpassword);
        etConfPassword = (TextInputEditText) findViewById(R.id.newconfPassword);
        Button joinUs = (Button) findViewById(R.id.joinUs);
        joinUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.joinUs:
                String email = etEmail.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confPassword = etConfPassword.getText().toString().trim();

                if (email.equals("")) {
                    etName.setError(getString(R.string.requiredData));
                    return;
                }

                if (!email.contains("@")){
                    etName.setError(getString(R.string.wrongMail));
                    return;
                }

                if (name.equals("")) {
                    etName.setError(getString(R.string.requiredData));
                    return;
                }
                if (password.equals("")) {
                    etPassword.setError(getString(R.string.requiredData));
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError(getString(R.string.tooShort));
                    return;
                }
                if (confPassword.equals("")) {
                    etConfPassword.setError(getString(R.string.requiredData));
                    return;
                }
                if (!password.equals(confPassword)) {
                    Toast.makeText(this, R.string.differentPasswords,
                            Toast.LENGTH_SHORT).show();
                    etConfPassword.setError(getString(R.string.reviewThat));
                    etPassword.setError(getString(R.string.reviewThat));
                    return;
                }

                view = v;
                   presenter.newUser(email, password, name);

                break;
        }

    }

    @Override
    public void goToLoginActivity() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        Toast.makeText(getApplicationContext(), R.string.confirmEmail,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void createAccountError(Exception e) {
        if (presenter.isOnline(this)){

            if (view != null){

                if (e.getMessage().equals(getString(R.string.CONSTANTEMAILEXISTS))){
                    Snackbar.make(view, R.string.account_alredy_exists,
                            Snackbar.LENGTH_LONG).show();
                }else {
                    Snackbar.make(view, R.string.wrong_data_string,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }else{

            if (view != null){
                Snackbar.make(view, R.string.wrong_internet_conection,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
