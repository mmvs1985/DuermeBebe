package com.pmcoder.duermebeb.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import java.util.ArrayList;

import static com.pmcoder.duermebeb.constants.Constant.LOADING;
import static com.pmcoder.duermebeb.views.MainActivity.mMPService;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.PictureViewHolder>{

    private Activity activity;
    private int recurso;
    private ArrayList<ElementoPlaylist> cancion;

    private DatabaseReference databaseReference = Constant.fbDatabase
            .getReference().child("users");
    private DatabaseReference favDatabase = databaseReference
            .child(Constant.uid).child("favorites");
    private StorageReference imgReference = FirebaseStorage.getInstance().getReference();

    public FavoritesAdapter(ArrayList<ElementoPlaylist> cancion, Activity activity, int recurso) {
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
    public void onBindViewHolder(final PictureViewHolder holder, int position) {
        final ElementoPlaylist elementoPlaylist = cancion.get(position);
        holder.songName.setText(elementoPlaylist.getName());
        holder.artistName.setText(elementoPlaylist.getArtist());
        holder.progressBar.setVisibility(View.GONE);
        imgReference.child("music").child("imgsongs").child(elementoPlaylist.getUrlimg())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri == null){return;}
                Glide.with(activity).load(uri).into(holder.imgAlbum);
            }
        });
        holder.songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOADING){return;}
                String song = elementoPlaylist.getUrlsong();
                holder.progressBar.setVisibility(View.VISIBLE);
                Constant.viewHolder = holder.progressBar;
                mMPService.setPlaying(song);
            }
        });
        holder.artistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOADING){return;}
                String song = elementoPlaylist.getUrlsong();

                holder.progressBar.setVisibility(View.VISIBLE);
                Constant.viewHolder = holder.progressBar;
                mMPService.setPlaying(song);

            }
        });
        holder.imgAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (LOADING){return;}
                String song = elementoPlaylist.getUrlsong();

                holder.progressBar.setVisibility(View.VISIBLE);
                Constant.viewHolder = holder.progressBar;
                mMPService.setPlaying(song);


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
                        reloadFragment();

                        int i = 0;

                        while (i < Constant.mainListArray.size()){
                            if (Constant.mainListArray.get(i).getName().equals(elementoPlaylist.getName())){
                                Constant.mainListArray.get(i).setLike("false");
                            }
                            i++;
                        }

                    }
                });
            }
        });

    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAlbum;
        private TextView artistName;
        private TextView songName;
        private CheckBox remove;
        private ProgressBar progressBar;

        public PictureViewHolder(View itemView) {
            super(itemView);

            imgAlbum = (ImageView) itemView.findViewById(R.id.fav_imgrecycler);
            artistName = (TextView) itemView.findViewById(R.id.fav_artistName);
            songName = (TextView) itemView.findViewById(R.id.fav_songName);
            remove = (CheckBox) itemView.findViewById(R.id.remove);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progsongloading);

        }
    }

    @Override
    public int getItemCount() {
        return cancion.size();
    }

    private void reloadFragment(){
        Intent i = new Intent("RELOADFAVORITES");
        try {
            activity.sendBroadcast(i);
        }catch (NullPointerException e){
            Log.e("SendBroadcast: ", "Error "+ e);
        }
    }
}
