package com.pmcoder.duermebeb.global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import com.google.firebase.database.FirebaseDatabase;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Runtime;

public class GlobalVariables {

    public static Boolean PLAYING = false,
            LOADING = false;
    public static Boolean persistence = false;
    public static String mailUser, nameUser, uid = "", infoArtist, infoSong, youtube, soundcloud, web;
    public static int EXIT = 0;
    public static String profileImgBase64;

    public static FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    public static ArrayList<ElementoPlaylist> mainListArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> dataBaseMainArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> favoritesArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> databaseFavArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> soundsArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> databaseSoundsArray = new ArrayList<>();

    public static Map<String, ElementoPlaylist> artistChannelDB = new HashMap<>();
    public static Map<String, ElementoPlaylist> artistSyncDB = new HashMap<>();

    public static View progressBar = null, playButton = null, pauseButton = null;

    public static Boolean isOnline(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
