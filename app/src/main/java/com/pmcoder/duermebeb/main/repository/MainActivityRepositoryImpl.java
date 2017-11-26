package com.pmcoder.duermebeb.main.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.*;
import com.pmcoder.duermebeb.basicModels.ElementoPlaylist;
import com.pmcoder.duermebeb.main.adapter.FavoritesAdapter;
import com.pmcoder.duermebeb.main.presenter.MainActivityPresenter;
import com.pmcoder.duermebeb.main.presenter.MainActivityPresenterImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pmcoder on 15/11/17.
 */

public class MainActivityRepositoryImpl implements MainActivityRepository {

    private MainActivityPresenter presenter;
    private DatabaseReference mArtistChannel;
    private DatabaseReference userData;
    private DatabaseReference songList;
    private DatabaseReference favorites;
    private String profilePicRoute = null;
    private String profileImgBase64 = null;
    private String YOUTUBE = null;
    private String SOUNDCLOUD = null;
    private String WEB = null;

    private ArrayList<ElementoPlaylist> dataBaseMainArray;
    public static ArrayList<ElementoPlaylist> mainListArray;
    public static ArrayList<ElementoPlaylist> favoritesArray;
    public static ArrayList<ElementoPlaylist> databaseFavArray;
    public static ArrayList<ElementoPlaylist> soundsArray;
    public static ArrayList<ElementoPlaylist> databaseSoundsArray;
    public static Map<String, ElementoPlaylist> artistChannelDB;
    public static Map<String, ElementoPlaylist> artistSyncDB;
    public static boolean mainListLoaded = false;
    public static boolean favoritesListLoaded = false;

    public MainActivityRepositoryImpl (Activity context, MainActivityPresenterImpl presenter){

        this.presenter = presenter;

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SharedPreferences preferences = context.getSharedPreferences("User-Data", Context.MODE_PRIVATE);

        profilePicRoute = "profilepic";
        YOUTUBE = "youtube";
        SOUNDCLOUD = "soundcloud";
        WEB = "web";

        songList = mDatabaseReference.child("defSongs");
        favorites = mDatabaseReference
                .child("users")
                .child(preferences.getString("UID", ""))
                .child("favorites");
        mArtistChannel = mDatabaseReference.child("artist-channel");
        userData = mDatabaseReference
                .child("users")
                .child(preferences.getString("UID", ""))
                .child("userdata");

        favoritesArray = new ArrayList<>();
        databaseFavArray = new ArrayList<>();
        soundsArray = new ArrayList<>();
        databaseSoundsArray = new ArrayList<>();
        dataBaseMainArray = new ArrayList<>();
        mainListArray = new ArrayList<>();
        mainListArray.add(
                new ElementoPlaylist(null,
                        context.getString(com.pmcoder.duermebeb.R.string.loadingElements),
                        null,
                        null));
        artistChannelDB = new HashMap<>();
        artistSyncDB = new HashMap<>();
    }


    @Override
    public void createDataBaseListeners() {

        userData.keepSynced(true);
        songList.keepSynced(true);
        favorites.keepSynced(true);

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()) {return;}

                if (dataSnapshot.child(profilePicRoute).exists()
                        && dataSnapshot.child(profilePicRoute).getValue() != null){

                    profileImgBase64 = dataSnapshot
                            .child(profilePicRoute).getValue().toString();
                }

                if (profileImgBase64 != null &&
                        !profileImgBase64.equals("")){

                    presenter.setProfilePicture();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mArtistChannel.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildren() == null) return;

                for (DataSnapshot artist: dataSnapshot.getChildren()){

                    if (artist.child(YOUTUBE).getValue().toString() == null) return;
                    String youtube = artist.child(YOUTUBE).getValue().toString();
                    if (artist.child(SOUNDCLOUD).getValue().toString() == null) return;
                    String soundCloud = artist.child(SOUNDCLOUD).getValue().toString();
                    if (artist.child(WEB).getValue().toString() == null) return;
                    String web= artist.child(WEB).getValue().toString();

                    artistSyncDB.put(artist.getKey(),
                            new ElementoPlaylist(soundCloud, youtube, web));

                }
                if(!artistChannelDB.equals(artistSyncDB)){

                    artistChannelDB = artistSyncDB;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        songList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataBaseMainArray != null) {

                    dataBaseMainArray.clear();
                }
                else {
                    dataBaseMainArray = new ArrayList<>();
                }

                for (DataSnapshot songlist: dataSnapshot.getChildren()) {

                    String songName = songlist.child("name").getValue() != null?
                            songlist.child("name").getValue().toString():null;
                    String artist = songlist.child("artist").getValue() != null?
                            songlist.child("artist").getValue().toString(): null;
                    String urlSong = songlist.child("urlsong").getValue() != null?
                            songlist.child("urlsong").getValue().toString():null;
                    String icon = songlist.child("icon").getValue() != null?
                            songlist.child("icon").getValue().toString():null;

                    dataBaseMainArray
                            .add(new ElementoPlaylist(artist, songName, urlSong, "false", icon));
                }

                if (!mainListArray
                        .equals(dataBaseMainArray)){

                    mainListArray.clear();
                    mainListArray = dataBaseMainArray;
                    mainListLoaded = true;
                }

                dataBaseMainArray = null;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        favorites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null){
                    return;
                }

                if (MainActivityRepositoryImpl.databaseFavArray != null) {
                    MainActivityRepositoryImpl.databaseFavArray.clear();
                }
                else {
                    MainActivityRepositoryImpl.databaseFavArray = new ArrayList<>();
                }

                for (DataSnapshot songlist: dataSnapshot.getChildren()){

                    String like = songlist.child("like").getValue().toString();
                    String name = songlist.child("name").getValue() != null?
                            songlist.child("name").getValue().toString():null;
                    String artist =songlist.child("artist").getValue() != null?
                            songlist.child("artist").getValue().toString():null;
                    String urlSong = songlist.child("urlsong").getValue() != null?
                            songlist.child("urlsong").getValue().toString():null;
                    String icon = songlist.child("icon").getValue() != null?
                            songlist.child("icon").getValue().toString():null;

                    for (int i = 0; i < mainListArray.size(); i++){

                        if (mainListArray.get(i).getName().equals(name)){

                            mainListArray.get(i).setLike(like);
                        }
                    }

                    MainActivityRepositoryImpl.databaseFavArray
                            .add(new ElementoPlaylist(artist, name, urlSong, icon));
                }

                if (!MainActivityRepositoryImpl.favoritesArray
                        .equals(MainActivityRepositoryImpl.databaseFavArray)){
                    MainActivityRepositoryImpl.favoritesArray.clear();
                    MainActivityRepositoryImpl.favoritesArray =
                            MainActivityRepositoryImpl.databaseFavArray;

                    favoritesListLoaded = true;

                    Log.i("fav", "Hasta aquí todo listo");
                    MainActivityRepositoryImpl.databaseFavArray = null;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public String getProfilePicB64() {
        return profileImgBase64;
    }
}
