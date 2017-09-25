package com.pmcoder.duermebeb.views.interactor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.Toast;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.fragments.*;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.services.MediaPlayerMainService;
import java.io.File;
import static com.pmcoder.duermebeb.R.drawable.*;
import static com.pmcoder.duermebeb.fragments.MainFragment.mainAdapter;
import static com.pmcoder.duermebeb.global.GlobalVariables.*;
import static com.pmcoder.duermebeb.services.MediaPlayerMainService.*;

/**
 * Created by pmcoder on 22/09/17.
 */

public class MainActivityInteractorImpl implements MainActivityInteractor {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private MediaPlayerMainService mediaPlayerMainService = null;
    private Activity activity;
    private InfoFragment infoFragment;
    private FragmentManager fragmentManager;
    private int tabIcons[] = {R.drawable.traklist_48, R.drawable.sounds_48};

    public static String fragmentStatus;

    public MainActivityInteractorImpl(Activity activity) {
        this.activity = activity;
        this.fragmentStatus = activity.getResources().getString(R.string.start);
    }

    @Override
    public void playPauseButton(Boolean b, BottomNavigationView bottomNavigationView) {

        if (b) {
            bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(pause_48);
            if (GlobalVariables.playButton != null) {
                GlobalVariables.playButton.setVisibility(View.GONE);
                GlobalVariables.pauseButton.setVisibility(View.VISIBLE);
            }
        } else {
            if (GlobalVariables.playButton != null) {
                bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(circled_play_48);
                GlobalVariables.playButton.setVisibility(View.VISIBLE);
                GlobalVariables.pauseButton.setVisibility(View.GONE);
            }
        }
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
    public Fragment loadServiceOnFragment(Fragment fragment) {
        fragment.setReenterTransition(mediaPlayerMainService);
        return fragment;
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

                        if (MainActivityInteractorImpl.fragmentStatus.equals(activity.getString(R.string.start))) {
                            if (mainListArray.size() < 2) {
                                Snackbar.make(activity.getCurrentFocus().findFocus(), R.string.list_empty,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            } else {
                                playShuffleUrl(bottomNavigationView);
                            }
                        } else if (MainActivityInteractorImpl.fragmentStatus.equals(activity.getString(R.string.sounds))) {
                            if (soundsArray.size() < 2) {
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
    public void proxim() {
        Toast.makeText(activity, R.string.coming_soon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        if (fragmentStatus != null && fragmentStatus.equals("infoFragment")) {
            fragmentManager
                    .beginTransaction()
                    .remove(infoFragment)
                    .commit();

            fragmentStatus = " ";
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
    public void openInfoFragment(String autor, String song, FragmentManager manager,
                                 DrawerLayout drawer) {

        GlobalVariables.infoArtist = autor;
        GlobalVariables.infoSong = song;

        fragmentManager = manager;
        manager.beginTransaction()
                .add(R.id.container, infoFragment = new InfoFragment())
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();

        drawer.closeDrawers();
    }

    @Override
    public void closeInfoFragment() {
        setExit(0);
    }

    @Override
    public void setExit(int exit) {
        GlobalVariables.EXIT = exit;
    }

    public void playShuffleUrl(BottomNavigationView bottomNavigationView) {

        if (mediaPlayerMainService == null){
            mediaPlayerMainService = new MediaPlayerMainService(activity);
        }

        int i = (int) Math.floor(Math.random() * mainAdapter.getItemCount());

        if (mediaPlayerMainService.mp == null) {
            Toast
                    .makeText(activity,
                            R.string.shuffle_starts,
                            Toast.LENGTH_SHORT).show();
            if (GlobalVariables.progressBar != null) {
                GlobalVariables.progressBar.setVisibility(View.GONE);
            }
            setPlayingUrl(mainListArray.get(i).getUrlsong());
            bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
        } else {
            setPlayingUrl(songNow);
        }
    }

    public void playShuffleLocal(BottomNavigationView bottomNavigationView) {
        int i = (int) Math.floor(Math.random() * soundsArray.size());

        if (mediaPlayerMainService.mp == null) {
            Toast
                    .makeText(activity,
                            R.string.shuffle_starts,
                            Toast.LENGTH_SHORT).show();
            if (GlobalVariables.progressBar != null) {
                GlobalVariables.progressBar.setVisibility(View.GONE);
            }
            setPlayingLocal(new File(SleepSounds.rootPath.getAbsolutePath() +
                    "/" + soundsArray.get(i).getUrlsong()));
            bottomNavigationView.getMenu().findItem(R.id.play_pause_menu).setIcon(shuffle_48);
        } else {
            setPlayingLocal(new File(SleepSounds.rootPath.getAbsolutePath() +
                    "/" + soundNow));
        }
    }
}
