package patil.rahul.cineboxtma.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineGenre;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 13/2/18.
 */

public class TvAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;
    private List<TvShows> mTvList;
    private Context mContext;
    private String imageQuality;
    private AppCompatActivity activity;

    private CineListener.OnTvClickListener onTvClickListener;

    public TvAdapter(Context context, CineListener.OnTvClickListener onTvClickListener, String imageQuality, AppCompatActivity activity) {
        mContext = context;
        this.onTvClickListener = onTvClickListener;
        this.imageQuality = imageQuality;
        this.activity = activity;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_ITEM) {
            return new TvViewHolder(inflater.inflate(R.layout.item_card_tv, parent, false));
        } else {
            return new LoadingHolder(inflater.inflate(R.layout.item_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final TvShows tvShows = mTvList.get(position);
        if (viewHolder instanceof TvViewHolder) {

            TvViewHolder tvViewHolder = (TvViewHolder) viewHolder;

            Uri uri = CineUrl.createImageUri(mContext.getString(R.string.backdrop_size), tvShows.getBackdropPath());
            tvViewHolder.mBackdropImage.setImageURI(uri);
            tvViewHolder.mTitle.setText(tvShows.getName());
            tvViewHolder.mFirstAirDate.setText(CineDateFormat.formatDate(tvShows.getFirstAirDate()));
            tvViewHolder.mOverview.setText(tvShows.getOverview());
            if (!tvShows.getVoteAverage().equals("0")){
                tvViewHolder.mVoteAverage.setText(tvShows.getVoteAverage());
            }
            else {
                tvViewHolder.mVoteAverage.setVisibility(View.INVISIBLE);
            }

            List<Integer> genreList = new ArrayList<>(tvShows.getGenres());

            if (genreList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < genreList.size(); i++) {
                    stringBuilder.append(CineGenre.getGenres(genreList.get(i)));
                    if (i < genreList.size() - 1){
                        stringBuilder.append(", ");
                    }
                }
                tvViewHolder.mGenres.setText(stringBuilder.toString());
            }

            tvViewHolder.mShareTvBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = CineUrl.createTMDbWebUri(tvShows.getId(), "tv");
                    String title = "Complete Action using";
                    String mimeType = "text/plane";
                    String textToShare = "Checkout this Tv Shows on TMDb "+ uri;

                    ShareCompat.IntentBuilder.from(activity).setType(mimeType)
                            .setChooserTitle(title)
                            .setText(textToShare)
                            .startChooser();
                }
            });

        } else if (viewHolder instanceof LoadingHolder) {
            LoadingHolder loadingHolder = (LoadingHolder) viewHolder;
            loadingHolder.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mTvList == null ? 0 : mTvList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTvList.get(position) == null ? TYPE_LOADING : TYPE_ITEM;
    }

    public void addData(List<TvShows> list) {
        this.mTvList = list;
    }

    public void clearAdapter() {
        mTvList.clear();
        notifyDataSetChanged();
    }

    class TvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mBackdropImage;
        private TextView mTitle;
        private TextView mVoteAverage;
        private TextView mFirstAirDate;
        private TextView mOverview;
        private TextView mGenres;
        private Button mTvDetailBtn;
        private Button mShareTvBtn;

        TvViewHolder(View itemView) {
            super(itemView);
            mBackdropImage = itemView.findViewById(R.id.image);
            mTitle = itemView.findViewById(R.id.title);
            mVoteAverage = itemView.findViewById(R.id.voteAverage);
            mFirstAirDate = itemView.findViewById(R.id.first_air_date);
            mOverview = itemView.findViewById(R.id.overview);
            mGenres = itemView.findViewById(R.id.genres);
            mTvDetailBtn = itemView.findViewById(R.id.open_tvBtn);
            mShareTvBtn = itemView.findViewById(R.id.share_btn);

            mTvDetailBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int currentItemPosition = getAdapterPosition();
            onTvClickListener.onTvClick(mTvList.get(currentItemPosition));
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
