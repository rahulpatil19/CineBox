package patil.rahul.cineboxtma;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.tabfragment.SearchFragment;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.MySingleton;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        CineListener.OnMovieClickListener, CineListener.OnPeopleClickListener, CineListener.OnTvClickListener {

    public static final int SEARCH_TYPE_MOVIE = 1;
    public static final int SEARCH_TYPE_PEOPLE = 2;
    public static final int SEARCH_TYPE_TV_SHOW = 3;
    private SearchView mSearchView;
    private ViewPager mViewPager;
    private SearchPagerAdapter searchPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchView = findViewById(R.id.search_view);
        TabLayout mTabLayout = findViewById(R.id.search_tabLayout);
        mViewPager = findViewById(R.id.search_viewPager);

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.movie_search_hint));
        searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(searchPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    mSearchView.setQueryHint(getString(R.string.movie_search_hint));
                } else if (position == 1) {
                    mSearchView.setQueryHint(getString(R.string.people_search_hint));
                } else {
                    mSearchView.setQueryHint(getString(R.string.tv_show_search_hint));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySingleton.getInstance(this).getRequestQueue().cancelAll(CineTag.SEARCH_MOVIE_TAG);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchPagerAdapter.setQuery(query);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent movieIntent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Cine.MovieEntry.TITLE, movie.getTitle());
        bundle.putInt(Cine.MovieEntry.ID, movie.getId());
        movieIntent.putExtras(bundle);
        startActivity(movieIntent);
    }

    @Override
    public void onPeopleClick(People people) {
        int peopleId = people.getPeopleId();
        String peopleName = people.getPeopleName();
        Intent intent = new Intent(this, PeopleDetailActivity.class);
        intent.putExtra(Cine.PersonEntry.ID, peopleId);
        intent.putExtra(Cine.PersonEntry.NAME, peopleName);
        startActivity(intent);
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        Intent intent = new Intent(this, TvDetailActivity.class);
        intent.putExtra("id", tvShows.getId());
        intent.putExtra("name", tvShows.getName());
        startActivity(intent);
    }

    class SearchPagerAdapter extends FragmentPagerAdapter {
        private SearchFragment peopleSearchFragment;
        private SearchFragment tvShowSearchFragment;
        private SearchFragment movieSearchFragment;

        SearchPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return movieSearchFragment = SearchFragment.newInstance(SEARCH_TYPE_MOVIE);
            } else if (position == 1) {
                return peopleSearchFragment = SearchFragment.newInstance(SEARCH_TYPE_PEOPLE);
            } else {
                return tvShowSearchFragment = SearchFragment.newInstance(SEARCH_TYPE_TV_SHOW);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        private void setQuery(String query) {
            int position = mViewPager.getCurrentItem();
            if (position == 0) {
                movieSearchFragment.clearMovieAdapter();
                movieSearchFragment.searchMovie(query);
            } else if (position == 1) {
                peopleSearchFragment.clearPeopleAdapter();
                peopleSearchFragment.searchPeople(query);
            } else if (position == 2) {
                tvShowSearchFragment.clearTvAdapter();
                tvShowSearchFragment.searchTvShow(query);
            }
        }
    }
}