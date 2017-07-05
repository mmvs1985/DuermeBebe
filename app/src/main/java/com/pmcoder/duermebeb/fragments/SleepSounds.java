package com.pmcoder.duermebeb.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.adapter.SoundsAdapter;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class SleepSounds extends Fragment {

    private DatabaseReference soundsDB = GlobalVariables.fbDatabase.getReference()
            .child("main-sounds");
    private RecyclerView recyclerView;
    private SoundsAdapter adapter;
    public static File rootPath = new File(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "DuermeBebeMusic");

    public SleepSounds() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_sounds, container, false);

        if (!rootPath.exists()){
            try {
                rootPath.mkdirs();
            } catch (Exception e ) {
                Log.i("root", rootPath.canRead() ? "Si" : "No");
            }
        }

        ArrayList<ElementoPlaylist> arrayList = new ArrayList<>();
        arrayList.add(new ElementoPlaylist(null, "Cargando Elementos", null, null));
        GlobalVariables.soundsArray = arrayList;

        recyclerView = (RecyclerView) view.findViewById(R.id.soundsrecycler);

        GridLayoutManager grid = new GridLayoutManager(getContext(), 2, GridLayout.VERTICAL, false);
        recyclerView.setLayoutManager(grid);

        adapter = new SoundsAdapter(getActivity(), GlobalVariables.soundsArray, R.layout.sounds_cardview);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        soundsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;

                for (DataSnapshot d: dataSnapshot.getChildren()){
                    try {
                        String name = d.getKey();
                        String url = d.getValue().toString();
                        GlobalVariables.databaseSoundsArray
                                .add(new ElementoPlaylist(null, name, url, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!GlobalVariables.databaseSoundsArray.equals(GlobalVariables.soundsArray)){
                    GlobalVariables.soundsArray.clear();
                    GlobalVariables.soundsArray = GlobalVariables.databaseSoundsArray;

                    adapter = new SoundsAdapter(getActivity(),
                            GlobalVariables.soundsArray,
                            R.layout.sounds_cardview);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
