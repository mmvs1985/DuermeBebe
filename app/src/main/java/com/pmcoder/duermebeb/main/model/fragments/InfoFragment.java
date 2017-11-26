package com.pmcoder.duermebeb.main.model.fragments;

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
import com.pmcoder.duermebeb.main.model.MainActivity;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;

import static android.content.Intent.ACTION_VIEW;

public class InfoFragment extends Fragment implements View.OnClickListener{

    private String autor;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        autor = "Desconocido";
        String song = "Desconocido";
        if (getArguments() != null) {

            autor = getArguments().getString("autor");
            song = getArguments().getString("song");
        }

        TextView artist = (TextView) v.findViewById(R.id.artistcardinfo);
        artist.setText(autor);
        TextView songTV = (TextView) v.findViewById(R.id.songcardinfo);
        songTV.setText(song);
        TextView web = (TextView) v.findViewById(R.id.urlweb);
        web.setText(GlobalVariables.web);
        web.setOnClickListener(this);
        TextView soundcloud = (TextView) v.findViewById(R.id.urlsoundcloud);
        soundcloud.setText(GlobalVariables.soundcloud);
        soundcloud.setOnClickListener(this);
        TextView youtube = (TextView) v.findViewById(R.id.urlyoutube);
        youtube.setText(GlobalVariables.youtube);
        youtube.setOnClickListener(this);

        CoordinatorLayout exit = (CoordinatorLayout) v.findViewById(R.id.exit);
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

                MainActivity comm = (MainActivity) getActivity();
                comm.closeFloatFragment();

                break;

            case R.id.urlweb:
                openUrl(MainActivityRepositoryImpl.artistChannelDB.get(autor).getWeb());
                break;

            case R.id.urlsoundcloud:
                openUrl(MainActivityRepositoryImpl.artistChannelDB.get(autor).getSoundcloud());
                break;

            case R.id.urlyoutube:
                openUrl(MainActivityRepositoryImpl.artistChannelDB.get(autor).getYoutube());
                break;
        }

    }



    private void openUrl(String link){

        startActivity(new Intent(ACTION_VIEW, Uri.parse(link)));
    }
}
