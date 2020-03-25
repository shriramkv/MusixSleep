package android.music.sleep.adapters;

import android.content.Context;
import android.music.sleep.R;
import android.music.sleep.activities.StartActivity;
import android.music.sleep.model.UploadSong;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsAdapterViewHolder> {

    Context context;
    List<UploadSong> arrayListSongs;

    public SongsAdapter(Context context, List<UploadSong> arrayListSongs) {
        this.context = context;
        this.arrayListSongs = arrayListSongs;
    }

    @NonNull
    @Override
    public SongsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        return new SongsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapterViewHolder holder, int position) {
        UploadSong uploadSong = arrayListSongs.get(position);
        holder.name.setText(uploadSong.getSongName());
        holder.artist.setText("Artist : "+uploadSong.getSongArtist());
        holder.raaga.setText("Raaga : "+uploadSong.getSongRaaga());
        holder.duration.setText("Duration : "+uploadSong.getSongDuration());
    }

    @Override
    public int getItemCount() {
        return arrayListSongs.size();
    }

    public class SongsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, artist, raaga, duration;

        public SongsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sName);
            artist = itemView.findViewById(R.id.sArtist);
            raaga = itemView.findViewById(R.id.sRaaga);
            duration = itemView.findViewById(R.id.sDuration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                ((StartActivity)context).playSong(arrayListSongs, getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
