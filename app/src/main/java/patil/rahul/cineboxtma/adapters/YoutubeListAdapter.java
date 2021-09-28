package patil.rahul.cineboxtma.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.models.VideoEntry;

/**
 * Created by rahul on 28/2/18.
 */

public class YoutubeListAdapter extends RecyclerView.Adapter<YoutubeListAdapter.YoutubeViewHolder> {

    private Context mContext;
    private List<VideoEntry> entries;
    private OnYoutubeItemClickListener onYoutubeItemClickListener;

    public interface OnYoutubeItemClickListener{
        void onVideoClick(VideoEntry videoEntry);
    }

    public YoutubeListAdapter(Context context, OnYoutubeItemClickListener onYoutubeItemClickListener){
        mContext = context;
        this.onYoutubeItemClickListener = onYoutubeItemClickListener;
    }

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_youtube_list, parent, false);
        return new YoutubeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {
        VideoEntry entry = entries.get(position);
        String thumbnailUrl = "https://img.youtube.com/vi/"+entry.getVideoId()+"/sddefault.jpg";

        Log.d("YOUTUBE_IMAGE", thumbnailUrl);

        Uri uri = Uri.parse(thumbnailUrl);
        holder.videoThumbnail.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return null == entries ? 0 : entries.size();
    }

    public void addVideoEntries(List<VideoEntry> entries){
        if ( entries != null){
            this.entries = entries;
            notifyDataSetChanged();
        }
    }

    class YoutubeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private SimpleDraweeView videoThumbnail;
        YoutubeViewHolder(View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
             int position = getAdapterPosition();
             onYoutubeItemClickListener.onVideoClick(entries.get(position));
        }
    }
}
