package com.pmcoder.duermebeb.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import static com.pmcoder.duermebeb.R.drawable.*;
import static com.pmcoder.duermebeb.R.string.*;
import static com.pmcoder.duermebeb.fragments.MainFragment.mainAdapter;
import static com.pmcoder.duermebeb.services.MediaPlayerMainService.*;
import static com.pmcoder.duermebeb.global.GlobalVariables.*;

public class MainActivity extends AppCompatActivity implements Communicator, View.OnClickListener{

    public static NavigationView navigationView;
    public static MediaPlayerMainService mMPService;
    private static BottomNavigationView bNView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private String fragmentStatus;
    private TabLayout tabLayout;
    private ViewPager viewPager;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        loadViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcons();
        setIconColor(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()), "#FFFFFF");
        for (int i = 1; i < viewPagerAdapter.getCount(); i++){
            tabLayout.getTabAt(i)
                    .getIcon()
                    .setColorFilter(Color.parseColor("#4DD0E1"), PorterDuff.Mode.SRC_IN);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

        mMPService = new MediaPlayerMainService(getApplication());

        mReceiver = new PlayReceiver();
        intentFilter= new IntentFilter("BROADCAST_RECEIVER");

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
/*
                boolean fragmentTransaction = false;
                Fragment fragment = null;
*/
                switch(item.getItemId()){/*
                    case R.id.nav_inicio:
                        /*fragmentStatus = getString(R.string.start);
                        item.setChecked(true);
                        fragmentTransaction = true;
                        fragment = new MainFragment();
                        setSalir(0);
                        break;

                    case R.id.nav_favoritos:
                        fragmentStatus = getString(R.string.favoritos);
                        item.setChecked(true);
                        fragmentTransaction = true;
                        fragment = new FavoritesFragment();
                        setSalir(0);
                        break;
*/
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
/*
                if (fragmentTransaction){
                    fragmentManager.beginTransaction().replace(R.id.container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    drawer.closeDrawers();
                }
*/
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
    public void onResume() {
        super.onResume();

        registerReceiver(mReceiver, intentFilter);
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
                        if (fragmentStatus.equals(getString(R.string.start))){
                            if (mainListArray.size() < 1){
                                Snackbar.make(getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        } else if (fragmentStatus.equals(getString(R.string.favoritos))) {
                            if (favoritesArray.size() < 1){
                                Snackbar.make(getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        }

                        int i = (int) Math.floor(Math.random() * mainAdapter.getItemCount());
                        if (mMPService.mp == null){
                            Toast
                                    .makeText(getApplicationContext(),
                                            R.string.shuffle_starts,
                                            Toast.LENGTH_SHORT).show();
                            if (GlobalVariables.viewHolder != null){
                                GlobalVariables.viewHolder.setVisibility(View.GONE);
                            }
                            mMPService.setPlaying(mainListArray.get(i).getUrlsong());
                            bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
                        }else {
                            mMPService.setPlaying(songNow);
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

                View v = MainActivity.navigationView.getHeaderView(0);

                GlobalVariables.nameUser = dataSnapshot.child("name").getValue().toString();
                GlobalVariables.mailUser = dataSnapshot.child("email").getValue().toString();
                if (dataSnapshot.child("profilepic").exists() && dataSnapshot.child("profilepic").getValue() != null){
                    GlobalVariables.profileImgBase64 = dataSnapshot.child("profilepic").getValue().toString();
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

    private void proxim (){
        Toast.makeText(getApplication(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
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

        if (mReceiver.isInitialStickyBroadcast() || mReceiver.isOrderedBroadcast()) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if (mReceiver.isInitialStickyBroadcast() || mReceiver.isOrderedBroadcast()) {
            unregisterReceiver(mReceiver);
        }

    }

    public void playPause(Boolean b){
            if(b){
                this.bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(pause_48);
            }else {
                this.bNView.getMenu().findItem(R.id.play_pause_menu).setIcon(circled_play_48);
            }
        }

    @Override
    public void respond(String autor, String song) {

        GlobalVariables.infoArtist = autor;
        GlobalVariables.infoSong = song;

        infoFrag = new InfoFragment();

        fragmentManager.beginTransaction()
                .add(R.id.container, infoFrag)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();

        drawer.closeDrawers();

        fragmentStatus = "infoFragment";
    }

    @Override
    public void closeNotificationFragment() {

        setSalir(0);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgprofile:

                startActivity(new Intent(this, ProfilePicture.class));
                break;
        }

    }

    public static class PlayReceiver extends BroadcastReceiver{

        public PlayReceiver(){}

        MainActivity mainActivity = new MainActivity();

        @Override
        public void onReceive(Context context, Intent intent) {

                mainActivity.playPause(intent.getBooleanExtra("play", false));

        }
    }

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

    private void loadViewPager (ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(newFragmentInstance("Música", new MainFragment()));
        viewPagerAdapter.addFragment(newFragmentInstance("Sonidos", new SleepSounds()));
        viewPager.setAdapter(viewPagerAdapter);
    }

    private Fragment newFragmentInstance (String title, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        return fragment;
    }

    private void setTabIcons () {
        for (int i = 0; i < 2; i++){
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    private void setIconColor (TabLayout.Tab tab, String color) {
        tab.getIcon()
                .setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);

    }

}