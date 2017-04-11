package com.pmcoder.duermebeb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pmcoder.duermebeb.views.MainActivity;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.adapter.MainAdapter;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.models.ElementoPlaylist;

public class MainFragment extends Fragment {

    public static MainAdapter mainAdapter;


    public MainFragment() {
        // Required empty public constructor
    }

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
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

        songList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Constant.dataBaseMainArray.clear();

                for (DataSnapshot songlist: dataSnapshot.getChildren()) {

                    String songName = songlist.child("name").getValue().toString();
                    String artist = songlist.child("artist").getValue().toString();
                    String urlSong = songlist.child("urlsong").getValue().toString();
                    String urlImg = songlist.child("urlimg").getValue().toString();


                    Constant.dataBaseMainArray.add(new ElementoPlaylist(artist, songName, urlSong, "false", urlImg));
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
