package com.pmcoder.duermebeb.main.presenter;

import android.graphics.Bitmap;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import com.pmcoder.duermebeb.main.interactor.MainActivityInteractor;
import com.pmcoder.duermebeb.main.interactor.MainActivityInteractorImpl;
import com.pmcoder.duermebeb.main.model.MainActivity;
import com.pmcoder.duermebeb.main.model.MainActivityImpl;
import com.pmcoder.duermebeb.main.repository.MainActivityRepository;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;

import java.io.File;

/**
 * Created by pmcoder on 22/09/17.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private MainActivity mainActivity;
    private MainActivityInteractor interactor;
    private MainActivityRepository repository;

    public MainActivityPresenterImpl (MainActivityImpl activity){

        mainActivity = activity;
        interactor = new MainActivityInteractorImpl(activity);
        repository = new MainActivityRepositoryImpl(activity, this);

    }

    @Override
    public void openFloatFragment(String fragmentName, String autor, String song,
                                 DrawerLayout drawer) {

        interactor.openFloatFragment(fragmentName, autor, song, drawer);
    }

    @Override
    public void closeFloatFragment() {
        interactor.closeFloatFragment();

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

    @Override
    public void setFragmentSelected(int position, String tipo) {
        interactor.setFragmentSelected(position, tipo);
    }

    @Override
    public boolean isOnline() {
        return interactor.isOnline();
    }

    @Override
    public void createDataBaseListeners() {
        repository.createDataBaseListeners();
    }

    @Override
    public void setProfilePicture() {
        mainActivity.setProfilePicture();
    }
}
