package com.dam.musicplayer;

import android.net.Uri;

public class ModelSong {

    private String songTitle;
    private String songDuration;
    private String songArtist;
    private Uri songCover;
    private Uri songUri;

    public ModelSong() {
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public Uri getSongCover() {
        return songCover;
    }

    public void setSongCover(Uri songCover) {
        this.songCover = songCover;
    }

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }
}
