package com.pmcoder.duermebeb.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by pmcoder on 23/06/17.
 */

public class ImageResizer extends AsyncTask<File, Void, Bitmap>{

    private int TARGET_IMAGE_VIEW_WIDTH = 200;
    private int TARGET_IMAGE_VIEW_HEIGHT = 200;
    public static Bitmap fireBaseBitmap = null;
    WeakReference<ImageView> imageViewReference;

    public ImageResizer(ImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(File... params) {
        return fireBaseBitmap = crearBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null && imageViewReference != null){
            ImageView image = imageViewReference.get();
            if (image != null){
                image.setImageBitmap(bitmap);
            }
        }

    }

    public int calcularInSampleSize (BitmapFactory.Options bmfOptions) {
        final int photoWidth = bmfOptions.outWidth;
        final int photoHeight = bmfOptions.outHeight;
        int scaleFactor = 1;

        if (photoHeight > TARGET_IMAGE_VIEW_HEIGHT || photoWidth > TARGET_IMAGE_VIEW_WIDTH){
            final int halfPhotoWidht = photoWidth/2;
            final int halfPhotoHeight = photoHeight/2;

            while (halfPhotoWidht/scaleFactor >TARGET_IMAGE_VIEW_WIDTH ||
                    halfPhotoHeight/scaleFactor > TARGET_IMAGE_VIEW_HEIGHT){
                scaleFactor +=1;
            }
        }

        return scaleFactor;
    }

    public Bitmap crearBitmap (File photo){
        BitmapFactory.Options bmfOptions = new BitmapFactory.Options();
        bmfOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.getAbsolutePath(), bmfOptions);
        bmfOptions.inSampleSize = calcularInSampleSize(bmfOptions);
        bmfOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(photo.getAbsolutePath(), bmfOptions);
    }

    public void setTARGET_IMAGE_VIEW_WIDTH(int TARGET_IMAGE_VIEW_WIDTH) {
        this.TARGET_IMAGE_VIEW_WIDTH = TARGET_IMAGE_VIEW_WIDTH;
    }

    public void setTARGET_IMAGE_VIEW_HEIGHT(int TARGET_IMAGE_VIEW_HEIGHT) {
        this.TARGET_IMAGE_VIEW_HEIGHT = TARGET_IMAGE_VIEW_HEIGHT;
    }
}
