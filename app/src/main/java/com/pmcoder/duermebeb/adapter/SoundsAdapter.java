package com.pmcoder.duermebeb.adapter;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.fragments.SleepSounds;
import com.pmcoder.duermebeb.interfaces.Communicator;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import java.io.File;
import java.util.ArrayList;

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.PictureViewHolder> {

    private Activity activity;
    private ArrayList<ElementoPlaylist> array;
    private int cardview;
    private FirebaseStorage fBStorage = FirebaseStorage.getInstance();
    private StorageReference sReference = fBStorage.
            getReferenceFromUrl("gs://duerme-bebe.appspot.com")
            .child("sounds");
    private Communicator comm;

    public SoundsAdapter(Activity activity, ArrayList<ElementoPlaylist> array, int cardview) {
        this.activity = activity;
        this.array = array;
        this.cardview = cardview;
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(cardview, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, int position) {
        final ElementoPlaylist elementoPlaylist = array.get(position);
        final File sound = new File(SleepSounds.rootPath.getAbsolutePath() +
                "/" + elementoPlaylist.getUrlsong());

        holder.title.setText(elementoPlaylist.getName());
        if (getItemCount() == 1){
            holder.download.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }
        if (sound.exists()){
            holder.download.setVisibility(View.INVISIBLE);
            holder.playButton.setVisibility(View.VISIBLE);
        }
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elementoPlaylist.getUrlsong() == null) return;

                if (!sound.exists()) {
                    holder.download.setVisibility(View.INVISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);

                    sReference.child(elementoPlaylist.getUrlsong())
                            .getFile(sound)
                            .addOnSuccessListener(
                                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(activity,
                                    elementoPlaylist.getName() + " descargado", Toast.LENGTH_SHORT)
                                    .show();
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.playButton.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity,
                                    "No se pudo descargar archivo: " + elementoPlaylist.getName(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.download.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comm == null){
                    comm = (Communicator) activity;
                }

                comm.setPlayingLocal(sound);
            }
        });


    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView download;
        public ImageView playButton;
        public ProgressBar progressBar;


        public PictureViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.soundtitle);
            download = (ImageView) itemView.findViewById(R.id.downloadit);
            playButton = (ImageView) itemView.findViewById(R.id.playbutton);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
        }
    }
}
