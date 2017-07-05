package com.pmcoder.duermebeb.interfaces;

import java.io.File;

/**
 * Created by pmcoder on 19/04/17.
 */

public interface Communicator {
    //Manejar la apertura del fragment de información de autor
    void openInfoFragment (String autor, String song);
    void closeInfoFragment ();

    //Cambia el ícono del control de reproducción
    void playPauseButton (Boolean b);

    //Selecciona la url a reproducir
    void setPlayingUrl (String song);
    void setPlayingLocal (File song);
}
