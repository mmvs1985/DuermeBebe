package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/BABYCAKE.ttf");

        TextView title = (TextView) findViewById(R.id.startmaintitle);
        title.setTypeface(font);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
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
                    Constant.persistence = false;
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