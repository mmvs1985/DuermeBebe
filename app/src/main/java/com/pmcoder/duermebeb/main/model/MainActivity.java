package com.pmcoder.duermebeb.main.model;

import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.File;

/**
 * Created by pmcoder on 19/04/17.
 */

public interface MainActivity {
    boolean checkStoragePermission();

    //Manejar la apertura del fragment de información de autor
    void openFloatFragment(String autor, String song);
    void closeFloatFragment ();

    //Cambia el ícono del control de reproducción
    void playPauseButton (Boolean b);

    //Selecciona la url a reproducir
    boolean setPlayingUrl (String song);
    boolean setPlayingLocal (File song);

    void setProfilePicture();

    boolean isOnline();

    void setViews(ImageView playButton, ImageView pauseButton, ProgressBar progressBar);

    ProgressBar getProgressBar();
    void setProgressBar(ProgressBar progressBar);
}
