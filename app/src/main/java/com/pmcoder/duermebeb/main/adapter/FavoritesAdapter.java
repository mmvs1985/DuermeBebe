package com.pmcoder.duermebeb.main.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.main.model.MainActivity;
import com.pmcoder.duermebeb.basicModels.ElementoPlaylist;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;
import java.util.ArrayList;

import static com.pmcoder.duermebeb.main.service.MediaPlayerMainService.LOADING;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.PictureViewHolder>{

    private Activity activity;
    private int recurso;
    private ArrayList<ElementoPlaylist> cancion;

    private DatabaseReference favDatabase;
    private MainActivity comm;

    public FavoritesAdapter(ArrayList<ElementoPlaylist> cancion,
                            Activity activity, int recurso, String uid) {
        this.cancion = cancion;
        this.activity = activity;
        this.recurso = recurso;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("users");
        favDatabase = databaseReference
                .child(uid).child("favorites");
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(recurso, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, int position) {
        final ElementoPlaylist elementoPlaylist = cancion.get(position);
        holder.songName.setText(elementoPlaylist.getName());
        holder.artistName.setText(elementoPlaylist.getArtist());
        holder.progressBar.setVisibility(View.GONE);

        if (elementoPlaylist.getIcon() != null) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(elementoPlaylist.getIcon());
            arrayList.add(activity);
            arrayList.add(holder);

            new LoadImage().execute(arrayList);
        }

        holder.songData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comm == null){
                    comm = (MainActivity) activity;
                }
                if (LOADING){return;}
                String song = elementoPlaylist.getUrlsong();
                holder.progressBar.setVisibility(View.VISIBLE);
                comm.setProgressBar(holder.progressBar);
                comm.setPlayingUrl(song);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rm = elementoPlaylist.getName();

                favDatabase.child(rm).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(activity, "Favorito eliminado", Toast.LENGTH_SHORT).show();

                        int i = 0;

                        while (i < MainActivityRepositoryImpl.mainListArray.size()){
                            if (MainActivityRepositoryImpl.mainListArray.
                                    get(i)
                                    .getName()
                                    .equals(elementoPlaylist
                                            .getName())){

                                MainActivityRepositoryImpl.mainListArray.get(i).setLike("false");
                            }
                            i++;
                        }

                    }
                });
            }
        });

        holder.help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comm = (MainActivity) activity;

                if (!MainActivityRepositoryImpl.artistChannelDB.
                        containsKey(elementoPlaylist.getArtist())){

                    Toast.makeText(activity, R.string.no_artist_data, Toast.LENGTH_SHORT).show();

                    return;
                }

                GlobalVariables.web = MainActivityRepositoryImpl.artistChannelDB
                        .get(elementoPlaylist.getArtist()).getWeb();
                GlobalVariables.youtube = MainActivityRepositoryImpl.artistChannelDB
                        .get(elementoPlaylist.getArtist()).getYoutube();
                GlobalVariables.soundcloud = MainActivityRepositoryImpl.artistChannelDB
                        .get(elementoPlaylist.getArtist()).getSoundcloud();

                comm.openFloatFragment(
                        elementoPlaylist.getArtist(), elementoPlaylist.getName());
            }
        });

    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAlbum, help;
        private TextView artistName;
        private TextView songName;
        private CheckBox remove;
        private ProgressBar progressBar;
        private LinearLayout songData;

        public PictureViewHolder(View itemView) {
            super(itemView);

            imgAlbum = (ImageView) itemView.findViewById(R.id.fav_imgrecycler);
            help = (ImageView) itemView.findViewById(R.id.help);
            artistName = (TextView) itemView.findViewById(R.id.fav_artistName);
            songName = (TextView) itemView.findViewById(R.id.fav_songName);
            remove = (CheckBox) itemView.findViewById(R.id.remove);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progsongloading);
            songData = (LinearLayout) itemView.findViewById(R.id.favcardinfo);

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
