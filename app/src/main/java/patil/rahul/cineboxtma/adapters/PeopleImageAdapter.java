package patil.rahul.cineboxtma.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineUrl;

public class PeopleImageAdapter extends RecyclerView.Adapter<PeopleImageAdapter.ImageViewHolder> {

    private List<String> mImageList;
    private Context mContext;

    public PeopleImageAdapter(Context context){
        this.mContext = context;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int position) {
        String s = mImageList.get(position);
        Uri uri = CineUrl.createImageUri("w500" , s);
        imageViewHolder.mImageView.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    public void addImages(List<String> imageList){
        this.mImageList = imageList;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView mImageView;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.person_images);
        }
    }
}
