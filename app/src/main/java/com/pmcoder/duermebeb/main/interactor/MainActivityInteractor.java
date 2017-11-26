package com.pmcoder.duermebeb.main.interactor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;

import com.pmcoder.duermebeb.main.model.MainActivityImpl;

import java.io.File;

/**
 * Created by pmcoder on 22/09/17.
 */

public interface MainActivityInteractor {

    //Manejar la apertura del fragment de información de autor
    void openFloatFragment (String fragmentName, String autor, String song,
                           DrawerLayout drawer);
    void closeFloatFragment ();

    void stopPlaying();

    //Selecciona la url a reproducir
    boolean setPlayingUrl (String song);

    boolean setPlayingLocal (File song);

    Bitmap cropBitmap(Bitmap original, int height, int width);

    void setTabIcons (TabLayout tabLayout);

    void setIconColor (TabLayout.Tab tab, String color);

    boolean checkStoragePermission();

    void proxim ();

    void onBackPressed();

    void setExit(int exit);

    void onButtonNavigationViewSelected(BottomNavigationView bottomNavigationView);

    void setFragmentSelected(int position, String tipo);

    boolean isOnline();
}
