package patil.rahul.cineboxtma.pageradapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import patil.rahul.cineboxtma.tabfragment.CardMovieTabFragment;
import patil.rahul.cineboxtma.tabfragment.MovieTabFragment;
import patil.rahul.cineboxtma.utils.CineUrl;

/**
 * Created by rahul on 20/2/18.
 */

public class MoviePagerAdapter extends FragmentStatePagerAdapter {

    private CardMovieTabFragment nowShowingFragment = CardMovieTabFragment.newInstance(CineUrl.CATEGORY_NOW_PLAYING);
    private MovieTabFragment popularFragment = MovieTabFragment.newInstance(CineUrl.CATEGORY_POPULAR);
    private MovieTabFragment topRatedFragment = MovieTabFragment.newInstance(CineUrl.CATEGORY_TOP_RATED);

    public MoviePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return nowShowingFragment;
        } else if (position == 1) {
            return popularFragment;
        } else {
            return topRatedFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void refreshTab(ViewPager viewPager) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            nowShowingFragment.refreshMovieFragment();
        } else if (position == 1) {
            popularFragment.refreshMovieFragment();
        } else {
            topRatedFragment.refreshMovieFragment();
        }
    }
}