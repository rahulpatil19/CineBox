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
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.CineUrl;

public class MovieCreditAdapter extends RecyclerView.Adapter<MovieCreditAdapter.CreditMovieViewHolder> {
    private Context mContext;
    private List<Movie> movieList;
    private CineListener.OnMovieClickListener onMovieClickListener;
    private String mImageQuality;

    public MovieCreditAdapter(Context context, CineListener.OnMovieClickListener onMovieClickListener, String imageQuality) {
        mContext = context;
        this.onMovieClickListener = onMovieClickListener;
        this.mImageQuality = imageQuality;
    }

    @NonNull
    @Override
    public CreditMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_credit_movie, parent, false);
        return new CreditMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditMovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Uri posterUrl = CineUrl.createImageUri(mImageQuality, movie.getPosterPath());

        holder.mMovieImage.setImageURI(posterUrl);

        holder.mMovieTitle.setText(movie.getTitle());
        holder.mMovieReleaseDate.setText(CineDateFormat.formatDate(movie.getReleaseDate()));

        if (movie.getCharacter().trim().length() > 0){
            if (movie.isHasCharacter()){
                holder.mMovieCharacter.setVisibility(View.VISIBLE);
                holder.mMovieCharacter.setText(String.format("as %s", movie.getCharacter()));
            }
            else {
                holder.mMovieCharacter.setVisibility(View.VISIBLE);
                holder.mMovieCharacter.setText(movie.getCharacter());
            }
        }
        else {
            holder.mMovieCharacter.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == movieList ? 0 : movieList.size();
    }

    public void addData(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public void clearAdapter(){
            movieList.clear();
            notifyDataSetChanged();
    }

    class CreditMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mMovieImage;
        private TextView mMovieTitle;
        private TextView mMovieCharacter;
        private TextView mMovieReleaseDate;

        CreditMovieViewHolder(View itemView) {
            super(itemView);
            mMovieImage = itemView.findViewById(R.id.movie_image);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieCharacter = itemView.findViewById(R.id.movie_character);
            mMovieReleaseDate = itemView.findViewById(R.id.movie_release_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            onMovieClickListener.onMovieClick(movieList.get(itemPosition));
        }
    }
}
