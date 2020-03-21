package android.music.sleep.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.music.sleep.R;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    String music_url = "https://sampleswap.org/samples-ghost/MELODIC%20LOOPS/SAMPLED%20MUSIC%20LOOPS/537[kb]077_lofi-dub-groove.aif.mp3";
    TextView song_name;
    ImageView playIcon, previousIcon, nextIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        playIcon = findViewById(R.id.play_icon);
        previousIcon = findViewById(R.id.previous_icon);
        nextIcon = findViewById(R.id.next_icon);
        song_name = findViewById(R.id.current_song);
        song_name.setSelected(true);
        HashMap<String, String> songs = new HashMap<>();
        songs.put(music_url, "Sample data is playing and this is a long text to check the marquee of edit text");

        song_name.setText(songs.get(music_url));

        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    playIcon.setImageResource(R.drawable.ic_pause_black_24dp);
                } else {
                    mediaPlayer.pause();
                    playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(music_url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Toast.makeText(StartActivity.this,"Media Buffering complete ... ",Toast.LENGTH_SHORT).show();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
