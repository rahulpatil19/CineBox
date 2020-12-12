package patil.rahul.cineboxtma.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 29/1/18.
 */

public class TvHorizontalAdapter extends RecyclerView.Adapter<TvHorizontalAdapter.HorizontalViewHolder> {

    private Context mContext;
    private List<TvShows> mTvShowsList;
    private String imageQuality;
    private CineListener.OnTvClickListener onTvClickListener;

    public TvHorizontalAdapter(Context context, CineListener.OnTvClickListener onTvClickListener, String imageQuality) {
        mContext = context;
        this.imageQuality = imageQuality;
        this.onTvClickListener = onTvClickListener;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_horizontal_tv, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        TvShows tvShows = mTvShowsList.get(position);

        holder.mTvTitle.setText(tvShows.getName());
        Uri posterUrl = CineUrl.createImageUri(imageQuality, tvShows.getPosterPath());
        holder.mTvImage.setImageURI(posterUrl);

    }

    @Override
    public int getItemCount() {
        return null == mTvShowsList ? 0 : mTvShowsList.size();
    }

    public void addData(List<TvShows> movieList) {
            this.mTvShowsList = movieList;
            notifyDataSetChanged();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mTvImage;
        private TextView mTvTitle;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            mTvImage = itemView.findViewById(R.id.tv_horizontal_image);
            mTvTitle = itemView.findViewById(R.id.tv_horizontal_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int currentPosition =  getAdapterPosition();
            onTvClickListener.onTvClick(mTvShowsList.get(currentPosition));
        }
    }
}
