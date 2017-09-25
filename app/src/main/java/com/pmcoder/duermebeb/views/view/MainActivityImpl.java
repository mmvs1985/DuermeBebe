package com.pmcoder.duermebeb.views.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.adapter.ViewPagerAdapter;
import com.pmcoder.duermebeb.fragments.*;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import com.pmcoder.duermebeb.image.ImageUtil;
import com.pmcoder.duermebeb.services.MediaPlayerMainService;
import com.pmcoder.duermebeb.views.interactor.MainActivityInteractorImpl;
import com.pmcoder.duermebeb.views.presenter.MainActivityPresenter;
import com.pmcoder.duermebeb.views.presenter.MainActivityPresenterImpl;
import java.io.File;
import static com.pmcoder.duermebeb.R.string.*;
import static com.pmcoder.duermebeb.global.GlobalVariables.*;

public class MainActivityImpl extends AppCompatActivity implements MainActivity, View.OnClickListener{

    private NavigationView navigationView;
    private BottomNavigationView bNView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView profilePicture;
    private DatabaseReference mDatabaseReference = GlobalVariables.fbDatabase.getReference();
    private DatabaseReference mArtistChannel = mDatabaseReference.child("artist-channel");
    private DatabaseReference userData = mDatabaseReference
            .child("users").child(GlobalVariables.uid).child("userdata");
    private MainActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bNView = (BottomNavigationView) findViewById(R.id.main_bottomnavigation);

        presenter = new MainActivityPresenterImpl(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        loadViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1){
                    MainActivityInteractorImpl.fragmentStatus = getString(R.string.sounds);
                } else {
                    MainActivityInteractorImpl.fragmentStatus = getString(R.string.start);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        presenter.setTabIcons(tabLayout);
        presenter.setIconColor(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()), "#FFFFFF");

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
                if (tab.getPosition() == 1){
                    MainActivityInteractorImpl.fragmentStatus = getString(R.string.start);
                } else {
                    MainActivityInteractorImpl.fragmentStatus = getString(R.string.sounds);
                }

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

        Intent mediaService = new Intent(this, MediaPlayerMainService.class);
        startService(mediaService);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                navigation_drawer_open,
                navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_favoritos:
                        presenter.setExit(0);
                        presenter.proxim();
                        break;

                    case R.id.nav_share:
                        presenter.setExit(0);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.download_best_app) +
                                "https://play.google.com/store/apps/details?id=com.pmcoder.duermebeb");
                        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));

                        break;

                    case R.id.nav_ajustes:
                        presenter.setExit(0);
                        presenter.proxim();
                        break;

                    case R.id.nav_sesion:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        EXIT = 1;
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        GlobalVariables.uid = user.getUid();

        toggle.syncState();

        userData.keepSynced(true);

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()) {return;}

                View v = navigationView.getHeaderView(0);

                GlobalVariables.nameUser = dataSnapshot.child("name").getValue().toString();
                GlobalVariables.mailUser = dataSnapshot.child("email").getValue().toString();
                if (dataSnapshot.child("profilepic").exists()
                        && dataSnapshot.child("profilepic").getValue() != null){
                    GlobalVariables.profileImgBase64 = dataSnapshot
                            .child("profilepic").getValue().toString();
                }

                TextView username = (TextView) v.findViewById(R.id.usernavname);
                TextView mail = (TextView) v.findViewById(R.id.usernavmail);

                String saludo = getString(R.string.hi) + " " + GlobalVariables.nameUser;
                username.setText(saludo);
                mail.setText(GlobalVariables.mailUser);

                if (GlobalVariables.profileImgBase64 != null &&
                        !GlobalVariables.profileImgBase64.equals("")){
                    Bitmap bmp =
                            presenter.cropBitmap(ImageUtil
                                    .convert(GlobalVariables.profileImgBase64),
                                    profilePicture.getHeight(),
                                    profilePicture.getWidth());
                    profilePicture
                            .setImageBitmap(bmp);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mArtistChannel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren() == null) return;
                for (DataSnapshot artist: dataSnapshot.getChildren()){
                    if (artist.child("youtube").getValue().toString() == null) return;
                    String youtube = artist.child("youtube").getValue().toString();
                    if (artist.child("soundcloud").getValue().toString() == null) return;
                    String soundCloud = artist.child("soundcloud").getValue().toString();
                    if (artist.child("web").getValue().toString() == null) return;
                    String web= artist.child("web").getValue().toString();

                    GlobalVariables.artistSyncDB.put(artist.getKey(), new ElementoPlaylist(soundCloud, youtube, web));

                }
                if(artistChannelDB != artistSyncDB){
                    artistChannelDB = artistSyncDB;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        presenter.onButtonNavigationViewItemSelected(bNView);
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
        viewPagerAdapter.addFragment(presenter.loadServiceOnFragment(new MainFragment()));
        viewPagerAdapter.addFragment(new SleepSounds());
        viewPager.setAdapter(viewPagerAdapter);
    }

    //Controla el ícono del botón de play
    @Override
    public void playPauseButton(Boolean b){
        presenter.playPauseButton(b, bNView);
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
    public boolean checkStoragePermission(){
        return presenter.checkStoragePermission();
    }

        //Recibo eventos onClick
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgprofile:

                startActivity(new Intent(this, ProfilePictureActivity.class));
                break;
        }

    }

    //Se maneja la apertura y cierre del fragment informativo
    @Override
    public void openInfoFragment(String autor, String song) {

        presenter.openInfoFragment(autor, song, fragmentManager, drawer);
    }

    @Override
    public void closeInfoFragment() {

        presenter.closeInfoFragment();

    }

    @Override
    public void onBackPressed() {

        presenter.onBackPressed();
    }

}