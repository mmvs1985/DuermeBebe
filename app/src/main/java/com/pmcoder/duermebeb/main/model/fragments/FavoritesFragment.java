package com.pmcoder.duermebeb.main.model.fragments;

import android.app.Activity;
import android.os.*;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.LinearLayout;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.basicModels.ElementoPlaylist;
import com.pmcoder.duermebeb.main.adapter.FavoritesAdapter;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;
import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements View.OnClickListener {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    private static RecyclerView recyclerView;
    private static FavoritesAdapter favoritesAdapter;
    private static String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_favorites, container, false);

        if (getArguments() != null){

            uid = getArguments().getString("uid");
        }

        CoordinatorLayout exit = (CoordinatorLayout) view.findViewById(R.id.fav_exit);
        exit.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.fav_recyclerview);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        favoritesAdapter =
                new FavoritesAdapter(
                        MainActivityRepositoryImpl.favoritesArray,
                        getActivity(), R.layout.favouritescardview, uid);
        recyclerView.setAdapter(favoritesAdapter);

        new AsyncFFAdapter().execute(getActivity());

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.fav_exit:

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commit();

            break;
        }
    }

    private static class AsyncFFAdapter extends AsyncTask<Activity,
            Integer, Activity> {

        @Override
        protected Activity doInBackground(Activity context[]) {

            while (!MainActivityRepositoryImpl.favoritesListLoaded){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            MainActivityRepositoryImpl.favoritesListLoaded = false;

            return context[0];
        }

        @Override
        protected void onPostExecute(Activity activity) {
            super.onPostExecute(activity);

            favoritesAdapter = new FavoritesAdapter
                    (MainActivityRepositoryImpl.favoritesArray,
                            activity, R.layout.favouritescardview, uid);

            recyclerView.setAdapter(favoritesAdapter);
            new AsyncFFAdapter().execute(activity);
        }
    }

}
