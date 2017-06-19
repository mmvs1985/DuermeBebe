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
import com.pmcoder.duermebeb.golbal.GlobalVariables;
import com.pmcoder.duermebeb.models.ElementoPlaylist;

public class MainFragment extends Fragment {

    public static MainAdapter mainAdapter;


    public MainFragment() {
        // Required empty public constructor
    }

    private DatabaseReference mDatabaseReference = GlobalVariables.fbDatabase.getReference();
    private DatabaseReference songList = mDatabaseReference.child("defSongs");
    private DatabaseReference favorites = mDatabaseReference
            .child("users").child(GlobalVariables.uid).child("favorites");
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        GlobalVariables.mainListArray.add(new ElementoPlaylist("", "Cargando elementos", "", ""));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        mainAdapter = new MainAdapter(GlobalVariables.mainListArray, getActivity(), R.layout.cardview);
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

                com.pmcoder.duermebeb.golbal.GlobalVariables.dataBaseMainArray.clear();

                for (DataSnapshot songlist: dataSnapshot.getChildren()) {

                    String songName = songlist.child("name").getValue() != null?
                            songlist.child("name").getValue().toString():null;
                    String artist = songlist.child("artist").getValue() != null?
                            songlist.child("artist").getValue().toString(): null;
                    String urlSong = songlist.child("urlsong").getValue() != null?
                            songlist.child("urlsong").getValue().toString():null;
                    String icon = songlist.child("icon").getValue() != null?
                            songlist.child("icon").getValue().toString():null;


                    GlobalVariables.dataBaseMainArray.add(new ElementoPlaylist(artist, songName, urlSong, "false", icon));
                }

                if (!GlobalVariables.mainListArray.equals(GlobalVariables.dataBaseMainArray)){
                    GlobalVariables.mainListArray.clear();
                    GlobalVariables.mainListArray = GlobalVariables.dataBaseMainArray;

                    mainAdapter = new MainAdapter(GlobalVariables.mainListArray, getActivity(), R.layout.cardview);
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

                    while (i < GlobalVariables.mainListArray.size()){
                        if (GlobalVariables.mainListArray.get(i).getName().equals(name)){
                            GlobalVariables.mainListArray.get(i).setLike(like);
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
