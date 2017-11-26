package com.pmcoder.duermebeb.main.model.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.basicModels.ElementoPlaylist;
import com.pmcoder.duermebeb.main.adapter.MainAdapter;
import com.pmcoder.duermebeb.main.repository.MainActivityRepositoryImpl;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    private static MainAdapter mainAdapter;
    private static RecyclerView recyclerView;
    private static String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        if (getArguments() != null) {

            uid = getArguments().getString("uid");
            Log.i("UID: ", uid);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        mainAdapter = new MainAdapter
                (MainActivityRepositoryImpl.mainListArray, getActivity(), R.layout.homecardview, uid);
        recyclerView.setAdapter(mainAdapter);

        new AsyncMFAdapter().execute(getActivity());

        return view;

    }

    private static class AsyncMFAdapter extends AsyncTask<Activity,
            Integer, Activity> {

        @Override
        protected Activity doInBackground(Activity context[]) {


            while (!MainActivityRepositoryImpl.mainListLoaded){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            MainActivityRepositoryImpl.mainListLoaded = false;

            return context[0];
        }

        @Override
        protected void onPostExecute(Activity activity) {
            super.onPostExecute(activity);

            mainAdapter = new MainAdapter
                    (MainActivityRepositoryImpl.mainListArray,
                            activity, R.layout.homecardview, uid);
            recyclerView.setAdapter(mainAdapter);
        }
    }
}
