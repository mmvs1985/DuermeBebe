package com.pmcoder.duermebeb.basicModels;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {

    private String photoPath;
    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public File createProfileImage () {

        String timeStamp = new SimpleDateFormat("ddMMyymmss", new Locale("es", "uy"))
                .format(new Date());
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

}
