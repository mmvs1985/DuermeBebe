package com.pmcoder.duermebeb.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.adapter.FavoritesAdapter;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.models.ElementoPlaylist;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    private static RecyclerView recyclerView;
    private static FavoritesAdapter favoritesAdapter;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    DatabaseReference databaseReference = Constant.fbDatabase.getReference().child("users");
    DatabaseReference favPlaylist = databaseReference.child(Constant.uid).child("favorites");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favorites, container, false);

        broadcastReceiver = new ReloadFragment();
        intentFilter = new IntentFilter("RELOADFAVORITES");

        recyclerView = (RecyclerView) view.findViewById(R.id.fav_recyclerview);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        favoritesAdapter = new FavoritesAdapter(Constant.favoritesArray, getActivity(), R.layout.favouritescardview);
        recyclerView.setAdapter(favoritesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        favPlaylist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Constant.databaseFavArray.clear();

                for (DataSnapshot songlist: dataSnapshot.getChildren()){
                    String name = songlist.child("name").getValue().toString();
                    String artist = songlist.child("artist").getValue().toString();
                    String urlSong = songlist.child("urlsong").getValue().toString();
                    String urlImg = songlist.child("urlimg").getValue().toString();

                    Constant.databaseFavArray
                            .add(new ElementoPlaylist(artist, name, urlSong, urlImg));
                }
                if (!Constant.favoritesArray.equals(Constant.databaseFavArray)){
                    Constant.favoritesArray.clear();
                    Constant.favoritesArray = Constant.databaseFavArray;

                    Log.i("fav", "Hasta aquí todo listo");
                    favoritesAdapter = new FavoritesAdapter(Constant.favoritesArray, getActivity(), R.layout.favouritescardview);
                    recyclerView.setAdapter(favoritesAdapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ContextWrapper wrap = new ContextWrapper(getActivity());
        wrap.registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onStop() {
        super.onStop();

        ContextWrapper wrap = new ContextWrapper(getActivity());
        wrap.unregisterReceiver(broadcastReceiver);
    }

    public static class ReloadFragment extends BroadcastReceiver {

        public ReloadFragment() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            recyclerView.setAdapter(favoritesAdapter);

        }
    }

}
