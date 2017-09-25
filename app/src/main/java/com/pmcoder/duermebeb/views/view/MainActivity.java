package com.pmcoder.duermebeb.views.view;

import java.io.File;

/**
 * Created by pmcoder on 19/04/17.
 */

public interface MainActivity {
    boolean checkStoragePermission();

    //Manejar la apertura del fragment de información de autor
    void openInfoFragment (String autor, String song);
    void closeInfoFragment ();

    //Cambia el ícono del control de reproducción
    void playPauseButton (Boolean b);

    //Selecciona la url a reproducir
    boolean setPlayingUrl (String song);
    boolean setPlayingLocal (File song);

}
