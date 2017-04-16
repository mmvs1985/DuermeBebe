package com.pmcoder.duermebeb.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import com.pmcoder.duermebeb.constants.Constant;
import java.util.*;

import static com.pmcoder.duermebeb.constants.Constant.LOADING;
import static com.pmcoder.duermebeb.views.MainActivity.mMPService;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.PictureViewHolder>{

    private Activity activity;
    private int recurso;
    private ArrayList<ElementoPlaylist> cancion;
    private DatabaseReference databaseReference = Constant.fbDatabase.getReference().child("users");
    private DatabaseReference userRef = databaseReference.child(Constant.uid).child("favorites");
    private StorageReference imgReference = FirebaseStorage.getInstance().getReference();

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
        holder.songName.setText(elementoPlaylist.getName());
        holder.artistName.setText(elementoPlaylist.getArtist());
        imgReference.child("music").child("imgsongs").child(elementoPlaylist.getUrlimg())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(activity).load(uri).into(holder.imgAlbum);
            }
        });
        holder.progressBar.setVisibility(View.GONE);
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
        holder.fab.setChecked(elementoPlaylist.isLikeState());
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.fab.isChecked()){
                    Map<String, String> mymap = new HashMap<>();
                    mymap.put("name", elementoPlaylist.getName());
                    mymap.put("artist", elementoPlaylist.getArtist());
                    mymap.put("urlsong", elementoPlaylist.getUrlsong());
                    mymap.put("urlimg", elementoPlaylist.getUrlimg());
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
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAlbum;
        private TextView artistName;
        private TextView songName;
        private CheckBox fab;
        private ProgressBar progressBar;

        public PictureViewHolder(View itemView) {
            super(itemView);

            imgAlbum = (ImageView) itemView.findViewById(R.id.imgrecycler);
            artistName = (TextView) itemView.findViewById(R.id.artistName);
            songName = (TextView) itemView.findViewById(R.id.songName);
            fab = (CheckBox) itemView.findViewById(R.id.fab);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progsongloading);


        }
    }

    @Override
    public int getItemCount() {
        return cancion.size();
    }
}
