package android.music.sleep.model;

import com.google.firebase.database.Exclude;

public class UploadSong {

    public String songName, songArtist, songRaaga, songDuration, songLink, mKey;

    public UploadSong() {

    }

    public UploadSong(String songName, String songArtist, String songRaaga, String songDuration, String songLink) {
        if (songName.trim().equals("")) {
            songName = "Undefined";
        }
        if (songArtist.trim().equals("")) {
            songArtist = "Undefined";
        }
        if (songRaaga.trim().equals("")) {
            songRaaga = "Undefined";
        }
        this.songName = songName;
        this.songArtist = songArtist;
        this.songRaaga = songRaaga;
        this.songDuration = songDuration;
        this.songLink = songLink;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongRaaga() {
        return songRaaga;
    }

    public void setSongRaaga(String songRaaga) {
        this.songRaaga = songRaaga;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
