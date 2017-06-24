package com.pmcoder.duermebeb.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.interfaces.Communicator;

import static android.content.Intent.ACTION_VIEW;

public class InfoFragment extends Fragment implements View.OnClickListener{

    public InfoFragment() {
        // Required empty public constructor
    }

    private TextView artist, song, web, soundcloud, youtube;
    private CoordinatorLayout exit;
    private Communicator comm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        artist = (TextView) v.findViewById(R.id.artistcardinfo);
        artist.setText(GlobalVariables.infoArtist);
        song = (TextView) v.findViewById(R.id.songcardinfo);
        song.setText(GlobalVariables.infoSong);
        web = (TextView) v.findViewById(R.id.urlweb);
        web.setText(GlobalVariables.web);
        web.setOnClickListener(this);
        soundcloud = (TextView) v.findViewById(R.id.urlsoundcloud);
        soundcloud.setText(GlobalVariables.soundcloud);
        soundcloud.setOnClickListener(this);
        youtube = (TextView) v.findViewById(R.id.urlyoutube);
        youtube.setText(GlobalVariables.youtube);
        youtube.setOnClickListener(this);

        exit = (CoordinatorLayout) v.findViewById(R.id.exit);
        exit.setOnClickListener(this);

        return v;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.exit:
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commit();

                comm = (Communicator)getActivity();
                comm.closeNotificationFragment();

                break;

            case R.id.urlweb:
                openUrl(GlobalVariables.web);
                break;

            case R.id.urlsoundcloud:
                openUrl(GlobalVariables.soundcloud);
                break;

            case R.id.urlyoutube:
                openUrl(GlobalVariables.youtube);
                break;
        }

    }

    private void openUrl(String link){

        startActivity(new Intent(ACTION_VIEW, Uri.parse(link)));
    }
}
