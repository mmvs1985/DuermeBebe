package com.pmcoder.duermebeb.views;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.fragments.*;
import com.pmcoder.duermebeb.interfaces.Communicator;
import com.pmcoder.duermebeb.services.MediaPlayerMainService;
import static com.pmcoder.duermebeb.R.drawable.*;
import static com.pmcoder.duermebeb.R.string.*;
import static com.pmcoder.duermebeb.fragments.MainFragment.mainAdapter;
import static com.pmcoder.duermebeb.services.MediaPlayerMainService.*;
import static com.pmcoder.duermebeb.constants.Constant.*;

public class MainActivity extends AppCompatActivity implements Communicator{

    public static NavigationView navigationView;
    public static MediaPlayerMainService mMPService;
    private static BottomNavigationView bNView;
    private AppBarLayout appbar;
    private NestedScrollView nScrollView;
    private CollapsingToolbarLayout collToolLay;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private TextView toolbarTitle;
    private String fragmentStatus;
    private InfoFragment infoFrag;
    private DatabaseReference mDatabaseReference = Constant.fbDatabase.getReference();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();

        DatabaseReference userData = mDatabaseReference.child("users").child(Constant.uid).child("userdata");

        userData.keepSynced(true);

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()) {return;}

                View v = MainActivity.navigationView.getHeaderView(0);

                Constant.nameUser = dataSnapshot.child("name").getValue().toString();
                Constant.mailUser = dataSnapshot.child("email").getValue().toString();

                Log.i("user", Constant.nameUser + " " + Constant.mailUser);

                TextView username = (TextView) v.findViewById(R.id.usernavname);
                TextView mail = (TextView) v.findViewById(R.id.usernavmail);

                String saludo = getString(R.string.hi) + " " + Constant.nameUser;
                username.setText(saludo);
                mail.setText(Constant.mailUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0){
                    if (fragmentStatus == getString(R.string.start)){
                        collToolLay.setTitle(" ");
                        toolbarTitle.setText(R.string.app_name);
                    } else if (fragmentStatus == getString(R.string.favoritos)) {
                        collToolLay.setTitle(" ");
                        toolbarTitle.setText(R.string.favTitle);
                    }
                }else{
                    collToolLay.setTitle(getString(R.string.app_name));
                    toolbarTitle.setText(" ");
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (AppBarLayout) findViewById(R.id.mainappbar);

        nScrollView = (NestedScrollView) findViewById(R.id.nestedscroll);

        collToolLay = (CollapsingToolbarLayout) findViewById(R.id.collaptoolbar);
        collToolLay.setTitle(" ");

        toolbarTitle = (TextView) findViewById(R.id.titletoolbar);
        toolbarTitle.setText(R.string.startTitle);

        fragmentManager.beginTransaction().add(R.id.container, new MainFragment()).commit();

        mMPService = new MediaPlayerMainService(getApplication());

        mReceiver = new PlayReceiver();
        intentFilter= new IntentFilter("BROADCAST_RECEIVER");

        Intent mediaService = new Intent(this, MediaPlayerMainService.class);
        startService(mediaService);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                boolean fragmentTransaction = false;
                Fragment fragment = null;

                switch(item.getItemId()){
                    case R.id.nav_inicio:
                        fragmentStatus = getString(R.string.start);
                        item.setChecked(true);
                        appbar.setExpanded(true);
                        fragmentTransaction = true;
                        fragment = new MainFragment();
                        setSalir(0);
                        break;

                    case R.id.nav_favoritos:
                        fragmentStatus = getString(R.string.favoritos);
                        item.setChecked(true);
                        appbar.setExpanded(true);
                        fragmentTransaction = true;
                        fragment = new FavoritesFragment();
                        setSalir(0);
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

                if (fragmentTransaction){
                    fragmentManager.beginTransaction().replace(R.id.container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    drawer.closeDrawers();
                }

                return false;
            }
        });

        bNView = (BottomNavigationView) findViewById(R.id.main_bottomnavigation);

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
        Constant.uid = user.getUid();

        bNView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.play_pause_menu:
                        if (fragmentStatus == getString(R.string.start)){
                            if (mainListArray.size() < 1){
                                Constant.viewHolder = getCurrentFocus();
                                Snackbar.make(getCurrentFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        } else if (fragmentStatus == getString(R.string.favoritos)) {
                            Constant.viewHolder = getCurrentFocus();
                            if (favoritesArray.size() < 1){
                                Snackbar.make(getCurrentFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        }

                        int i = (int) Math.floor(Math.random() * mainAdapter.getItemCount());
                        if (mMPService.mp == null){
                            Toast.makeText(getApplicationContext(), R.string.shuffle_starts,Toast.LENGTH_SHORT).show();
                            if (Constant.viewHolder != null){
                                Constant.viewHolder.setVisibility(View.GONE);
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

    }

    private void proxim (){
        Toast.makeText(getApplication(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        if (fragmentStatus == "infoFragment"){
            fragmentManager
                    .beginTransaction()
                    .remove(infoFrag)
                    .commit();

            fragmentStatus = " ";

            appbar.setExpanded(true);
            nScrollView.setNestedScrollingEnabled(true);
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

    @Override
    public void onPause(){
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        appbar.setExpanded(false);
        nScrollView.setNestedScrollingEnabled(false);

        Constant.infoArtist = autor;
        Constant.infoSong = song;

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

        nScrollView.setNestedScrollingEnabled(true);
        setSalir(0);

    }

    public static class PlayReceiver extends BroadcastReceiver{

        public PlayReceiver(){}

        MainActivity mainActivity = new MainActivity();

        @Override
        public void onReceive(Context context, Intent intent) {

                mainActivity.playPause(intent.getBooleanExtra("play", false));

        }
    }
}