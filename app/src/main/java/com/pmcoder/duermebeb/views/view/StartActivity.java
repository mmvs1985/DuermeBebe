package com.pmcoder.duermebeb.views.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/BABYCAKE.ttf");

        final TextView title = (TextView) findViewById(R.id.startmaintitle);
        title.setTypeface(font);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                    Log.i("SESION", "Sesión iniciada");
                    GlobalVariables.uid = user.getUid();

                    if (!GlobalVariables.persistence){

                        try {
                            GlobalVariables.fbDatabase.setPersistenceEnabled(true);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    GlobalVariables.persistence = true;
                    startActivity(new Intent(getApplicationContext(), MainActivityImpl.class));
                    finish();

                }
                else{

                    GlobalVariables.persistence = false;
                    Log.i("SESION", "Sesión cerrada");

                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
