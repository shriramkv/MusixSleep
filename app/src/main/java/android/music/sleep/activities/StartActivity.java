package android.music.sleep.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.music.sleep.R;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    String music_url = "https://www.musicwemake.com/listen?song=sleep-YWanE74XUUqUyRKibEQipa";
    TextView song_name;
    SeekBar song_seekbar;
    ImageView playIcon, previousIcon, nextIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        playIcon = findViewById(R.id.play_icon);
        previousIcon = findViewById(R.id.previous_icon);
        nextIcon = findViewById(R.id.next_icon);
        song_name = findViewById(R.id.current_song);
        song_seekbar = findViewById(R.id.song_seekbar);
        song_name.setSelected(true);

        handler = new Handler();

        HashMap<String, String> songs = new HashMap<>();
        songs.put(music_url, "Sample data is playing and this is a long text to check the marquee of edit text");

        song_name.setText(songs.get(music_url));

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(music_url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Toast.makeText(StartActivity.this, "Media Buffering complete ... ", Toast.LENGTH_SHORT).show();
                    song_seekbar.setMax(mediaPlayer.getDuration());
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
            Toast.makeText(StartActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }

        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
            }
        });

        song_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void playSong() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playIcon.setImageResource(R.drawable.ic_pause_black_24dp);
            changeSeekbar();
        } else {
            mediaPlayer.pause();
            playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            changeSeekbar();
        }
    }

    private void changeSeekbar() {
        song_seekbar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }
}
