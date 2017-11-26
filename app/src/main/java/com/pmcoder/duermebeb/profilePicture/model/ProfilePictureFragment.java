package com.pmcoder.duermebeb.profilePicture.model;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.basicModels.FileManager;
import com.pmcoder.duermebeb.basicModels.ImageResizer;
import com.pmcoder.duermebeb.basicModels.ImageUtil;
import com.pmcoder.duermebeb.profilePicture.model.fragments.ProfilePicFragment;

import java.io.File;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePictureFragment extends AppCompatActivity implements View.OnClickListener{

    private FileManager manager = new FileManager(this);
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragmentProfile = null;
    private CircleImageView profilePic;
    private String uid = "";
    private String userName = "";
    private String userEmail = "";
    final static int REQUEST_CODE_CAPTURE = 1;
    private DatabaseReference profPicDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        uid = savedInstanceState.getString("uid");
        userName = savedInstanceState.getString("userName");
        userEmail = savedInstanceState.getString("userEmail");

        profPicDb = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(uid);

        ImageView cam = findViewById(R.id.cam);
        profilePic = findViewById(R.id.profilepic);
        TextView username = findViewById(R.id.username);
        TextView usermail = findViewById(R.id.usermail);

        if (GlobalVariables.profileImgBase64 != null && !GlobalVariables.profileImgBase64.equals("")){

            profilePic.setImageBitmap(ImageUtil.convert(GlobalVariables.profileImgBase64));
        } else {
            Toast.makeText(getApplicationContext()
                    , R.string.notprofilepic
                    , Toast.LENGTH_SHORT)
                    .show();
        }

        if (userName != null && !userName.equals("")){
            username.setText(userName);
            usermail.setText(userEmail);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
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

            return;
        }
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cam:

                takePicture();
                break;
            case R.id.profilepic:

                if (GlobalVariables.profileImgBase64 != null && !GlobalVariables.profileImgBase64.equals("")){
                    fragmentProfile = new ProfilePicFragment();
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

        ImageResizer imageResizer = new ImageResizer(profilePic);
        imageResizer.setTARGET_IMAGE_VIEW_HEIGHT(profilePic.getHeight());
        imageResizer.setTARGET_IMAGE_VIEW_WIDTH(profilePic.getWidth());

        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
            File photo = new File(manager.getPhotoPath());

           // Glide.with(this).load(manager.getPhotoPath()).into(profilePic);

            imageResizer.execute(photo);

            if (ImageResizer.fireBaseBitmap == null) {
                while (ImageResizer.fireBaseBitmap == null){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (ImageResizer.fireBaseBitmap != null)
            {
                GlobalVariables.profileImgBase64 = ImageUtil.convert(ImageResizer.fireBaseBitmap);
                Log.i("pic", ImageUtil.convert(ImageResizer.fireBaseBitmap));
                new ImgToFirebase().execute(profPicDb);
            }

        }
    }

    public static class ImgToFirebase extends AsyncTask<DatabaseReference, Integer, Void> {


        @Override
        protected Void doInBackground(DatabaseReference... params) {

            params[0].child("userdata")
                    .child("profilepic")
                    .setValue(GlobalVariables.profileImgBase64);

            return null;
        }
    }

}
