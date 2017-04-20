package com.pmcoder.duermebeb.fragments;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import com.google.firebase.database.*;
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

                    if (songlist.child("name").getValue() == null) return;
                    String name = songlist.child("name").getValue().toString();
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

                    Constant.databaseFavArray
                            .add(new ElementoPlaylist(artist, name, urlSong, urlImg, soundCloud, youtube, web));
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
