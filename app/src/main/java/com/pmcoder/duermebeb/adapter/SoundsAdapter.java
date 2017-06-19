package com.pmcoder.duermebeb.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.models.ElementoPlaylist;
import java.util.ArrayList;

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.PictureViewHolder> {

    private Activity activity;
    private ArrayList<ElementoPlaylist> array;
    private int cardview;

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
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        ElementoPlaylist elementoPlaylist = array.get(position);
        holder.title.setText(elementoPlaylist.getName());

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public PictureViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.soundtitle);
        }
    }
}
