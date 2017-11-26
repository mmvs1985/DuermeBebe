package com.pmcoder.duermebeb.main.interactor;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.main.adapter.*;
import com.pmcoder.duermebeb.main.model.MainActivityImpl;
import com.pmcoder.duermebeb.main.model.fragments.FavoritesFragment;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;
import com.pmcoder.duermebeb.main.service.MediaPlayerMainService;
import com.pmcoder.duermebeb.main.model.fragments.*;
import java.io.File;
import static com.pmcoder.duermebeb.R.drawable.*;
import static com.pmcoder.duermebeb.main.service.MediaPlayerMainService.*;

/**
 * Creado por pmcoder el 22/09/17.
 */

public class MainActivityInteractorImpl implements MainActivityInteractor {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private MediaPlayerMainService mediaPlayerMainService = null;
    private InfoFragment infoFragment;
    private FavoritesFragment favoritesFragment;
    private MainActivityImpl activity;
    private FragmentManager fragmentManager;
    private int tabIcons[] = {R.drawable.traklist_48, R.drawable.sounds_48};
    private int EXIT = 0;
    private String currentFragment;
    private SharedPreferences preferences;

    public MainActivityInteractorImpl(MainActivityImpl activity) {

        this.activity = activity;
        this.currentFragment = activity.getResources().getString(R.string.start);
        mediaPlayerMainService = new MediaPlayerMainService(activity);
        infoFragment = null;
        favoritesFragment = null;
        fragmentManager = activity.getSupportFragmentManager();
        preferences = activity.getSharedPreferences("User-Data", Context.MODE_PRIVATE);
    }

    @Override
    public void stopPlaying() {

        if (mediaPlayerMainService != null) {
            mediaPlayerMainService.stop();
        }
    }

    @Override
    public boolean setPlayingUrl(String song) {
        if (mediaPlayerMainService == null) {
            mediaPlayerMainService = new MediaPlayerMainService(activity);
        }

        return this.mediaPlayerMainService.setPlayingUrl(song);
    }

    @Override
    public boolean setPlayingLocal(File song) {
        if (this.mediaPlayerMainService == null) {
            this.mediaPlayerMainService = new MediaPlayerMainService(activity);
        }

        return mediaPlayerMainService.setPlayingLocal(song);
    }

    @Override
    public Bitmap cropBitmap(Bitmap original, int height, int width) {

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

    @Override
    public void setTabIcons(TabLayout tabLayout) {
        for (int i = 0; i < 2; i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    @Override
    public void setIconColor(TabLayout.Tab tab, String color) {
        tab.getIcon()
                .setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public boolean checkStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            int hasStorageWritePermission =activity.
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasStorageWritePermission != PackageManager.PERMISSION_GRANTED){
                activity.
                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION);

                return false;

            }
        }

        return true;
    }

    @Override
    public void onButtonNavigationViewSelected(final BottomNavigationView bottomNavigationView) {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.play_pause_menu:

                        if (currentFragment.equals(activity.getString(R.string.start))) {
                            if (MainActivityRepositoryImpl.mainListArray.size() < 2) {
                                Snackbar.make(activity.getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            } else {
                                playShuffleUrl(bottomNavigationView);
                            }
                        } else if (currentFragment.equals(activity.getString(R.string.sounds))) {
                            if (MainActivityRepositoryImpl.soundsArray.size() < 2) {
                                Snackbar.make(activity.getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            } else {
                                playShuffleLocal(bottomNavigationView);
                            }
                        }

                        break;

                    case R.id.stop_menu:
                        stopPlaying();

                        break;

                }
                return false;
            }
        });
    }

    @Override
    public void setFragmentSelected(int position, String tipo) {

        if (tipo.equals("ViewPager")){
            if (position == 1){
                currentFragment = activity.getString(R.string.sounds);
            } else {
                currentFragment = activity.getString(R.string.start);
            }
        } else if (tipo.equals("TabLayout")) {

            if (position == 1) {

                currentFragment = activity.getString(R.string.start);
            } else {
                currentFragment = activity.getString(R.string.sounds);
            }
        }

        System.out.println(currentFragment);
    }

    @Override
    public boolean isOnline() {

        ConnectivityManager connectivity = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivity != null;
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    @Override
    public void proxim() {
        Toast.makeText(activity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        if (infoFragment != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(infoFragment)
                    .commit();
            infoFragment = null;
            return;
        } else if (favoritesFragment != null){
            fragmentManager
                    .beginTransaction()
                    .remove(favoritesFragment)
                    .commit();
            favoritesFragment = null;
            return;
        }

        EXIT += 1;
        if (EXIT == 2) {
            activity.finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            EXIT = 0;
            activity.startActivity(intent);

        } else {
            Toast.makeText(activity, R.string.press_again_2_exit, Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void openFloatFragment(String fragmentName,
                                  String autor, String song, DrawerLayout drawer) {

        if (fragmentName.equals("INFO")) {

            openInfoFragment(drawer, autor, song);
        }
        else if(fragmentName.equals("FAV")){
            openFavoritesFragment(drawer);
        }
    }

    @Override
    public void closeFloatFragment() {
        setExit(0);
    }

    @Override
    public void setExit(int exit) {
        EXIT = exit;
    }

    public void playShuffleUrl(BottomNavigationView bottomNavigationView) {

        if (mediaPlayerMainService == null){
            mediaPlayerMainService = new MediaPlayerMainService(activity);
        }

        int i = (int)
                Math.floor(Math.random() * MainAdapter.adapterSize);

        if (mediaPlayerMainService.mp == null) {
            Toast
                    .makeText(activity,
                            R.string.shuffle_starts,
                            Toast.LENGTH_SHORT).show();
            ProgressBar progressBar = activity.getProgressBar();
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            setPlayingUrl(MainActivityRepositoryImpl.mainListArray.get(i).getUrlsong());
            bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
        } else {
            setPlayingUrl(songNow);
        }
    }

    public void playShuffleLocal(BottomNavigationView bottomNavigationView) {
        int i = (int) Math.floor(Math.random() * SoundsAdapter.adapterSize);

        if (mediaPlayerMainService.mp == null) {
            Toast
                    .makeText(activity,
                            R.string.shuffle_starts,
                            Toast.LENGTH_SHORT).show();

            ProgressBar progressBar = activity.getProgressBar();
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            setPlayingLocal(new File(SleepSounds.rootPath.getAbsolutePath() +
                    "/" + MainActivityRepositoryImpl.soundsArray.get(i).getUrlsong()));
            bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
        } else {
            setPlayingLocal(new File(SleepSounds.rootPath.getAbsolutePath() +
                    "/" + soundNow));
        }
    }

    private void openFavoritesFragment(DrawerLayout drawer) {

        favoritesFragment = new FavoritesFragment();
        Bundle mfavBundle = new Bundle();
        mfavBundle.putString("uid", preferences.getString("UID", ""));
        favoritesFragment.setArguments(mfavBundle);

        fragmentManager.beginTransaction()
                .add(R.id.container, favoritesFragment)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();

        drawer.closeDrawers();
    }

    private void openInfoFragment(DrawerLayout drawer, String autor, String song){

        infoFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("autor", autor);
        bundle.putString("song", song);
        infoFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .add(R.id.container, infoFragment)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();

        drawer.closeDrawers();
    }
}
