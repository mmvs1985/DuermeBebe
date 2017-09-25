package com.pmcoder.duermebeb.views.interactor;

import android.graphics.Bitmap;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import java.io.File;

/**
 * Created by pmcoder on 22/09/17.
 */

public interface MainActivityInteractor {

    //Manejar la apertura del fragment de información de autor
    void openInfoFragment (String autor, String song, FragmentManager manager,
                           DrawerLayout drawer);
    void closeInfoFragment ();

    //Cambia el ícono del control de reproducción
    void playPauseButton (Boolean b, BottomNavigationView bottomNavigationView);

    void stopPlaying();

    //Selecciona la url a reproducir
    boolean setPlayingUrl (String song);

    boolean setPlayingLocal (File song);

    Bitmap cropBitmap(Bitmap original, int height, int width);

    Fragment loadServiceOnFragment (Fragment fragment);

    void setTabIcons (TabLayout tabLayout);

    void setIconColor (TabLayout.Tab tab, String color);

    boolean checkStoragePermission();

    void proxim ();

    void onBackPressed();

    void setExit(int exit);

    void onButtonNavigationViewSelected(BottomNavigationView bottomNavigationView);
}
