package patil.rahul.cineboxtma.bottomnavfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.pageradapters.MoviePagerAdapter;

/**
 * Created by rahul on 1/2/18.
 */

public class MovieFragment extends Fragment {
    public MovieFragment() {
    }

    private ViewPager mViewPager;
    private MoviePagerAdapter mPagerAdapter;
    private AppBarLayout mAppBarLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new MoviePagerAdapter(getFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_movie, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        mAppBarLayout = view.findViewById(R.id.appbar);
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        return view;
    }

    public void refreshFragment() {
        mPagerAdapter.refreshTab(mViewPager);
    }
}