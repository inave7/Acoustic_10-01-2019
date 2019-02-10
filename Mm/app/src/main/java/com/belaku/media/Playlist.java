package com.belaku.media;

import java.util.ArrayList;

public class Playlist {

    private String plName;
    private ArrayList<AudioSong> plSongs = new ArrayList<>();

    public Playlist(String plName) {
        this.plName = plName;
    }

    public String getPlName() {
        return plName;
    }

    public void setPlName(String plName) {
        this.plName = plName;
    }

    public ArrayList<AudioSong> getPlSongs() {
        return plSongs;
    }

    public void setPlSongs(ArrayList<AudioSong> plSong) {
        this.plSongs = plSong;
    }

    public void setPlSong(AudioSong audioSong) {
        plSongs.add(audioSong);
        setPlSongs(plSongs);

    }
}
