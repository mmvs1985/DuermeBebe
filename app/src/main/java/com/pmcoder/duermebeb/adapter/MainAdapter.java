package com.pmcoder.duermebeb.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.interfaces.Communicator;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import com.pmcoder.duermebeb.constants.Constant;
import java.util.*;
import static com.pmcoder.duermebeb.constants.Constant.LOADING;
import static com.pmcoder.duermebeb.constants.Constant.artistChannelDB;
import static com.pmcoder.duermebeb.views.MainActivity.mMPService;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.PictureViewHolder>{

    private Activity activity;
    private int recurso;
    private ArrayList<ElementoPlaylist> cancion;
    private DatabaseReference databaseReference = Constant.fbDatabase.getReference().child("users");
    private DatabaseReference userRef = databaseReference.child(Constant.uid).child("favorites");
    private Communicator comm;

    public MainAdapter(ArrayList<ElementoPlaylist> cancion, Activity activity, int recurso) {
        this.cancion = cancion;
        this.activity = activity;
        this.recurso = recurso;
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(recurso, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, final int position) {
        final ElementoPlaylist elementoPlaylist = cancion.get(position);

        if (elementoPlaylist.getIcon() != null) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(elementoPlaylist.getIcon());
            arrayList.add(activity);
            arrayList.add(holder);

            new LoadImage().execute(arrayList);
        }

        holder.songName.setText(elementoPlaylist.getName());
        holder.artistName.setText(elementoPlaylist.getArtist());
        holder.progressBar.setVisibility(View.GONE);
        holder.mainInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOADING){return;}
                String song = elementoPlaylist.getUrlsong();

                holder.progressBar.setVisibility(View.VISIBLE);
                Constant.viewHolder = holder.progressBar;
                mMPService.setPlaying(song);
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
                            Toast.makeText(activity, "Favorito agregado", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    userRef.child(elementoPlaylist.getName())
                            .removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(activity, "Favorito eliminado", Toast.LENGTH_SHORT).show();
                        }
                    });

                    int i = 0;

                    while (i < Constant.mainListArray.size()){
                        if (Constant.mainListArray.get(i).getName().equals(elementoPlaylist.getName())){
                            Constant.mainListArray.get(i).setLike("false");
                        }
                        i++;
                    }
                }

            }
        });

        holder.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm = (Communicator) activity;

                if (!artistChannelDB.containsKey(elementoPlaylist.getArtist())){
                    Toast.makeText(activity, R.string.no_artist_data, Toast.LENGTH_SHORT).show();

                    return;
                }

                Constant.web = artistChannelDB.get(elementoPlaylist.getArtist()).getWeb();
                Constant.youtube = artistChannelDB.get(elementoPlaylist.getArtist()).getYoutube();
                Constant.soundcloud = artistChannelDB.get(elementoPlaylist.getArtist()).getSoundcloud();
                comm.respond(elementoPlaylist.getArtist(), elementoPlaylist.getName());
            }
        });
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAlbum, helpButton;
        private TextView artistName;
        private TextView songName;
        private CheckBox fab;
        private ProgressBar progressBar;
        private LinearLayout mainInfo;

        public PictureViewHolder(View itemView) {
            super(itemView);

            imgAlbum = (ImageView) itemView.findViewById(R.id.imgrecycler);
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
        return cancion.size();
    }

    public static class LoadImage extends AsyncTask<ArrayList, Integer, byte[]> {

        private Activity act;
        private PictureViewHolder holder;

        @Override
        protected byte[] doInBackground(ArrayList... params) {
            byte img[] = null;
            act = (Activity) params[0].get(1);
            holder = (PictureViewHolder) params[0].get(2);

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
