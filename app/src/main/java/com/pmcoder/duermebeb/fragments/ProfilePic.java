package com.pmcoder.duermebeb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.image.ImageUtil;

public class ProfilePic extends Fragment {

    public ProfilePic() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile_pic, container, false);

        ImageView profilePicture = (ImageView) view.findViewById(R.id.imageProfile);
        profilePicture.setImageBitmap(ImageUtil.convert(GlobalVariables.profileImgBase64));

        return view;

    }
}
