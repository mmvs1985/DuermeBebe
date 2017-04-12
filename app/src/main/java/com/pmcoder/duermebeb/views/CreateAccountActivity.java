package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private TextInputEditText etEmail, etName, etPassword, etConfPassword;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setTitle("Crear Cuenta");
        }catch (Exception e){
            FirebaseCrash.report(e);
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
                                    firebaseAuth.getCurrentUser().sendEmailVerification();
                                } catch (Exception ignored) {
                                }
                                Toast.makeText(getApplicationContext(),
                                        R.string.verify_identity,
                                        Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

                if (Constant.funcionaInternet()){
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
}