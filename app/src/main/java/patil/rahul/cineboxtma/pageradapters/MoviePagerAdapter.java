package patil.rahul.cineboxtma.pageradapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import patil.rahul.cineboxtma.tabfragment.movie.InCinemasMovieFragment;
import patil.rahul.cineboxtma.tabfragment.movie.PopularMovieFragment;
import patil.rahul.cineboxtma.tabfragment.movie.TopRatedMovieFragment;

/**
 * Created by rahul on 20/2/18.
 */

public class MoviePagerAdapter extends FragmentStatePagerAdapter {

    private InCinemasMovieFragment nowShowingFragment = new InCinemasMovieFragment();
    private PopularMovieFragment popularFragment = new PopularMovieFragment();
    private TopRatedMovieFragment topRatedFragment = new TopRatedMovieFragment();

    public MoviePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return popularFragment ;
        } else if (position == 1) {
            return nowShowingFragment;
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
            popularFragment.refreshMovieFragment();
        } else if (position == 1) {
            nowShowingFragment.refreshMovieFragment();
        } else {
            topRatedFragment.refreshMovieFragment();
        }
    }
}