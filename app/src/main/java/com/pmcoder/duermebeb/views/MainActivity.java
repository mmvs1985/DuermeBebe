package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
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
import com.pmcoder.duermebeb.interfaces.Communicator;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import com.pmcoder.duermebeb.image.ImageUtil;
import com.pmcoder.duermebeb.services.MediaPlayerMainService;
import java.io.File;
import static com.pmcoder.duermebeb.R.drawable.*;
import static com.pmcoder.duermebeb.R.string.*;
import static com.pmcoder.duermebeb.fragments.MainFragment.mainAdapter;
import static com.pmcoder.duermebeb.services.MediaPlayerMainService.*;
import static com.pmcoder.duermebeb.global.GlobalVariables.*;

public class MainActivity extends AppCompatActivity implements Communicator, View.OnClickListener{

    private NavigationView navigationView;
    private MediaPlayerMainService mMPService;
    private BottomNavigationView bNView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private String fragmentStatus;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private InfoFragment infoFrag;
    private ImageView profilePicture;
    private DatabaseReference mDatabaseReference = GlobalVariables.fbDatabase.getReference();
    private DatabaseReference mArtistChannel = mDatabaseReference.child("artist-channel");
    private DatabaseReference userData = mDatabaseReference
            .child("users").child(GlobalVariables.uid).child("userdata");
    private int tabIcons[] = {R.drawable.traklist_48, R.drawable.sounds_48};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStatus = getString(R.string.start);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        loadViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1){
                    fragmentStatus = getString(R.string.sounds);
                } else {
                    fragmentStatus = getString(R.string.start);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcons();
        setIconColor(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()), "#FFFFFF");

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
                    fragmentStatus = getString(R.string.start);
                } else {
                    fragmentStatus = getString(R.string.sounds);
                }

                setIconColor(tab, "#FFFFFF");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setIconColor(tab, "#4DD0E1");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mMPService = new MediaPlayerMainService(this);

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
                        setSalir(0);
                        proxim();
                        break;

                    case R.id.nav_share:
                        setSalir(0);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.download_best_app) +
                                "https://play.google.com/store/apps/details?id=com.pmcoder.duermebeb");
                        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));

                        break;

                    case R.id.nav_ajustes:
                        setSalir(0);
                        proxim();
                        break;

                    case R.id.nav_sesion:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        EXIT = 1;
                        setSalir(0);
                        break;

                }
                return false;
            }
        });

        bNView = (BottomNavigationView) findViewById(R.id.main_bottomnavigation);

        profilePicture = (ImageView) navigationView
                .getHeaderView(0)
                .findViewById(R.id.imgprofile);
        profilePicture.setOnClickListener(this);

    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        GlobalVariables.uid = user.getUid();

        bNView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.play_pause_menu:

                        if ( fragmentStatus.equals(getString(R.string.start))){
                            if (mainListArray.size() < 2){
                                Snackbar.make(getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                            else {
                                playShuffleUrl();
                            }
                        }
                        else if (fragmentStatus.equals(getString(R.string.sounds))) {
                            if (soundsArray.size() < 2){
                                Snackbar.make(getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                            else {
                                playShuffleLocal();
                            }
                        }

                        break;

                    case R.id.stop_menu:
                        mMPService.stop();

                        break;

                }
                return false;
            }
        });

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
                            cropBitmap(ImageUtil
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
    }

    @Override
    public void onBackPressed() {

        if (fragmentStatus != null && fragmentStatus.equals("infoFragment")){
            fragmentManager
                    .beginTransaction()
                    .remove(infoFrag)
                    .commit();

            fragmentStatus = " ";
            return;
        }

        EXIT +=1;
        if (EXIT == 2){
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(), R.string.press_again_2_exit, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void setSalir(int exit) {
        EXIT = exit;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMPService.stop();
        stopService(new Intent(getBaseContext(), MediaPlayerMainService.class));
    }
        //Controla el ícono del botón de play
    @Override
    public void playPauseButton(Boolean b){
            if(b){
                bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(pause_48);
            }else {
                bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(circled_play_48);
            }
        }

    @Override
    public void setPlayingUrl(String song) {
        mMPService.setPlayingUrl(song);
    }

    @Override
    public void setPlayingLocal(File song){
        mMPService.setPlayingLocal(song);
    }

    //Se maneja la apertura y cierre del fragment informativo
    @Override
    public void openInfoFragment(String autor, String song) {

        GlobalVariables.infoArtist = autor;
        GlobalVariables.infoSong = song;

        infoFrag = new InfoFragment();

        fragmentManager.beginTransaction()
                .add(R.id.container, infoFrag)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();

        drawer.closeDrawers();
    }

    @Override
    public void closeInfoFragment() {

        setSalir(0);

    }
        //Recibo eventos onClick
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgprofile:

                startActivity(new Intent(this, ProfilePicture.class));
                break;
        }

    }

        //Se recorta la foto de perfil del Header en el NavigationView
    private Bitmap cropBitmap(Bitmap original, int height, int width) {
        Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(croppedImage);

        Rect srcRect = new Rect(0, 0, original.getWidth(), original.getHeight());
        Rect dstRect = new Rect(0, 0, width, height);

        int dx = (srcRect.width() - dstRect.width()) / 2;
        int dy = (srcRect.height() - dstRect.height()) / 2;

        // If the srcRect is too big, use the center part of it.
        srcRect.inset(Math.max(0, dx), Math.max(0, dy));

        // If the dstRect is too big, use the center part of it.
        dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

        // Draw the cropped bitmap in the center
        canvas.drawBitmap(original, srcRect, dstRect, null);

        original.recycle();

        return croppedImage;
    }

        //Se cargan los fragments de Música y Sonidos
    private void loadViewPager (ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(loadServiceOnFragment(new MainFragment()));
        viewPagerAdapter.addFragment(new SleepSounds());
        viewPager.setAdapter(viewPagerAdapter);
    }

    private Fragment loadServiceOnFragment (Fragment fragment){
        fragment.setReenterTransition(mMPService);
        return fragment;
    }

        //Se añaden los íconos a las tabs
    private void setTabIcons () {
        for (int i = 0; i < 2; i++){
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

        //Se cambia el color de los íconos
    private void setIconColor (TabLayout.Tab tab, String color) {
        tab.getIcon()
                .setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);

    }

        //play Shuffle
    public void playShuffleUrl (){

        int i = (int) Math.floor(Math.random() * mainAdapter.getItemCount());

        if (mMPService.mp == null){
            Toast
                    .makeText(getApplicationContext(),
                            R.string.shuffle_starts,
                            Toast.LENGTH_SHORT).show();
            if (GlobalVariables.viewHolder != null){
                GlobalVariables.viewHolder.setVisibility(View.GONE);
            }
            setPlayingUrl(mainListArray.get(i).getUrlsong());
            bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
        }else {
            setPlayingUrl(songNow);
        }
    }

    public void playShuffleLocal (){

        int i = (int) Math.floor(Math.random() * soundsArray.size());

        if (mMPService.mp == null){
            Toast
                    .makeText(getApplicationContext(),
                            R.string.shuffle_starts,
                            Toast.LENGTH_SHORT).show();
            if (GlobalVariables.viewHolder != null){
                GlobalVariables.viewHolder.setVisibility(View.GONE);
            }
            setPlayingLocal(new File(SleepSounds.rootPath.getAbsolutePath() +
                    "/" + soundsArray.get(i).getUrlsong()));
            bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
        }else {
            setPlayingLocal(new File(SleepSounds.rootPath.getAbsolutePath() +
                    "/" + soundNow));
        }
    }

        //Para próximos añadidos
    private void proxim (){
        Toast.makeText(getApplication(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

}