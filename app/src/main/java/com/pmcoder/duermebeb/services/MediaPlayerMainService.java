package com.pmcoder.duermebeb.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pmcoder.duermebeb.constants.Constant;
import java.io.IOException;
import static com.pmcoder.duermebeb.constants.Constant.*;

public class MediaPlayerMainService extends Service implements MediaPlayer.OnPreparedListener {

    private FirebaseStorage fBStorage = FirebaseStorage.getInstance();
    private StorageReference sReference = fBStorage.getReferenceFromUrl("gs://duerme-bebe.appspot.com").child("music");
    public MediaPlayer mp = null;
    public static String songNow;
    private Context ctx;

    public MediaPlayerMainService(){
    }

    public MediaPlayerMainService(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    public void preparePlaying(String song){

        if (!Constant.funcionaInternet()){
            Snackbar.make(Constant.viewHolder, "Revisa tu conexión a Internet", Snackbar.LENGTH_LONG).show();
            return;
        }

        LOADING = true;
        PLAYING = true;

        songNow = song;

        sReference.child(song)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                System.out.println("La URL es: " + url);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mp.setDataSource(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.setOnPreparedListener(MediaPlayerMainService.this);
                mp.prepareAsync();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.seekTo(0);
        mp.start();
        playBroadcastSender(PLAYING);
        LOADING = false;
        setLoadingState();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stop(){
        if (LOADING){return;}
        if(mp!=null){
            mp.stop();
            mp.release();
            mp = null;
            setLoadingState();
        }else {
            return;
        }
        PLAYING =  false;
        playBroadcastSender(false);
    }

    public void setPlaying(String song){

        if (LOADING){return;}

        if (mp == null){
            mp = new MediaPlayer();
            preparePlaying(song);
        }else {
            if(song == songNow){
                playPause();
            }else {
                mp.stop();
                mp.reset();
                playBroadcastSender(false);
                preparePlaying(song);
            }
        }
    }

    private void playPause()
    {
        if (LOADING){return;}
        if (PLAYING){
            mp.pause();
            PLAYING = false;
            playBroadcastSender(PLAYING);
            setLoadingState();
        }else {
            mp.start();
            PLAYING = true;
            playBroadcastSender(PLAYING);
        }
    }

    public void playBroadcastSender(Boolean b){
        Intent i = new Intent("AJUSTAR_REPRODUCTOR");
        i.putExtra("play", b);
        try {
            ctx.sendBroadcast(i);
        }catch (NullPointerException e){
            Log.e("SendBroadcast: ", "Error "+ e);
        }
    }

    public void setLoadingState(){
        if (Constant.viewHolder != null){
            Constant.viewHolder.setVisibility(View.GONE);
        }
    }
}
