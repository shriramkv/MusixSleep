package android.music.sleep.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.music.sleep.adapters.SongsAdapter;
import android.music.sleep.model.UploadSong;
import android.music.sleep.R;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    TextView song_name;
    SeekBar song_seekbar;
    ImageView playIcon, previousIcon, nextIcon;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<UploadSong> mUpload;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    SongsAdapter adapter;
    int pointer = 0;

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
        progressBar = findViewById(R.id.progress_spin);
        Sprite waves = new Wave();
        progressBar.setIndeterminateDrawable(waves);
        recyclerView = findViewById(R.id.song_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUpload = new ArrayList<>();

        adapter = new SongsAdapter(StartActivity.this, mUpload);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUpload.clear();
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    UploadSong uploadSong = dss.getValue(UploadSong.class);
                    uploadSong.setmKey(dss.getKey());
                    mUpload.add(uploadSong);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void playPauseSong() {
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
        if (mediaPlayer != null) {
            song_seekbar.setProgress(mediaPlayer.getCurrentPosition());
            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekbar();
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void playSong(List<UploadSong> arrayListSongs, int adapterPosition) throws IOException {
        pointer = adapterPosition;
        if (pointer >= 0 && pointer < arrayListSongs.size()) {
            UploadSong uploadSong = arrayListSongs.get(adapterPosition);
            song_name.setText(uploadSong.getSongName() + " - by " + uploadSong.getSongArtist());
            handler = new Handler();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(uploadSong.getSongLink());
            mediaPlayer.prepareAsync();
            song_seekbar.setProgress(0);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    song_seekbar.setMax(mediaPlayer.getDuration());
                    playPauseSong();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            });


            playIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPauseSong();
                }
            });

            nextIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playSong(mUpload,pointer+1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            previousIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playSong(mUpload,pointer-1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        } else if (pointer < 0){
            playSong(mUpload, mUpload.size()-1);
        } else if (pointer > mUpload.size()-1) {
            playSong(mUpload, 0);
        }
    }
}
