package com.pmcoder.duermebeb.start.model;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.login.model.LoginActivity;
import com.pmcoder.duermebeb.main.model.MainActivityImpl;
import com.pmcoder.duermebeb.start.presenter.*;

public class StartActivity extends AppCompatActivity implements StartActivityInt {

    private StartActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        presenter = new StartActivityPresenterImpl(this);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/BABYCAKE.ttf");

        final TextView title = findViewById(R.id.startmaintitle);
        title.setTypeface(font);

    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.newAuthStateListener();

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.removeAuthStateListener();
    }

    @Override
    public void goToMainActivity() {

        startActivity(new Intent(getApplicationContext(),
                MainActivityImpl.class));
        finish();
    }

    @Override
    public void goToLoginActivity() {
        startActivity(new Intent(getApplicationContext(),
                LoginActivity.class));
        finish();
    }
}
