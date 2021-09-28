package patil.rahul.cineboxtma.bottomnavfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.pageradapters.TvShowPagerAdapter;

public class TvShowFragment extends Fragment {
    public TvShowFragment() {
    }

    private TvShowPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new TvShowPagerAdapter(getParentFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_tvshow, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tv_tabs);
        mViewPager = view.findViewById(R.id.tv_view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        return view;
    }

    public void refreshFragment() {
        mPagerAdapter.refreshTab(mViewPager);
    }
}