package patil.rahul.cineboxtma.pageradapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import patil.rahul.cineboxtma.tabfragment.tv.PopularTvFragment;
import patil.rahul.cineboxtma.tabfragment.tv.TopRatedTvFragment;

public class TvShowPagerAdapter extends FragmentStatePagerAdapter {

    private PopularTvFragment popularTabFragment = new PopularTvFragment();
    private TopRatedTvFragment topRatedFragment = new TopRatedTvFragment();

    public TvShowPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return popularTabFragment;
        } else {
            return topRatedFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void refreshTab(ViewPager viewPager) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            popularTabFragment.refreshTvList();
        } else if (position == 1) {
            topRatedFragment.refreshTvList();
        }
    }
}
