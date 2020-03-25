package android.music.sleep.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.music.sleep.model.UploadSong;
import android.music.sleep.R;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {

    EditText name, artist, raaga;
    Button select, upload;
    ProgressBar progressBar;
    TextView selected_song;
    Uri audioUri;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    DatabaseReference referenceSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initViews();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioFile(v);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudioToFirebase(v);
            }
        });
    }

    private void initViews() {
        name = findViewById(R.id.song_name);
        artist = findViewById(R.id.song_artist);
        raaga = findViewById(R.id.song_raaga);
        select = findViewById(R.id.song_select);
        upload = findViewById(R.id.song_upload);
        progressBar = findViewById(R.id.song_upload_progress);
        selected_song = findViewById(R.id.selected_song);
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
    }

    public void openAudioFile(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, 101);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            audioUri = data.getData();
            String fileName = getFileName(audioUri);
            selected_song.setText(fileName);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFileName(Uri audioUri) {
        String result = null;
        if (audioUri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(audioUri, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = audioUri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadAudioToFirebase(View v) {
        if (selected_song.getText().toString().equals("No song selected")) {
            Toast.makeText(UploadActivity.this, "Please select a song", Toast.LENGTH_LONG).show();
        } else {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(),"Song upload is already in progress",Toast.LENGTH_LONG).show();
            } else {
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        if (audioUri != null) {
            String durationTxt;
            Toast.makeText(UploadActivity.this, "Uploading please wait ... ", Toast.LENGTH_LONG).show();

            progressBar.setVisibility(View.VISIBLE);

            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(audioUri));

            int durationInMillis = findSongDuration(audioUri);

            if (durationInMillis == 0) {
                durationTxt = "NA";
            }
            durationTxt = getDurationFromMilli(durationInMillis);


            final String finalDurationTxt = durationTxt;
            mUploadTask = storageReference.putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UploadSong uploadSong = new UploadSong(name.getText().toString(),
                                            artist.getText().toString(), raaga.getText().toString(), finalDurationTxt, uri.toString());
                                    String uploadId = referenceSongs.push().getKey();
                                    referenceSongs.child(uploadId).setValue(uploadSong);
                                }
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    })
            ;

        } else {
            Toast.makeText(UploadActivity.this, "No song selected to upload", Toast.LENGTH_LONG).show();
        }
    }

    private String getDurationFromMilli(int durationInMillis) {
        Date date = new Date(durationInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private int findSongDuration(Uri audioUri) {
        int timeInMillis = 0;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, audioUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillis = Integer.parseInt(time);
            retriever.release();
            return timeInMillis;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getFileExtension(Uri audioUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }
}
