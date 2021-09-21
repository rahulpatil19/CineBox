package patil.rahul.cineboxtma;

import static patil.rahul.cineboxtma.utils.Cine.MovieEntry;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import patil.rahul.cineboxtma.bottomnavfragments.HomeFragment;
import patil.rahul.cineboxtma.bottomnavfragments.MovieFragment;
import patil.rahul.cineboxtma.bottomnavfragments.PeopleFragment;
import patil.rahul.cineboxtma.bottomnavfragments.TvShowFragment;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineListener;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener,
        CineListener.OnPeopleClickListener,
        HomeFragment.OnMoreClickListener,
        CineListener.OnMovieClickListener,
        CineListener.OnTvClickListener,
        BottomNavigationView.OnItemReselectedListener {
    private HomeFragment mHomeFragment;
    private MovieFragment mMovieFragment;
    private PeopleFragment mPeopleFragment;
    private TvShowFragment mTvShowFragment;

    private AppBarLayout mAppBarLayout;
    private boolean doubleBackToExitPressedOnce = false;
    public static final String MEDIA_TYPE = "media_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mAppBarLayout = findViewById(R.id.appbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAppBarLayout.setElevation(4);
        }
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.app_name);
        mBottomNav.setOnItemSelectedListener(this);
        mBottomNav.setOnItemReselectedListener(this);

        if (savedInstanceState == null) {
            mHomeFragment = new HomeFragment();
            mMovieFragment = new MovieFragment();
            mPeopleFragment = new PeopleFragment();
            mTvShowFragment = new TvShowFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment, mHomeFragment).commit();
        }
    }

    protected void displayHomeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mHomeFragment.isAdded()) {
            transaction.show(mHomeFragment);
        } else {
            transaction.add(R.id.fragment, mHomeFragment);
            transaction.addToBackStack(null);
        }
        if (mMovieFragment.isAdded()) {
            transaction.hide(mMovieFragment);
        }
        if (mPeopleFragment.isAdded()) {
            transaction.hide(mPeopleFragment);
        }
        if (mTvShowFragment.isAdded()) {
            transaction.hide(mTvShowFragment);
        }
        transaction.commit();
    }

    protected void displayMovieFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mMovieFragment != null) {
            if (mMovieFragment.isAdded()) {
                transaction.show(mMovieFragment);
            } else {
                transaction.add(R.id.fragment, mMovieFragment);
            }
            if (mHomeFragment.isAdded()) {
                transaction.hide(mHomeFragment);
            }
            if (mPeopleFragment.isAdded()) {
                transaction.hide(mPeopleFragment);
            }
            if (mTvShowFragment.isAdded()) {
                transaction.hide(mTvShowFragment);
            }
            transaction.commit();
        }
    }

    protected void displayPersonFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mPeopleFragment.isAdded()) {
            transaction.show(mPeopleFragment);
        } else {
            transaction.add(R.id.fragment, mPeopleFragment);
        }
        if (mHomeFragment.isAdded()) {
            transaction.hide(mHomeFragment);
        }
        if (mMovieFragment.isAdded()) {
            transaction.hide(mMovieFragment);
        }
        if (mTvShowFragment.isAdded()) {
            transaction.hide(mTvShowFragment);
        }
        transaction.commit();
    }

    protected void displayTvShowFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mTvShowFragment.isAdded()) {
            transaction.show(mTvShowFragment);
        } else {
            transaction.add(R.id.fragment, mTvShowFragment);
        }
        if (mHomeFragment.isAdded()) {
            transaction.hide(mHomeFragment);
        }
        if (mMovieFragment.isAdded()) {
            transaction.hide(mMovieFragment);
        }
        if (mPeopleFragment.isAdded()) {
            transaction.hide(mPeopleFragment);
        }
        transaction.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void tapBackAgainToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getApplicationContext(), R.string.exit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent movieIntent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MovieEntry.TITLE, movie.getTitle());
        bundle.putInt(MovieEntry.ID, movie.getId());
        movieIntent.putExtras(bundle);
        startActivity(movieIntent);
    }

    @Override
    public void onPeopleClick(People people) {
        Intent intent = new Intent(this, PeopleDetailActivity.class);
        intent.putExtra(Cine.PersonEntry.ID, people.getPeopleId());
        intent.putExtra(Cine.PersonEntry.NAME, people.getPeopleName());
        startActivity(intent);
    }

    @Override
    public void onMoreClick(String mediaType) {
        if (mediaType.equals("movie")) {
            Intent intent = new Intent(this, MovieMoreActivity.class);
            intent.putExtra(MEDIA_TYPE, mediaType);
            startActivity(intent);
        } else if (mediaType.equals("tv")) {
            Intent intent = new Intent(this, TvMoreActivity.class);
            intent.putExtra(MEDIA_TYPE, mediaType);
            startActivity(intent);
        }
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        Intent intent = new Intent(this, TvDetailActivity.class);
        intent.putExtra("id", tvShows.getId());
        intent.putExtra("name", tvShows.getName());
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppBarLayout.setElevation(8);
            }
            mAppBarLayout.setExpanded(true);
            displayHomeFragment();
        } else if (itemId == R.id.nav_movie) {
            setTitle(R.string.nav_title_movie);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppBarLayout.setElevation(0);
            }
            mAppBarLayout.setExpanded(true);
            displayMovieFragment();
        } else if (itemId == R.id.nav_tv) {
            setTitle(R.string.nav_title_tvShow);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppBarLayout.setElevation(0);
            }
            mAppBarLayout.setExpanded(true);
            displayTvShowFragment();
        } else if (itemId == R.id.nav_people) {
            setTitle(R.string.title_people);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppBarLayout.setElevation(8);
            }
            mAppBarLayout.setExpanded(true);
            displayPersonFragment();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationItemReselected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_movie) {
            mMovieFragment.refreshFragment();
        } else if (itemId == R.id.nav_tv) {
            mTvShowFragment.refreshFragment();
        } else if (itemId == R.id.nav_people) {
            mPeopleFragment.refreshPeopleList();
        }
    }


}