package com.pmcoder.duermebeb.resetPassword.model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.login.model.LoginActivity;
import com.pmcoder.duermebeb.resetPassword.presenter.ResetPasswordPresenter;
import com.pmcoder.duermebeb.resetPassword.presenter.ResetPasswordPresenterImpl;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private FirebaseAuth mAuth;
    private ResetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        presenter = new ResetPasswordPresenterImpl();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setTitle(R.string.recoverPassTitle);
        }catch (Exception e){
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.resetPassET);
        Button resetPass = findViewById(R.id.resetPassBtn);
        resetPass.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resetPassBtn:

                if (presenter.isOnline(this)){
                    Snackbar.make(v, getString(R.string.wrong_internet_conection), Snackbar.LENGTH_LONG).show();
                    return;
                }else{
                    if (email.getText().toString().equals("")){
                        email.setError(getString(R.string.putEmailHere));
                        return;
                    }else {
                        mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),
                                            R.string.reviewEmailAccount,
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplication(), LoginActivity.class));
                                    finish();
                                }else{
                                    email.setError(getString(R.string.wrongEmail));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                email.setError(getString(R.string.emailNotExists));
                            }
                        });
                    }
                }

            break;
        }
    }
}
