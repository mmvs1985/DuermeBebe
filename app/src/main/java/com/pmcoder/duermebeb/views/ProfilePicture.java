package com.pmcoder.duermebeb.views;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.constants.Constant;
import com.pmcoder.duermebeb.fragments.ProfilePic;
import com.pmcoder.duermebeb.models.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePicture extends AppCompatActivity implements View.OnClickListener{

    private FileManager manager = new FileManager(this);
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragmentProfile = null;
    private CircleImageView profilePic;
    private TextView username, usermail;
    private byte aByte[];
    final static int REQUEST_CODE_CAPTURE = 1;
    private DatabaseReference profPicDb = Constant.fbDatabase
            .getReference()
            .child("users")
            .child(Constant.uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        ImageView cam = (ImageView) findViewById(R.id.cam);
        profilePic = (CircleImageView) findViewById(R.id.profilepic);
        username = (TextView) findViewById(R.id.username);
        usermail = (TextView) findViewById(R.id.usermail);

        if (Constant.profileImgBase64 != null && !Constant.profileImgBase64.equals("")){
            byte photo[] = Base64.decode(Constant.profileImgBase64, Base64.DEFAULT);
            Glide.with(getApplicationContext())
                    .load(photo)
                    .into(profilePic);
        } else {
            Toast.makeText(getApplicationContext()
                    , R.string.notprofilepic
                    , Toast.LENGTH_SHORT)
                    .show();
        }

        if (Constant.nameUser != null && !Constant.nameUser.equals("")){
            username.setText(Constant.nameUser);
            usermail.setText(Constant.mailUser);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.profileTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cam.setOnClickListener(this);
        profilePic.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        if (fragmentProfile != null){
            fragmentManager.beginTransaction().remove(fragmentProfile).commit();
            fragmentProfile = null;
            
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cam:

                takePicture();
                break;
            case R.id.profilepic:

                if (Constant.profileImgBase64 != null && !Constant.profileImgBase64.equals("")){
                    fragmentProfile = new ProfilePic();
                    fragmentManager
                            .beginTransaction()
                            .add(R.id.fullimage, fragmentProfile)
                            .commit();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getText(R.string.notprofilepic),
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }

    }

    private void takePicture () {

        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePic.resolveActivity(getApplicationContext().getPackageManager()) != null) {

            File img = null;

            try {
                img = manager.createProfileImage();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (img != null) {

                Uri imgUri = FileProvider
                        .getUriForFile(getApplicationContext(), "com.pmcoder.duermebeb", img);

                takePic.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(takePic, REQUEST_CODE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {

            Glide.with(getApplicationContext()).load(manager.getPhotoPath()).into(profilePic);

            imgToBase64();
        }
    }

    private void imgToBase64 () {

        File imageJpg = new File(manager.getPhotoPath());
        try {
            FileInputStream fiStream = new FileInputStream(imageJpg);
            aByte = new byte[(int)imageJpg.length()];
            fiStream.read(aByte);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Constant.profileImgBase64 = Base64.encodeToString(aByte, Base64.DEFAULT);

        Log.i("base", Constant.profileImgBase64);

        new FileManager.ImgToFirebase().execute(profPicDb);
    }

}
