package android.music.sleep.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    String music_url = "https://www.musicwemake.com/listen?song=sleep-YWanE74XUUqUyRKibEQipa";
    TextView song_name;
    SeekBar song_seekbar;
    ImageView playIcon, previousIcon, nextIcon;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<UploadSong> mUpload;
//    FirebaseStorage mStorage;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    SongsAdapter adapter;

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

        adapter = new SongsAdapter(StartActivity.this,mUpload);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUpload.clear();
                for (DataSnapshot dss:dataSnapshot.getChildren()) {
                    UploadSong uploadSong = dss.getValue(UploadSong.class);
                    uploadSong.setmKey(dss.getKey());
                    mUpload.add(uploadSong);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
//        handler = new Handler();
//
//        HashMap<String, String> songs = new HashMap<>();
//        songs.put(music_url, "Sample data is playing and this is a long text to check the marquee of edit text");
//
//        song_name.setText(songs.get(music_url));
//
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(music_url);
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    Toast.makeText(StartActivity.this, "Media Buffering complete ... ", Toast.LENGTH_SHORT).show();
//                    song_seekbar.setMax(mediaPlayer.getDuration());
//                }
//            });
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(StartActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
//        }
//
//        playIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playSong();
//            }
//        });
//
//        song_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    mediaPlayer.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

//    private void playSong() {
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//            playIcon.setImageResource(R.drawable.ic_pause_black_24dp);
//            changeSeekbar();
//        } else {
//            mediaPlayer.pause();
//            playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
//            changeSeekbar();
//        }
//    }

//    private void changeSeekbar() {
//        song_seekbar.setProgress(mediaPlayer.getCurrentPosition());
//        if (mediaPlayer.isPlaying()) {
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//                    changeSeekbar();
//                }
//            };
//            handler.postDelayed(runnable,1000);
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void playSong(List<UploadSong> arrayListSongs, int adapterPosition) throws IOException {
        UploadSong uploadSong = arrayListSongs.get(adapterPosition);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(uploadSong.getSongLink());

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer.prepareAsync();
    }
}
