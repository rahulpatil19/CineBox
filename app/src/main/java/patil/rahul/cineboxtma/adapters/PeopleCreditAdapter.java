package patil.rahul.cineboxtma.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 27/2/18.
 */

public class PeopleCreditAdapter extends RecyclerView.Adapter<PeopleCreditAdapter.CreditViewHolder> {

    private CineListener.OnPeopleClickListener onPeopleClickListener;
    private List<People> mCreditList;
    private Context mContext;


    public PeopleCreditAdapter(Context context, CineListener.OnPeopleClickListener onPeopleClickListener){
        this.onPeopleClickListener = onPeopleClickListener;
        mContext = context;
    }

    @Override
    public CreditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_credit_people, parent, false);
        return new CreditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditViewHolder holder, int position) {
     People people = mCreditList.get(position);

        Uri uri = CineUrl.createImageUri("w185", people.getPeopleImage());

        holder.mImageView.setImageURI(uri);
        holder.mNameView.setText(people.getPeopleName());

        if (people.isCharacter()){
            if (people.getPeopleCharacter().length() > 0) {
                holder.mCharacterView.setVisibility(View.VISIBLE);
                holder.mCharacterView.setText(String.format("as %s", people.getPeopleCharacter()));
            }
            else {
                holder.mCharacterView.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.mCharacterView.setVisibility(View.VISIBLE);
            holder.mCharacterView.setText(people.getPeopleCharacter());
        }

    }

    @Override
    public int getItemCount() {
        if (null == mCreditList) return 0;
        return mCreditList.size();
    }

    public void addCredits(List<People> creditList){
        if (creditList != null){
            mCreditList = creditList;
            notifyDataSetChanged();
        }
    }

    class CreditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private SimpleDraweeView mImageView;
        private TextView mNameView;
        private TextView mCharacterView;

        CreditViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.people_image);
            mNameView = itemView.findViewById(R.id.people_name);
            mCharacterView = itemView.findViewById(R.id.people_character);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onPeopleClickListener.onPeopleClick(mCreditList.get(position));
        }
    }
}