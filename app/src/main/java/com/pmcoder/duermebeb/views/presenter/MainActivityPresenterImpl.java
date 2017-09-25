package com.pmcoder.duermebeb.views.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import com.pmcoder.duermebeb.views.interactor.MainActivityInteractor;
import com.pmcoder.duermebeb.views.interactor.MainActivityInteractorImpl;
import java.io.File;

/**
 * Created by pmcoder on 22/09/17.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private MainActivityInteractor interactor;

    public MainActivityPresenterImpl (Activity activity){

        interactor = new MainActivityInteractorImpl(activity);

    }

    @Override
    public void openInfoFragment(String autor, String song, FragmentManager manager,
                                 DrawerLayout drawer) {

        interactor.openInfoFragment(autor, song, manager, drawer);
    }

    @Override
    public void closeInfoFragment() {
        interactor.closeInfoFragment();

    }

    @Override
    public void playPauseButton(Boolean b, BottomNavigationView bottomNavigationView) {
        interactor.playPauseButton(b, bottomNavigationView);
    }

    @Override
    public void stopPlaying() {
        interactor.stopPlaying();
    }

    @Override
    public boolean setPlayingUrl(String song) {

        return interactor.setPlayingUrl(song);
    }

    @Override
    public boolean setPlayingLocal(File song) {

        return interactor.setPlayingLocal(song);
    }

    @Override
    public Bitmap cropBitmap(Bitmap original, int height, int width) {
        return interactor.cropBitmap(original, height, width);
    }

    @Override
    public Fragment loadServiceOnFragment(Fragment fragment) {

        return interactor.loadServiceOnFragment(fragment);
    }

    @Override
    public void setTabIcons(TabLayout tabLayout) {
        interactor.setTabIcons(tabLayout);
    }

    @Override
    public void setIconColor(TabLayout.Tab tab, String color) {
        interactor.setIconColor(tab, color);
    }

    @Override
    public boolean checkStoragePermission() {
        return interactor.checkStoragePermission();
    }

    @Override
    public void proxim() {
        interactor.proxim();
    }

    @Override
    public void onBackPressed() {
        interactor.onBackPressed();
    }

    @Override
    public void setExit(int exit) {
        interactor.setExit(exit);
    }

    @Override
    public void onButtonNavigationViewItemSelected(BottomNavigationView bottomNavigationView) {
       interactor.onButtonNavigationViewSelected(bottomNavigationView);
    }
}
