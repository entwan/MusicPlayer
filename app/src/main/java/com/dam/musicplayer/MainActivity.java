package com.dam.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //debut version v2
    private static final String TAG = "MainActivity";


    /**
     * Ajout automatique de la lecture
     **/
    private MediaPlayer mediaPlayer;

    private SeekBar sbPosition;

    private TextView tvCurrentPos, tvTotalDuration, tvSongTitle;

    private ImageView btnPrev, btnPlay, btnNext;

    private RecyclerView recyclerView;

    private ArrayList<ModelSong> songArrayList;

    public static final int PERMISSION_READ = 0;


    // Methode d'initialisation
    private void init() {
        // init UI
        sbPosition = findViewById(R.id.sbPosition);
        tvCurrentPos = findViewById(R.id.tvCurrentPos);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        btnPrev = findViewById(R.id.iv_btnPrev);
        btnPlay = findViewById(R.id.iv_btnPlay);
        btnNext = findViewById(R.id.iv_btnNext);
        recyclerView = findViewById(R.id.recyclerView);

        // init
        mediaPlayer = new MediaPlayer();

        songArrayList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    /**
     * Méthodes pour le fonctionnement de l'application
     **/
    // Methode pour verifier les permissions de l'application
    public boolean checkPermission() {
        int READ_INTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (READ_INTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Please allow storage access permission", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lancement de l'app
                        setSong();
                    }
                }
            }
        }
    }

    public void getAudioFiles() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        // loop au travers de toutes les lignes et ajout dans le ArrayList
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Récuperation des donnees pour injection dans le tableau
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long album_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                Uri coverFolder = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(coverFolder, album_id);

                //Utilisation du model pour remplir les valeurs dans le tableau
                ModelSong modelSong = new ModelSong();
                modelSong.setSongTitle(title);
                modelSong.setSongArtist(artist);
                modelSong.setSongDuration(duration);
                modelSong.setSongCover(albumArtUri);
                modelSong.setSongUri(Uri.parse(url));

                // Ajout de ces données dans le ArrayList
                songArrayList.add(modelSong);

            } while (cursor.moveToNext());
        }

        // Ajpout de l'adapter qui va permettre l'affichage des donnees recuperees dans le ArrayList
        AdapterSong adapterSong = new AdapterSong(this,songArrayList);

        // Adaptation des donnees de ArrayList au recycler
        recyclerView.setAdapter(adapterSong);

        adapterSong.setOnItemClickListener(new AdapterSong.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                // a venir
            }
        });




    }

    private void setSong() {
        //getAudioFiles();
    }

    public void play(View view) {
        mediaPlayer.start();
        Log.i(TAG, "play");
    }

    public void pause(View view) {
        mediaPlayer.pause();
        Log.i(TAG, "pause");
    }



    private void position() {
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
        }, 0, 300);

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
        //mediaPlayer = MediaPlayer.create(this,R.raw.sound);



        // Lancement de la methode
        //position();

        //en attendant Godot

        init();
        checkPermission();
        getAudioFiles();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}