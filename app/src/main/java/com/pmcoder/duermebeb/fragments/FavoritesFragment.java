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
import com.pmcoder.duermebeb.golbal.GlobalVariables;
import com.pmcoder.duermebeb.models.ElementoPlaylist;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    private static RecyclerView recyclerView;
    private static FavoritesAdapter favoritesAdapter;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    DatabaseReference databaseReference = GlobalVariables.fbDatabase.getReference().child("users");
    DatabaseReference favPlaylist = databaseReference.child(GlobalVariables.uid).child("favorites");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_favorites, container, false);

        broadcastReceiver = new ReloadFragment();
        intentFilter = new IntentFilter("RELOADFAVORITES");

        recyclerView = (RecyclerView) view.findViewById(R.id.fav_recyclerview);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        favoritesAdapter = new FavoritesAdapter(GlobalVariables.favoritesArray, getActivity(), R.layout.favouritescardview);
        recyclerView.setAdapter(favoritesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        favPlaylist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GlobalVariables.databaseFavArray.clear();

                for (DataSnapshot songlist: dataSnapshot.getChildren()){

                    String name = songlist.child("name").getValue() != null?
                            songlist.child("name").getValue().toString():null;
                    String artist =songlist.child("artist").getValue() != null?
                            songlist.child("artist").getValue().toString():null;
                    String urlSong = songlist.child("urlsong").getValue() != null?
                            songlist.child("urlsong").getValue().toString():null;
                    String icon = songlist.child("icon").getValue() != null?
                            songlist.child("icon").getValue().toString():null;

                    GlobalVariables.databaseFavArray
                            .add(new ElementoPlaylist(artist, name, urlSong, icon));
                }
                if (!GlobalVariables.favoritesArray.equals(GlobalVariables.databaseFavArray)){
                    GlobalVariables.favoritesArray.clear();
                    GlobalVariables.favoritesArray = GlobalVariables.databaseFavArray;

                    Log.i("fav", "Hasta aquí todo listo");
                    favoritesAdapter = new FavoritesAdapter(GlobalVariables.favoritesArray, getActivity(), R.layout.favouritescardview);
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
