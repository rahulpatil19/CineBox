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
import patil.rahul.cineboxtma.models.People;

/**
 * Created by rahul on 1/2/18.
 */

public class PeopleAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;

    private List<People> mPeopleList;
    private Context mContext;
    private CineListener.OnPeopleClickListener onPeopleClickListener;


    public PeopleAdapter(Context context, CineListener.OnPeopleClickListener onPeopleItemClickListener) {
        mContext = context;
        this.onPeopleClickListener = onPeopleItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_ITEM) {
            return new PeopleViewHolder(inflater.inflate(R.layout.item_people, parent, false));
        } else {
            return new LoadingViewHolder(inflater.inflate(R.layout.item_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        People people = mPeopleList.get(position);

        if (viewHolder instanceof PeopleViewHolder) {
            PeopleViewHolder holder = (PeopleViewHolder) viewHolder;
            String imageUrl = "https://image.tmdb.org/t/p/w185" + people.getPeopleImage();
            Uri uri = Uri.parse(imageUrl);
            holder.mPeopleImage.setImageURI(uri);

            holder.mPeopleName.setText(people.getPeopleName());
        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder holder = (LoadingViewHolder) viewHolder;
            holder.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mPeopleList == null ? 0 : mPeopleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPeopleList.get(position) == null ? TYPE_LOADING : TYPE_ITEM;
    }

    public void addData(List<People> peopleList) {
        mPeopleList = peopleList;
    }

    public void clearAdapter() {
        mPeopleList.clear();
        notifyDataSetChanged();
    }

    class PeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mPeopleImage;
        private TextView mPeopleName;

        PeopleViewHolder(View itemView) {
            super(itemView);

            mPeopleName = itemView.findViewById(R.id.people_name);
            mPeopleImage = itemView.findViewById(R.id.people_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onPeopleClickListener.onPeopleClick(mPeopleList.get(position));
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
