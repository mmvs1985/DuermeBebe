package com.pmcoder.duermebeb.views.view;

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
import com.pmcoder.duermebeb.R;
import com.pmcoder.duermebeb.fragments.ProfilePic;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.models.FileManager;
import com.pmcoder.duermebeb.image.ImageResizer;
import com.pmcoder.duermebeb.image.ImageUtil;
import java.io.File;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePictureActivity extends AppCompatActivity implements View.OnClickListener{

    private FileManager manager = new FileManager(this);
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragmentProfile = null;
    private CircleImageView profilePic;
    final static int REQUEST_CODE_CAPTURE = 1;
    private DatabaseReference profPicDb = GlobalVariables.fbDatabase
            .getReference()
            .child("users")
            .child(GlobalVariables.uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        ImageView cam = (ImageView) findViewById(R.id.cam);
        profilePic = (CircleImageView) findViewById(R.id.profilepic);
        TextView username = (TextView) findViewById(R.id.username);
        TextView usermail = (TextView) findViewById(R.id.usermail);

        if (GlobalVariables.profileImgBase64 != null && !GlobalVariables.profileImgBase64.equals("")){

            profilePic.setImageBitmap(ImageUtil.convert(GlobalVariables.profileImgBase64));
        } else {
            Toast.makeText(getApplicationContext()
                    , R.string.notprofilepic
                    , Toast.LENGTH_SHORT)
                    .show();
        }

        if (GlobalVariables.nameUser != null && !GlobalVariables.nameUser.equals("")){
            username.setText(GlobalVariables.nameUser);
            usermail.setText(GlobalVariables.mailUser);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        ImageResizer imageResizer = new ImageResizer(profilePic);
        imageResizer.setTARGET_IMAGE_VIEW_HEIGHT(profilePic.getHeight());
        imageResizer.setTARGET_IMAGE_VIEW_WIDTH(profilePic.getWidth());

        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
            File photo = new File(manager.getPhotoPath());
            imageResizer.execute(photo);

            if (ImageResizer.fireBaseBitmap == null) {
                while (ImageResizer.fireBaseBitmap == null){
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
