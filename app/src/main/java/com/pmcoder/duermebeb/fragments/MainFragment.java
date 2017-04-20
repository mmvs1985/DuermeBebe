package com.pmcoder.duermebeb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.*;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.firebase.database.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.adapter.MainAdapter;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.models.ElementoPlaylist;

public class MainFragment extends Fragment {

    public static MainAdapter mainAdapter;


    public MainFragment() {
        // Required empty public constructor
    }

    private DatabaseReference mDatabaseReference = Constant.fbDatabase.getReference();
    private DatabaseReference songList = mDatabaseReference.child("defSongs");
    private DatabaseReference favorites = mDatabaseReference.child("users").child(Constant.uid).child("favorites");
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        mainAdapter = new MainAdapter(Constant.mainListArray, getActivity(), R.layout.cardview);
        recyclerView.setAdapter(mainAdapter);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        songList.keepSynced(true);
        favorites.keepSynced(true);

        songList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Constant.dataBaseMainArray.clear();

                for (DataSnapshot songlist: dataSnapshot.getChildren()) {

                    if (songlist.child("name").getValue() == null) return;
                    String songName = songlist.child("name").getValue().toString();
                    if (songlist.child("artist").getValue() == null) return;
                    String artist = songlist.child("artist").getValue().toString();
                    if (songlist.child("urlsong").getValue() == null) return;
                    String urlSong = songlist.child("urlsong").getValue().toString();
                    if (songlist.child("urlimg").getValue() == null) return;
                    String urlImg = songlist.child("urlimg").getValue().toString();
                    if (songlist.child("web").getValue() == null) return;
                    String web = songlist.child("web").getValue().toString();
                    if (songlist.child("soundcloud").getValue() == null) return;
                    String soundCloud = songlist.child("soundcloud").getValue().toString();
                    if (songlist.child("youtube").getValue() == null) return;
                    String youtube = songlist.child("youtube").getValue().toString();


                    Constant.dataBaseMainArray.add(new ElementoPlaylist(artist, songName, urlSong, "false", urlImg, soundCloud, youtube, web));
                }

                if (!Constant.mainListArray.equals(Constant.dataBaseMainArray)){
                    Constant.mainListArray.clear();
                    Constant.mainListArray = Constant.dataBaseMainArray;

                    mainAdapter = new MainAdapter(Constant.mainListArray, getActivity(), R.layout.cardview);
                    recyclerView.setAdapter(mainAdapter);
                }
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
                for(DataSnapshot favs: dataSnapshot.getChildren()){
                    String like = favs.child("like").getValue().toString();
                    String name = favs.child("name").getValue().toString();

                    int i = 0;

                    while (i < Constant.mainListArray.size()){
                        if (Constant.mainListArray.get(i).getName().equals(name)){
                            Constant.mainListArray.get(i).setLike(like);
                        }
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
