package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.crash.FirebaseCrash;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private EditText username;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(mAuthListener);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        Button loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(this);

        TextView addAccount = (TextView) findViewById(R.id.createaccount_button);
        addAccount.setOnClickListener(this);

        TextView resetPassword = (TextView) findViewById(R.id.recoverypassword_button);
        resetPassword.setOnClickListener(this);

        username = (EditText) findViewById(R.id.oldusername);
        password = (EditText) findViewById(R.id.oldpass);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.i("SESION", "Sesión iniciada");
                    Constant.uid = user.getUid();
                    if (!Constant.persistence){
                        try {
                            Constant.fbDatabase.setPersistenceEnabled(true);
                        }catch (Exception e){
                            FirebaseCrash.report(e.fillInStackTrace());
                        }
                    }
                    Constant.persistence = true;
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{
                    Log.i("SESION", "Sesión cerrada");
                }
            }
        };


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.login_button:
                login(v);

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

    private void login(final View v){

        String user = username.getText().toString().trim();
        String pass = password.getText().toString();

        if (user.equals("")){return;}
        if (pass.equals("")){return;}
        

        firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplication(), "Bienvenid@!!",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(v, "Revisa tu conexión, se registran problemas técnicos",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (Constant.funcionaInternet()){
                Snackbar.make(v, "Usuario o contraseña incorrectos", Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(v, "Revisa tu conexión a internet", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public void onStop(){
        super.onStop();

        firebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
