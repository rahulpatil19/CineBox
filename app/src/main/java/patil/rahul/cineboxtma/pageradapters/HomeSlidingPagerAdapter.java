package patil.rahul.cineboxtma.pageradapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 21/3/18.
 */

public class HomeSlidingPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Movie> imagePathList;

    public HomeSlidingPagerAdapter(Context context, List<Movie> imagePathList) {
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

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_home_slide, container, false);
        SimpleDraweeView imageView = view.findViewById(R.id.sliding_image);
        TextView titleView = view.findViewById(R.id.sliding_title);
        TextView releaseDate = view.findViewById(R.id.sliding_release_date);

        Movie movie = imagePathList.get(position);
        Uri uri = CineUrl.createImageUri("w780", movie.getBackdropPath());

        imageView.setImageURI(uri);
        titleView.setText(movie.getTitle());
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
