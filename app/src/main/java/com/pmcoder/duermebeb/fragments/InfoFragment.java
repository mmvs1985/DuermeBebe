package com.pmcoder.duermebeb.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;
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
        artist.setText(Constant.infoArtist);
        song = (TextView) v.findViewById(R.id.songcardinfo);
        song.setText(Constant.infoSong);
        web = (TextView) v.findViewById(R.id.urlweb);
        web.setText(Constant.web);
        web.setOnClickListener(this);
        soundcloud = (TextView) v.findViewById(R.id.urlsoundcloud);
        soundcloud.setText(Constant.soundcloud);
        soundcloud.setOnClickListener(this);
        youtube = (TextView) v.findViewById(R.id.urlyoutube);
        youtube.setText(Constant.youtube);
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
                openUrl(Constant.web);
                break;

            case R.id.urlsoundcloud:
                openUrl(Constant.soundcloud);
                break;

            case R.id.urlyoutube:
                openUrl(Constant.youtube);
                break;
        }

    }

    private void openUrl(String link){

        startActivity(new Intent(ACTION_VIEW, Uri.parse(link)));
    }
}
