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
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineUrl;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder> {

    private Context mContext;
    private List<TvShows> mTvShowsList;
    private String imageQuality;
    private CineListener.OnTvClickListener onTvClickListener;

    public SeasonAdapter(Context context, String imageQuality) {
        mContext = context;
        this.imageQuality = imageQuality;
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_horizontal_tv, parent, false);
        return new SeasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder seasonViewHolder, int position) {
        TvShows tvShows = mTvShowsList.get(position);
        seasonViewHolder.mSeasonTitle.setText(tvShows.getName());
        Uri posterUrl = CineUrl.createImageUri(imageQuality, tvShows.getPosterPath());
        seasonViewHolder.mSeasonImage.setImageURI(posterUrl);
        seasonViewHolder.mSeasonAirDate.setVisibility(View.VISIBLE);
        seasonViewHolder.mSeasonAirDate.setText(CineDateFormat.formatDate(tvShows.getFirstAirDate()));

    }

    @Override
    public int getItemCount() {
        return null == mTvShowsList ? 0 : mTvShowsList.size();
    }

    public void addData(List<TvShows> movieList) {
        this.mTvShowsList = movieList;
        notifyDataSetChanged();
    }

    class SeasonViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView mSeasonImage;
        private TextView mSeasonTitle;
        private TextView mSeasonAirDate;

        SeasonViewHolder(View itemView) {
            super(itemView);
            mSeasonImage = itemView.findViewById(R.id.tv_horizontal_image);
            mSeasonTitle = itemView.findViewById(R.id.tv_horizontal_title);
            mSeasonAirDate = itemView.findViewById(R.id.tv_horizontal_date);
        }

    }
}
