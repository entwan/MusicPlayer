package com.dam.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    /** Ajout automatique de la lecture **/
    MediaPlayer mediaPlayer = new MediaPlayer();

    /** Méthodes pour le fonctionnement de l'application **/
    public void play(View view) {
        mediaPlayer.start();
        Log.i(TAG, "play");
    }

    public void pause(View view) {
        mediaPlayer.pause();
        Log.i(TAG, "pause");
    }


    private void volume(){
        // Association de seekbar au Java
        SeekBar sbVolume = findViewById(R.id.sbVolume);

        // Initialiser le manager en tant que service
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Volume max du terminal
        int volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Valorisation de cette valeur au max de la seekbar
        sbVolume.setMax(volumeMax);

        // Définition du volume courant
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sbVolume.setProgress(currentVolume);

        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: Volume = " + Integer.toString(progress));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void position(){
        // Association de seekbar au Java
        SeekBar sbPosition = findViewById(R.id.sbPosition);

        // définir la valeur max de la seekbar position
        // getDuration renvoie le temps total de la chanson
        sbPosition.setMax(mediaPlayer.getDuration());

        // Part one : la gestion de l'utilisation
        sbPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "Position dans le morceau : " + Integer.toString(progress));
//                Log.i(TAG, "onProgressChanged: sbPosition.getProgress() : " + Integer.toString(sbPosition.getProgress()));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause(sbPosition);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(sbPosition.getProgress());
                play(sbPosition);

            }
        });

        // Part two : Gestion du déplacement du curseur par l'application
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Déplacement automatique
                sbPosition.setProgress(mediaPlayer.getCurrentPosition());
            }
        },0,300);

    }

    //END Volume
    /************** Cycles de vie **************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Methode 1 lecture automatique du son
        mediaPlayer = MediaPlayer.create(this,R.raw.sound);
        mediaPlayer.start();
        **/

        /** Methode 2 avec les boutons **/
        mediaPlayer = MediaPlayer.create(this,R.raw.sound);

        // Lancement de la methode
        volume();
        position();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}