package com.pmcoder.duermebeb.constants;

import android.view.View;
import com.google.firebase.database.FirebaseDatabase;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import java.util.ArrayList;

public class Constant {

    public static Boolean PLAYING, LOADING = false;
    public static Boolean persistence = false;
    public static String mailUser, nameUser, uid = "";
    public static int EXIT = 0;
    public static FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    public static ArrayList<ElementoPlaylist> mainListArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> dataBaseMainArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> favoritesArray = new ArrayList<>();
    public static ArrayList<ElementoPlaylist> databaseFavArray = new ArrayList<>();
    public static View viewHolder;

    public static Boolean funcionaInternet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            return (val == 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
