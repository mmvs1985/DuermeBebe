package com.pmcoder.duermebeb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.adapter.SoundsAdapter;
import com.pmcoder.duermebeb.models.ElementoPlaylist;

import java.util.ArrayList;


public class SleepSounds extends Fragment {

    public SleepSounds() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_sounds, container, false);

        ArrayList<ElementoPlaylist> arrayList = new ArrayList<>();
        arrayList.add(new ElementoPlaylist("Prueba 1", ""));
        arrayList.add(new ElementoPlaylist("Prueba 2", ""));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.soundsrecycler);

        GridLayoutManager grid = new GridLayoutManager(getContext(), 2, GridLayout.VERTICAL, false);
        recyclerView.setLayoutManager(grid);

        SoundsAdapter adapter = new SoundsAdapter(getActivity(), arrayList, R.layout.sounds_cardview);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
