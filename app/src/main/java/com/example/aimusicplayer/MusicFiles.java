package com.example.aimusicplayer;

public class MusicFiles {
    private String path;
    private String title;
    private String artist;
    private String ablum;
    private String duration;


    public MusicFiles(String path, String title, String artist, String ablum, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.ablum = ablum;
        this.duration = duration;
    }


    public MusicFiles() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAblum() {
        return ablum;
    }

    public void setAblum(String ablum) {
        this.ablum = ablum;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
