package com.pmcoder.duermebeb.main.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.main.model.MainActivity;
import com.pmcoder.duermebeb.basicModels.ElementoPlaylist;
import com.pmcoder.duermebeb.main.model.MainActivityImpl;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;
import java.util.*;
import static com.pmcoder.duermebeb.main.service.MediaPlayerMainService.*;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.PictureViewHolder>{

    private MainActivityImpl activity;
    private int recurso;
    public static int adapterSize;
    private ArrayList<ElementoPlaylist> cancion;

    private DatabaseReference userRef;
    private MainActivity comm;

    public MainAdapter
            (ArrayList<ElementoPlaylist> cancion, Activity activity, int recurso, String uid) {
        this.cancion = cancion;
        this.activity = (MainActivityImpl) activity;
        this.recurso = recurso;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        userRef = databaseReference
                .child(uid).child("favorites");
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(recurso, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, final int position) {
        final ElementoPlaylist elementoPlaylist = cancion.get(position);

        if (getItemCount() == 1){
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        if (elementoPlaylist.getIcon() != null) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(elementoPlaylist.getIcon());
            arrayList.add(holder);

            new LoadImage(activity).execute(arrayList);
        }

        holder.songName.setText(elementoPlaylist.getName());
        holder.artistName.setText(elementoPlaylist.getArtist());
        holder.progressBar.setVisibility(View.GONE);

        holder.mainInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comm == null){
                    comm =  activity;
                }
                if (LOADING){return;}
                String song = elementoPlaylist.getUrlsong();

                if (comm.setPlayingUrl(song)) {

                    holder.progressBar.setVisibility(View.VISIBLE);
                    if (!LOADING){

                        holder.progressBar.setVisibility(View.GONE);
                    }
                }

                if (PLAYING){
                    holder.playButton.setVisibility(View.GONE);
                    holder.pauseButton.setVisibility(View.VISIBLE);
                }
                else {

                    holder.playButton.setVisibility(View.VISIBLE);
                    holder.pauseButton.setVisibility(View.GONE);
                }

                comm.setViews(holder.playButton, holder.pauseButton, holder.progressBar);
            }
        });
        holder.fab.setChecked(elementoPlaylist.isLikeState());
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.fab.isChecked()){
                    Map<String, String> mymap = new HashMap<>();
                    mymap.put("name", elementoPlaylist.getName());
                    mymap.put("artist", elementoPlaylist.getArtist());
                    mymap.put("urlsong", elementoPlaylist.getUrlsong());
                    mymap.put("icon", elementoPlaylist.getIcon());
                    mymap.put("like", "true");

                    userRef.child(elementoPlaylist.getName()).setValue(mymap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(activity, R.string.favAdded, Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    userRef.child(elementoPlaylist.getName())
                            .removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(activity, R.string.favDeleted, Toast.LENGTH_SHORT).show();
                        }
                    });

                    int i = 0;

                    while (i < MainActivityRepositoryImpl.mainListArray.size()){

                        if (MainActivityRepositoryImpl.mainListArray.get(i).getName()
                                .equals(elementoPlaylist.getName())){

                            MainActivityRepositoryImpl.mainListArray.get(i).setLike("false");
                        }
                        i++;
                    }
                }

            }
        });

        holder.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm = activity;

                if (!MainActivityRepositoryImpl.artistChannelDB
                        .containsKey(elementoPlaylist.getArtist())){

                    Toast.makeText(activity, R.string.no_artist_data, Toast.LENGTH_SHORT).show();

                    return;
                }

                GlobalVariables.web =
                        MainActivityRepositoryImpl.artistChannelDB.get(
                                elementoPlaylist.getArtist()).getWeb();
                GlobalVariables.youtube = MainActivityRepositoryImpl.artistChannelDB.get(
                        elementoPlaylist.getArtist()).getYoutube();
                GlobalVariables.soundcloud = MainActivityRepositoryImpl.artistChannelDB.get(
                        elementoPlaylist.getArtist()).getSoundcloud();
                comm.openFloatFragment(
                        elementoPlaylist.getArtist(), elementoPlaylist.getName());
            }
        });
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAlbum, helpButton, playButton, pauseButton;
        private TextView artistName;
        private TextView songName;
        private CheckBox fab;
        private ProgressBar progressBar;
        private LinearLayout mainInfo;

        public PictureViewHolder(View itemView) {
            super(itemView);

            imgAlbum = (ImageView) itemView.findViewById(R.id.imgrecycler);
            playButton = (ImageView) itemView.findViewById(R.id.imgplay);
            pauseButton = (ImageView) itemView.findViewById(R.id.imgpause);
            artistName = (TextView) itemView.findViewById(R.id.artistName);
            songName = (TextView) itemView.findViewById(R.id.songName);
            fab = (CheckBox) itemView.findViewById(R.id.fab);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progsongloading);
            mainInfo = (LinearLayout) itemView.findViewById(R.id.maincardinfo);
            helpButton = (ImageView) itemView.findViewById(R.id.help);


        }
    }

    @Override
    public int getItemCount() {

        adapterSize = cancion.size();

        return cancion.size();
    }

    public static class LoadImage extends AsyncTask<ArrayList, Integer, byte[]> {

        private Activity act;
        private PictureViewHolder holder;

        public LoadImage(Activity activity){
            act = activity;
        }

        @Override
        protected byte[] doInBackground(ArrayList... params) {
            byte img[] = null;
            holder = (PictureViewHolder) params[0].get(1);

            if (params[0].get(0) != null) {
                img = Base64.decode((String) params[0].get(0), Base64.DEFAULT);
            }
            return img;
        }

        @Override
        protected void onPostExecute(byte[] s) {
            super.onPostExecute(s);

            Glide.with(act)
                    .load(s)
                    .into(holder.imgAlbum);
        }
    }
}
