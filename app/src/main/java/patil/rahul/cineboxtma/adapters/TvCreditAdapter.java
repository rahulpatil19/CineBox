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

public class TvCreditAdapter extends RecyclerView.Adapter<TvCreditAdapter.CreditTvViewHolder> {

    private Context mContext;
    private List<TvShows> tvShowsList;
    private CineListener.OnTvClickListener onTvClickListener;
    private String mImageQuality;

    public TvCreditAdapter(Context context, CineListener.OnTvClickListener onTvClickListener, String imageQuality) {
        mContext = context;
        this.onTvClickListener = onTvClickListener;
        this.mImageQuality = imageQuality;
    }

    @NonNull
    @Override
    public CreditTvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_credit_tv, parent, false);
        return new CreditTvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditTvViewHolder holder, int position) {
        TvShows tvShows = tvShowsList.get(position);
        Uri posterUrl = CineUrl.createImageUri(mImageQuality, tvShows.getPosterPath());
        holder.mTvImage.setImageURI(posterUrl);
        holder.mTvTitle.setText(tvShows.getName());

        if (tvShows.getCharacter().length() > 0){
            if (tvShows.isHasCharacter()){
                holder.mTvCharacter.setVisibility(View.VISIBLE);
                holder.mTvCharacter.setText(String.format("as %s", tvShows.getCharacter()));
            }
            else {
                holder.mTvCharacter.setVisibility(View.VISIBLE);
                holder.mTvCharacter.setText(tvShows.getCharacter());
            }
        }
        else {
            holder.mTvCharacter.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == tvShowsList ? 0 : tvShowsList.size();
    }

    public void addData(List<TvShows> movieList) {
        this.tvShowsList = movieList;
    }

    class CreditTvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mTvImage;
        private TextView mTvTitle;
        private TextView mTvCharacter;

        CreditTvViewHolder(View itemView) {
            super(itemView);
            mTvImage = itemView.findViewById(R.id.tv_poster_image);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvCharacter = itemView.findViewById(R.id.tv_character);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            onTvClickListener.onTvClick(tvShowsList.get(itemPosition));
        }
    }
}
