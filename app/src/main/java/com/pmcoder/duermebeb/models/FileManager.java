package com.pmcoder.duermebeb.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.firebase.database.DatabaseReference;
import com.pmcoder.duermebeb.constants.Constant;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

    private String photoPath;
    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public File createProfileImage () {

        String timeStamp = new SimpleDateFormat("ddmmyy_hhmmss").format(new Date());
        String filename = "IMG_" + timeStamp;
        File img = null;

        File storeDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {

            img = File.createTempFile(filename, ".jpg", storeDir);
            photoPath = img.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;

    }

    public String getPhotoPath(){

        return photoPath;
    }

    public static class ImgToFirebase extends AsyncTask <DatabaseReference, Integer, String>{


        @Override
        protected String doInBackground(DatabaseReference... params) {

            params[0].child("userdata")
                    .child("profilepic")
                    .setValue(Constant.profileImgBase64);

            return null;
        }
    }
}
