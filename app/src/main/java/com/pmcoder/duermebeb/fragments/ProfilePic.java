package com.pmcoder.duermebeb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.golbal.GlobalVariables;

public class ProfilePic extends Fragment {
    public ProfilePic() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile_pic, container, false);

        byte image[] = Base64.decode(GlobalVariables.profileImgBase64, Base64.DEFAULT);
        ImageView profilePicture = (ImageView) view.findViewById(R.id.imageProfile);
        Glide.with(getActivity()).load(image).into(profilePicture);

        return view;

    }
}
