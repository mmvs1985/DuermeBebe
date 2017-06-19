package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.golbal.GlobalVariables;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputEditText etEmail, etName, etPassword, etConfPassword;
    private DatabaseReference database = GlobalVariables.fbDatabase.getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setTitle("Crear Cuenta");
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


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.i("SESION", "Sesión iniciada");
                    GlobalVariables.uid = user.getUid();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Iniciando Sesión",
                            Toast.LENGTH_LONG).show();
                }else{
                    Log.i("SESION", "Sesión cerrada");
                }
            }
        };
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
                    etName.setError("Campo requerido");
                    return;
                }
                if (name.equals("")) {
                    etName.setError("Campo Requerido");
                    return;
                }
                if (password.equals("")) {
                    etPassword.setError("Campo Requerido");
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("La contraseña debe ser mayor a seis caracteres");
                    return;
                }
                if (confPassword.equals("")) {
                    etConfPassword.setError("Campo requerido");
                    return;
                }
                if (!password.equals(confPassword)) {
                    Toast.makeText(this, "Tus contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    etConfPassword.setError("Revisa estos datos");
                    etPassword.setError("Revisa estos datos");
                    return;
                }

                    newUser(email, password, name, v);

                    break;
        }

    }

    private void newUser(final String email, final String password, final String name, final View v){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                try {
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    setUserDatabase(uid, name, email);
                                } catch (Exception ignored) {
                                }
                                finish();
                            }
                            else {
                            Snackbar.make(v,
                                    R.string.sorry_problems,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (GlobalVariables.funcionaInternet()){
                    if (e.getMessage().equals(getString(R.string.CONSTANTEMAILEXISTS))){
                        Snackbar.make(v, R.string.account_alredy_exists,
                                Snackbar.LENGTH_LONG).show();
                    }else {
                        Snackbar.make(v, R.string.wrong_data_string,
                                Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar.make(v, R.string.wrong_internet_conection,
                            Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void setUserDatabase(String uid, String name, String email){
        database.setValue(uid);
        database.child(uid).child("userdata").child("name").setValue(name);
        database.child(uid).child("userdata").child("email").setValue(email);
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
