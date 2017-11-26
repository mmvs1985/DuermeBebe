package com.pmcoder.duermebeb.main.model;

import android.content.*;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.main.adapter.ViewPagerAdapter;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.basicModels.ImageUtil;
import com.pmcoder.duermebeb.main.service.MediaPlayerMainService;
import com.pmcoder.duermebeb.main.model.fragments.*;
import com.pmcoder.duermebeb.login.model.LoginActivity;
import com.pmcoder.duermebeb.main.presenter.*;
import com.pmcoder.duermebeb.profilePicture.model.ProfilePictureFragment;
import java.io.File;
import static com.pmcoder.duermebeb.R.drawable.*;
import static com.pmcoder.duermebeb.R.string.*;

public class MainActivityImpl extends AppCompatActivity implements MainActivity, View.OnClickListener{

    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView profilePicture;
    private String tabLayoutString = "TabLayout", viewPagerString = "ViewPager";
    private MainActivityPresenter presenter;
    private ImageView playButton, pauseButton;
    private ProgressBar progressBar;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainActivityPresenterImpl(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        preferences = getSharedPreferences("User-Data", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        loadViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        TextView username = (TextView) navigationView.
                getHeaderView(0).findViewById(R.id.usernavname);
        TextView mail = (TextView) navigationView.
                getHeaderView(0).findViewById(R.id.usernavmail);
        String saludo = getString(R.string.hi)
                + " "
                + preferences.getString("NAME", "Usuario");

        playButton = (ImageView) findViewById(R.id.imgplay);
        pauseButton = (ImageView) findViewById(R.id.imgpause);

        presenter.setTabIcons(tabLayout);
        presenter.setIconColor(tabLayout
                .getTabAt(tabLayout.getSelectedTabPosition()), "#FFFFFF");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_bottomnavigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                navigation_drawer_open,
                navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        username.setText(saludo);
        mail.setText(preferences.getString("EMAIL", ""));

        startService(new Intent(this, MediaPlayerMainService.class));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                presenter.setFragmentSelected(position, viewPagerString);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 1; i < viewPagerAdapter.getCount(); i++){
            try {
                tabLayout.getTabAt(i)
                        .getIcon()
                        .setColorFilter(Color.parseColor("#4DD0E1"), PorterDuff.Mode.SRC_IN);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                presenter.setFragmentSelected(tab.getPosition(),
                        tabLayoutString);

                presenter.setIconColor(tab, "#FFFFFF");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                presenter.setIconColor(tab, "#4DD0E1");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_favoritos:
                        presenter.setExit(0);

                        presenter.openFloatFragment("FAV",
                                null, null, drawer);
                        break;

                    case R.id.nav_share:
                        presenter.setExit(0);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.download_best_app) +
                                getString(R.string.appStoreUrl));
                        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));

                        break;

                    case R.id.nav_ajustes:
                        presenter.setExit(0);
                        presenter.proxim();
                        break;

                    case R.id.nav_sesion:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        presenter.setExit(0);
                        break;

                }
                return false;
            }
        });

        profilePicture = (ImageView) navigationView
                .getHeaderView(0)
                .findViewById(R.id.imgprofile);
        profilePicture.setOnClickListener(this);

        presenter.checkStoragePermission();

    }

    @Override
    public void onStart(){
        super.onStart();

        toggle.syncState();

        presenter.createDataBaseListeners();

        presenter.onButtonNavigationViewItemSelected(bottomNavigationView);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.stopPlaying();
        stopService(new Intent(getBaseContext(), MediaPlayerMainService.class));
    }

    //Se cargan los fragments de Música y Sonidos
    public void loadViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle mFragBundle = new Bundle();
        mFragBundle.putString("uid", preferences.getString("UID", ""));
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(mFragBundle);

        viewPagerAdapter.addFragment(mainFragment);
        viewPagerAdapter.addFragment(new SleepSounds());
        viewPager.setAdapter(viewPagerAdapter);
    }

    //Controla el ícono del botón de play
    @Override
    public void playPauseButton(Boolean b){
        if (b) {
                bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(pause_48);
                if (playButton != null) {
                    playButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);
                }
            } else {
                bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(circled_play_48);
                if (playButton != null) {
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.GONE);
                }
            }
    }

    @Override
    public boolean setPlayingUrl(String song) {

        return presenter.setPlayingUrl(song);
    }

    @Override
    public boolean setPlayingLocal(File song){

        return presenter.setPlayingLocal(song);
    }

    @Override
    public void setProfilePicture() {
        Bitmap bmp =
                presenter.cropBitmap(ImageUtil
                                .convert(GlobalVariables.profileImgBase64),
                        profilePicture.getHeight(),
                        profilePicture.getWidth());
        profilePicture
                .setImageBitmap(bmp);
    }

    @Override
    public boolean isOnline() {
        return presenter.isOnline();
    }

    @Override
    public void setViews(ImageView playButton, ImageView pauseButton, ProgressBar progressBar) {
        this.playButton = playButton;
        this.pauseButton = pauseButton;
        this.progressBar = progressBar;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public boolean checkStoragePermission(){
        return presenter.checkStoragePermission();
    }

    //Recibo eventos onClick
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgprofile:

                startActivity(new Intent(this, ProfilePictureFragment.class));
                break;
        }

    }

    //Se maneja la apertura y cierre del fragment informativo desde el Adapter
    @Override
    public void openFloatFragment(String autor, String song) {

        presenter.openFloatFragment("INFO", autor, song, drawer);
    }

    @Override
    public void closeFloatFragment() {

        presenter.closeFloatFragment();

    }

    @Override
    public void onBackPressed() {

        presenter.onBackPressed();
    }

}