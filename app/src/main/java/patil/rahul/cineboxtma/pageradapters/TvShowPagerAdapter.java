package patil.rahul.cineboxtma.pageradapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import patil.rahul.cineboxtma.tabfragment.TvTabFragment;

public class TvShowPagerAdapter extends FragmentStatePagerAdapter {

    private TvTabFragment popularTabFragment = TvTabFragment.newInstance("popular");
    private TvTabFragment topRatedFragment = TvTabFragment.newInstance("top_rated");

    public TvShowPagerAdapter(FragmentManager fm) {
        super(fm);
    }

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
