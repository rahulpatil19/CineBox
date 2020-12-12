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

import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineGenre;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 21/2/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MOVIE = 0;
    private static final int TYPE_LOADING = 1;

    private Context mContext;
    private List<Movie> mMovieList;
    private CineListener.OnMovieClickListener movieClickListener;
    private String imageQuality;

    public MovieAdapter(Context context, CineListener.OnMovieClickListener movieClickListener, String imageQuality) {
        mContext = context;
        this.movieClickListener = movieClickListener;
        this.imageQuality = imageQuality;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_MOVIE) {
            return new MovieHolder(inflater.inflate(R.layout.item_movie, parent, false));
        } else {
            return new LoadingHolder(inflater.inflate(R.layout.item_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);

        if (holder instanceof MovieHolder) {
            MovieHolder movieHolder = (MovieHolder) holder;
            Uri posterUrl = CineUrl.createImageUri(imageQuality, movie.getPosterPath());
            movieHolder.mMovieImage.setImageURI(posterUrl);
            movieHolder.mMovieTitle.setText(movie.getTitle());
            movieHolder.mMovieReleaseDate.setText(CineDateFormat.formatDate(movie.getReleaseDate()));
            movieHolder.mMovieOverview.setText(movie.getOverview());
            int voteValue = Integer.parseInt(movie.getVoteCount());
            if (voteValue > 10){
                movieHolder.mMovieVoteCount.setVisibility(View.VISIBLE);
                movieHolder.mMovieVoteAverage.setVisibility(View.VISIBLE);
                movieHolder.mMovieVoteAverage.setText(movie.getVoteAverage());
                movieHolder.mMovieVoteCount.setText(String.format("%s votes", movie.getVoteCount() + "\b"));
            }
            else {
                movieHolder.mMovieVoteCount.setVisibility(View.GONE);
                movieHolder.mMovieVoteAverage.setVisibility(View.GONE);
            }
            movieHolder.bindGenres(movie);

        } else if (holder instanceof LoadingHolder) {
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

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mMovieImage;
        private TextView mMovieTitle;
        private TextView mMovieReleaseDate;
        private TextView mMovieOverview;
        private TextView mMovieVoteAverage;
        private TextView mMovieVoteCount;
        private TextView mMovieGenre1;
        private TextView mMovieGenre2;

        private MovieHolder(View itemView) {
            super(itemView);
            mMovieImage = itemView.findViewById(R.id.movie_image);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieReleaseDate = itemView.findViewById(R.id.movie_release_date);
            mMovieOverview = itemView.findViewById(R.id.movie_overview);
            mMovieVoteAverage = itemView.findViewById(R.id.movie_voteAverage);
            mMovieVoteCount = itemView.findViewById(R.id.movie_voteCount);
            mMovieGenre1 = itemView.findViewById(R.id.movie_genre1);
            mMovieGenre2 = itemView.findViewById(R.id.movie_genre2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            movieClickListener.onMovieClick(mMovieList.get(position));
        }

        private void bindGenres(Movie movie) {
            int genreSize = movie.getGenre().size();
            String genre1;
            String genre2;
            if (genreSize > 0) {
                switch (genreSize) {
                    case 1:
                        genre1 = CineGenre.getGenres(movie.getGenre().get(0));
                        mMovieGenre1.setText(genre1);
                        mMovieGenre1.setVisibility(View.VISIBLE);
                        if (mMovieGenre2.getVisibility() == View.VISIBLE)
                            mMovieGenre2.setVisibility(View.INVISIBLE);
                        break;

                    case 2:
                        genre1 = CineGenre.getGenres(movie.getGenre().get(0));
                        genre2 = CineGenre.getGenres(movie.getGenre().get(1));
                        mMovieGenre1.setText(genre1);
                        mMovieGenre2.setText(genre2);
                        mMovieGenre1.setVisibility(View.VISIBLE);
                        mMovieGenre2.setVisibility(View.VISIBLE);
                        break;

                    default:
                        genre1 = CineGenre.getGenres(movie.getGenre().get(0));
                        genre2 = CineGenre.getGenres(movie.getGenre().get(1));
                        mMovieGenre1.setText(genre1);
                        mMovieGenre2.setText(genre2);
                        mMovieGenre1.setVisibility(View.VISIBLE);
                        mMovieGenre2.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                if (mMovieGenre1.getVisibility() == View.VISIBLE)
                    mMovieGenre1.setVisibility(View.INVISIBLE);
                if (mMovieGenre2.getVisibility() == View.VISIBLE)
                    mMovieGenre2.setVisibility(View.INVISIBLE);
            }
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        public LoadingHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}