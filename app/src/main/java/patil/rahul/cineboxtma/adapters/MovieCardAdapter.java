package patil.rahul.cineboxtma.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineGenre;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 6/2/18.
 */

public class MovieCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MOVIE = 0;
    private static final int TYPE_LOADING = 1;
    private Context mContext;
    private List<Movie> mMovieList;
    private String imageQuality;
    private Activity mActivity;
    private CineListener.OnMovieClickListener onMovieClickListener;

    public MovieCardAdapter(Context context, CineListener.OnMovieClickListener onMovieClickListener, String imageQuality, Activity activity) {
        mContext = context;
        this.imageQuality = imageQuality;
        this.onMovieClickListener = onMovieClickListener;
        this.mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_MOVIE) {
            return new MovieHolder(inflater.inflate(R.layout.item_card_movie, parent, false));
        } else {
            return new LoadingHolder(inflater.inflate(R.layout.item_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);
        if (holder instanceof MovieHolder) {
            final MovieHolder movieHolder = (MovieHolder) holder;

            Uri imageUri = CineUrl.createImageUri(mContext.getString(R.string.backdrop_size), movie.getBackdropPath());
            movieHolder.mMovieImage.setImageURI(imageUri);
            movieHolder.mMovieTitle.setText(movie.getTitle());
            movieHolder.mMovieReleaseDate.setText(CineDateFormat.formatDate(movie.getReleaseDate()));
            if (movie.getVoteAverage().equals("0")){
                movieHolder.mMovieVoteAverage.setVisibility(View.GONE);
            }
            else {
                movieHolder.mMovieVoteAverage.setVisibility(View.VISIBLE);
                movieHolder.mMovieVoteAverage.setText(movie.getVoteAverage());
            }
            movieHolder.mMovieOverview.setText(movie.getOverview());
            List<Integer> genreList = movie.getGenre();
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < genreList.size(); j++) {
                String genre = CineGenre.getGenres(genreList.get(j));
                builder = builder.append(genre);
                if (j < genreList.size() - 1) {
                    builder.append(",\b");
                }
                movieHolder.mMovieGenres.setText(builder.toString());
            }
        /* Share button removed from layout
           movieHolder.mShareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = CineUrl.createTMDbWebUri(movie.getId(), "movie");
                    String title = "Complete Action using";
                    String mimeType = "text/plane";
                    String textToShare = "Checkout this movie on TMDb "+ uri;

                    ShareCompat.IntentBuilder.from(mActivity).setType(mimeType)
                            .setChooserTitle(title)
                            .setText(textToShare)
                            .startChooser();
                }
            });*/
        }

        else if (holder instanceof LoadingHolder) {
            LoadingHolder loadingHolder = (LoadingHolder) holder;
            loadingHolder.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList == null ? 0 : mMovieList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMovieList.get(position) == null ? TYPE_LOADING : TYPE_MOVIE;
    }

    public void addData(List<Movie> movieList) {
        mMovieList = movieList;
    }

    public void clearAdapter() {
        mMovieList.clear();
        notifyDataSetChanged();
    }

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mMovieGenres;
        private SimpleDraweeView mMovieImage;
        private TextView mMovieTitle;
        private TextView mMovieVoteAverage;
        private TextView mMovieReleaseDate;
        private TextView mMovieOverview;

        private MovieHolder(View itemView) {
            super(itemView);
            mMovieImage = itemView.findViewById(R.id.movie_image);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieReleaseDate = itemView.findViewById(R.id.movie_release_date);
            mMovieVoteAverage = itemView.findViewById(R.id.movie_voteAverage);
            mMovieGenres = itemView.findViewById(R.id.movie_genres);
            mMovieOverview = itemView.findViewById(R.id.movie_overview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            onMovieClickListener.onMovieClick(mMovieList.get(adapterPosition));
        }
    }

    static class LoadingHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        LoadingHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
