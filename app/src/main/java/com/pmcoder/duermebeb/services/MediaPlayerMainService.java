package com.pmcoder.duermebeb.services;

import android.app.Activity;
import android.app.Service;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;
import com.pmcoder.duermebeb.global.GlobalVariables;
import com.pmcoder.duermebeb.views.view.MainActivity;

import java.io.File;
import java.io.IOException;
import static com.pmcoder.duermebeb.global.GlobalVariables.*;

public class MediaPlayerMainService extends Service implements MediaPlayer.OnPreparedListener {

    private FirebaseStorage fBStorage = FirebaseStorage.getInstance();
    private StorageReference sReference = fBStorage
            .getReferenceFromUrl("gs://duerme-bebe.appspot.com")
            .child("music");
    public MediaPlayer mp = null;
    public static String songNow;
    public static File soundNow;
    private Activity ctx;
    private boolean playingUrl = false;
    private MainActivity comm = null;

    public MediaPlayerMainService () {
        //Constructor vacío obligatorio
    }

    public MediaPlayerMainService(Activity ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        if (comm == null){
            comm = (MainActivity) ctx;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    public void preparePlaying(String song){

        if (comm == null){
            comm = (MainActivity) ctx;
        }

        if (!GlobalVariables.isOnline(ctx)){

            Toast.makeText(ctx, "Revisa tu conexión a Internet", Toast.LENGTH_LONG).show();

            comm.playPauseButton(false);
            LOADING = false;
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
        if (comm == null){
            comm = (MainActivity) ctx;
        }
        mp.seekTo(0);
        mp.start();
        mp.setLooping(true);
        comm.playPauseButton(PLAYING);
        LOADING = false;
        setLoadingState();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stop(){
        if (comm == null){
            comm = (MainActivity) ctx;
        }

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
        comm.playPauseButton(false);
    }

    private void playPause () {
        if (comm == null){
            comm = (MainActivity) ctx;
        }
        if (LOADING){return;}
        if (PLAYING){
            mp.pause();
            comm.playPauseButton(PLAYING = false);
            setLoadingState();
        }else {
            mp.start();
            comm.playPauseButton(PLAYING = true);
        }
    }

    public void setLoadingState(){
        if (GlobalVariables.progressBar != null){
            GlobalVariables.progressBar.setVisibility(View.GONE);
        }
    }

    public Boolean setPlayingUrl(String song) {
        if (comm == null){
            comm = (MainActivity) ctx;
        }

        if(!playingUrl){
            stop();
            playingUrl = true;
        }

        if (LOADING){return false;}

        if (mp == null){
            mp = new MediaPlayer();
            preparePlaying(song);
            return true;
        }else {
            if(song.equals(songNow)){
                playPause();
                return false;
            }else {
                mp.stop();
                mp.reset();
                comm.playPauseButton(false);
                preparePlaying(song);
                return true;
            }
        }
    }

    public boolean setPlayingLocal (File song){
        if (LOADING) return false;

        if (playingUrl){
            stop();
            playingUrl = false;
        }

        if (mp == null){
            mp = new MediaPlayer();
            soundNow = song;
            prepareLocal(song);
        }
        else {
            if(soundNow.equals(song)) {

                if (PLAYING) {
                    mp.pause();
                    playPause();
                    PLAYING = false;
                } else {
                    mp.start();
                    playPause();
                    PLAYING = true;
                }
            }
            else {
                mp.stop();
                mp.reset();
                prepareLocal(song);
            }

        }

        return PLAYING;
    }

    public void prepareLocal (File song){
        try {
            mp.setDataSource(song.getAbsolutePath());
            mp.prepare();
            mp.setOnPreparedListener(this);
            PLAYING = true;
            soundNow = song;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}