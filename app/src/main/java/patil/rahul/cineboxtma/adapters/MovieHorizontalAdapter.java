package patil.rahul.cineboxtma.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 29/1/18.
 */

public class MovieHorizontalAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;
    private Context mContext;
    private List<Movie> movieList;
    private CineListener.OnMovieClickListener onMovieClickListener;
    private String mImageQuality;

    public MovieHorizontalAdapter(Context context, CineListener.OnMovieClickListener onMovieClickListener, String imageQuality) {
        mContext = context;
        this.onMovieClickListener = onMovieClickListener;
        this.mImageQuality = imageQuality;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.item_horizontal_movie, parent, false);
            return new HorizontalViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_progress, parent, false);
            return new LoadingHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof HorizontalViewHolder){
            HorizontalViewHolder holder = (HorizontalViewHolder) viewHolder;
            Movie movie = movieList.get(position);

            boolean showReleaseDate = movie.isShowReleaseDate();

            Uri posterUrl = CineUrl.createImageUri(mImageQuality, movie.getPosterPath());
            holder.mMovieImage.setImageURI(posterUrl);
            holder.mMovieTitle.setText(movie.getTitle());

            if (showReleaseDate){
                holder.mMovieReleaseDate.setText(CineDateFormat.formatDate(movie.getReleaseDate()));
            }
            else {
                holder.mMovieReleaseDate.setVisibility(View.GONE);
            }
        }
        else {
            LoadingHolder holder = (LoadingHolder) viewHolder;
            holder.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return null == movieList ? 0 : movieList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return movieList.get(position) == null ? TYPE_LOADING : TYPE_ITEM;
    }

    public void addData(List<Movie> movieList) {
            this.movieList = movieList;
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mMovieImage;
        private TextView mMovieTitle;
        private TextView mMovieReleaseDate;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            mMovieImage = itemView.findViewById(R.id.movie_image);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieReleaseDate = itemView.findViewById(R.id.movie_release_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            onMovieClickListener.onMovieClick(movieList.get(itemPosition));
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        LoadingHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
