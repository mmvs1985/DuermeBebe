package com.pmcoder.duermebeb.views;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.fragments.FavoritesFragment;
import com.pmcoder.duermebeb.fragments.MainFragment;
import com.pmcoder.duermebeb.services.MediaPlayerMainService;
import static com.pmcoder.duermebeb.R.drawable.circled_play_48;
import static com.pmcoder.duermebeb.R.drawable.pause_48;
import static com.pmcoder.duermebeb.R.drawable.shuffle_48;
import static com.pmcoder.duermebeb.R.string.navigation_drawer_close;
import static com.pmcoder.duermebeb.R.string.navigation_drawer_open;
import static com.pmcoder.duermebeb.fragments.MainFragment.mainAdapter;
import static com.pmcoder.duermebeb.services.MediaPlayerMainService.*;
import static com.pmcoder.duermebeb.constants.Constant.*;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    public static NavigationView navigationView;
    public static MediaPlayerMainService mMPService;
    private static BottomNavigationView bNView;
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private CollapsingToolbarLayout collToolLay;
    private TextView toolbarTitle;
    private String fragmentStatus;
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();

        DatabaseReference userData = mDatabaseReference.child("users").child(Constant.uid).child("userdata");

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.mainappbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0){
                    if (fragmentStatus == "inicio"){
                        collToolLay.setTitle(" ");
                        toolbarTitle.setText(R.string.app_name);
                    } else if (fragmentStatus == "favoritos") {
                        collToolLay.setTitle(" ");
                        toolbarTitle.setText(R.string.favTitle);
                    }
                }else{
                    collToolLay.setTitle("Duerme bebé");
                    toolbarTitle.setText(" ");
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collToolLay = (CollapsingToolbarLayout) findViewById(R.id.collaptoolbar);
        collToolLay.setTitle(" ");

        toolbarTitle = (TextView) findViewById(R.id.titletoolbar);
        toolbarTitle.setText(R.string.startTitle);

        fragmentManager.beginTransaction().add(R.id.container, new MainFragment()).commit();

        mMPService = new MediaPlayerMainService(getApplication());

        mReceiver = new PlayReceiver();
        intentFilter= new IntentFilter("AJUSTAR_REPRODUCTOR");

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
                        fragmentStatus = "inicio";
                        item.setChecked(true);
                        fragmentTransaction = true;
                        fragment = new MainFragment();
                        setSalir(0);
                        break;

                    case R.id.nav_favoritos:
                        fragmentStatus = "favoritos";
                        item.setChecked(true);
                        fragmentTransaction = true;
                        fragment = new FavoritesFragment();
                        setSalir(0);
                        break;

                    case R.id.nav_share:
                        setSalir(0);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Descarga la mejor aplicación para dormir a tu bebé: " +
                                "https://play.google.com/store/apps/details?id=com.pmcoder.duermebeb");
                        startActivity(Intent.createChooser(intent, "Compartir con"));

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
                        int i = (int) Math.floor(Math.random() * mainAdapter.getItemCount());
                        if (mMPService.mp == null){
                            Toast.makeText(getApplicationContext(),"Modo aleatorio iniciado",Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplication(), "Proximamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        EXIT +=1;
        if (EXIT == 2){
            System.exit(0);
        }else {
            Toast.makeText(getApplicationContext(), "Presione otra vez para salir", Toast.LENGTH_SHORT)
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

    public static class PlayReceiver extends BroadcastReceiver{

        public PlayReceiver(){}

        MainActivity mainActivity = new MainActivity();

        @Override
        public void onReceive(Context context, Intent intent) {

            mainActivity.playPause(intent.getBooleanExtra("play", false));

        }
    }
}
