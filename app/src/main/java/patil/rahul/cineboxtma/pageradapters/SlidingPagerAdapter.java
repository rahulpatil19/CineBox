package patil.rahul.cineboxtma.pageradapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 15/3/18.
 */

public class SlidingPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> imagePathList;

    public SlidingPagerAdapter(Context context, List<String> imagePathList) {
        this.mContext = context;
        this.imagePathList = imagePathList;
    }

    @Override
    public int getCount() {
        if (imagePathList.size() > 10) {
            return 10;
        } else {
            return imagePathList.size();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_slide, container, false);
        SimpleDraweeView imageView = view.findViewById(R.id.sliding_image);
        String imagePath = imagePathList.get(position);
        Uri uri = CineUrl.createImageUri("w780", imagePath);
        imageView.setImageURI(uri);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
