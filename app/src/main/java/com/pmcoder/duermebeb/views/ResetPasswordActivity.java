package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setTitle("Recuperar contraseña");
        }catch (Exception e){
            FirebaseCrash.report(e);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.resetPassET);
        Button resetPass = (Button) findViewById(R.id.resetPassBtn);
        resetPass.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resetPassBtn:

                if (!Constant.funcionaInternet()){
                    Snackbar.make(v, "Revisa tu conexión an internet", Snackbar.LENGTH_LONG).show();
                    return;
                }else{
                    if (email.getText().toString().equals("")){
                        email.setError("Coloca tu correo electrónico aquí");
                        return;
                    }else {
                        mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),
                                            "Revisa tu correo electrónico para recuperar tu cuenta",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplication(), LoginActivity.class));
                                    finish();
                                }else{
                                    email.setError("Dirección de correo errónea");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                email.setError("Ups! Probablemente cu cuenta no existe");
                            }
                        });
                    }
                }

            break;
        }
    }
}
