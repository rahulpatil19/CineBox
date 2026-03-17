package patil.rahul.cineboxtma;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import patil.rahul.cineboxtma.adapters.MovieCreditAdapter;
import patil.rahul.cineboxtma.adapters.PeopleImageAdapter;
import patil.rahul.cineboxtma.adapters.TvCreditAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.PeopleDetailResponse;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.pageradapters.SlidingPagerAdapter;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.viewmodels.PeopleDetailViewModel;

public class PeopleDetailActivity extends AppCompatActivity implements CineListener.OnMovieClickListener,
        CineListener.OnTvClickListener {

    private CircleIndicator mPagerIndicator;
    private SlidingPagerAdapter mSlidingPagerAdapter;
    private ViewPager mSlidingViewPager;
    private ProgressBar mSlidingProgressBar;
    private RelativeLayout mTitleLayout;
    private LinearLayout mMovieCreditsLayout, mTvCreditsLayout, mErrorLayout, mImageLayout;
    private Button mRetryBtn;
    private TextView mPeopleNameTextView, mBirthDateTextView, mBirthPlaceTextView, mAgeTextView;
    private ExpandableTextView mBiographyTextView;
    private RecyclerView mMovieCreditsRecyclerView, mTvCreditsRecyclerView, mImageRecyclerView;
    private ProgressBar mProgressBar;
    private SimpleDraweeView mPeopleImageView;
    private MovieCreditAdapter mMovieCreditAdapter;
    private TvCreditAdapter mTvCreditAdapter;
    private PeopleImageAdapter mImageAdapter;

    private List<Movie> movieCreditList = new ArrayList<>();
    private List<TvShows> tvCreditList = new ArrayList<>();
    private List<String> mSidingList = new ArrayList<>();
    private List<String> mImageList = new ArrayList<>();

    private int personId;
    private String mIMDbId;
    private String mFacebookId;
    private String mInstagramId;
    private String mTwitterId;
    private PeopleDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        viewModel = new ViewModelProvider(this).get(PeopleDetailViewModel.class);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        setupViews();
        String imageQuality = CinePreferences.getImageQualityValue(this);

        Intent intent = getIntent();
        if (intent != null) {
            personId = intent.getExtras().getInt(Cine.PersonEntry.ID);
            final String personName = intent.getExtras().getString(Cine.PersonEntry.NAME);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = true;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle(personName);
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle(" "); 
                        isShow = false;
                    }
                }
            });

            mMovieCreditAdapter = new MovieCreditAdapter(this, this, imageQuality);
            mTvCreditAdapter = new TvCreditAdapter(this, this, imageQuality);
            mImageAdapter = new PeopleImageAdapter(this);
            mMovieCreditsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
            mTvCreditsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                    false));
            mImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            mMovieCreditsRecyclerView.setAdapter(mMovieCreditAdapter);
            mTvCreditsRecyclerView.setAdapter(mTvCreditAdapter);
            mImageRecyclerView.setAdapter(mImageAdapter);

            fetchPersonDetail();

            mRetryBtn.setOnClickListener(view -> {
                mErrorLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                fetchPersonDetail();
            });
        }
    }


    private void fetchPersonDetail() {
        viewModel.getPersonDetail(personId).observe(this, response -> {
            if (response != null) {
                updateUI(response);
            } else {
                mSlidingProgressBar.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateUI(PeopleDetailResponse response) {
        mPeopleImageView.setImageURI(CineUrl.createImageUri("w185", response.getProfilePath()));
        mProgressBar.setVisibility(View.INVISIBLE);
        mTitleLayout.setVisibility(View.VISIBLE);
        mPeopleNameTextView.setText(response.getName());
        mBiographyTextView.setText(response.getBiography());

        if (response.getBirthday() != null) {
            mBirthDateTextView.setText(CineDateFormat.formatDate(response.getBirthday()));
            mAgeTextView.setText(CineDateFormat.calculateAge(response.getBirthday()));
        } else {
            mBirthDateTextView.setText("N/A");
            mAgeTextView.setText("N/A");
        }

        if (response.getPlaceOfBirth() != null && !response.getPlaceOfBirth().equals("null")) {
            mBirthPlaceTextView.setText(response.getPlaceOfBirth());
        } else {
            mBirthPlaceTextView.setVisibility(View.INVISIBLE);
        }

        if (response.getExternalIds() != null) {
            mIMDbId = response.getExternalIds().getImdbId();
            mInstagramId = response.getExternalIds().getInstagramId();
            mTwitterId = response.getExternalIds().getTwitterId();
            mFacebookId = response.getExternalIds().getFacebookId();
        }

        movieCreditList.clear();
        tvCreditList.clear();
        mSidingList.clear();

        if (response.getCombinedCredits() != null) {
            processCredits(response.getCombinedCredits().getCast(), true);
            processCredits(response.getCombinedCredits().getCrew(), false);
        }

        if (!movieCreditList.isEmpty()) {
            mMovieCreditsLayout.setVisibility(View.VISIBLE);
            mMovieCreditAdapter.addData(movieCreditList);
            mMovieCreditAdapter.notifyDataSetChanged();
        }

        if (!tvCreditList.isEmpty()) {
            mTvCreditsLayout.setVisibility(View.VISIBLE);
            mTvCreditAdapter.addData(tvCreditList);
            mTvCreditAdapter.notifyDataSetChanged();
        }

        if (!mSidingList.isEmpty()) {
            mSlidingPagerAdapter = new SlidingPagerAdapter(getApplicationContext(), mSidingList);
            mSlidingViewPager.setAdapter(mSlidingPagerAdapter);
            mPagerIndicator.setViewPager(mSlidingViewPager);
            mSlidingProgressBar.setVisibility(View.INVISIBLE);
            if (mSidingList.size() <= 1) {
                mPagerIndicator.setVisibility(View.INVISIBLE);
            }
        } else {
            mSidingList.add(response.getProfilePath());
            mSlidingPagerAdapter = new SlidingPagerAdapter(getApplicationContext(), mSidingList);
            mSlidingViewPager.setAdapter(mSlidingPagerAdapter);
            mPagerIndicator.setViewPager(mSlidingViewPager);
            mSlidingProgressBar.setVisibility(View.INVISIBLE);
            mPagerIndicator.setVisibility(View.INVISIBLE);
        }

        mImageList.clear();
        if (response.getImages() != null && response.getImages().getProfiles() != null) {
            for (PeopleDetailResponse.Images.Profile profile : response.getImages().getProfiles()) {
                mImageList.add(profile.getFilePath());
            }
        }

        if (!mImageList.isEmpty()) {
            mImageLayout.setVisibility(View.VISIBLE);
            mImageAdapter.addImages(mImageList);
            mImageAdapter.notifyDataSetChanged();
        } else {
            mImageLayout.setVisibility(View.GONE);
        }
    }

    private void processCredits(List<PeopleDetailResponse.Credit> credits, boolean isCast) {
        if (credits == null) return;
        for (PeopleDetailResponse.Credit credit : credits) {
            if ("movie".equals(credit.getMediaType())) {
                mSidingList.add(credit.getBackdropPath());
                movieCreditList.add(new Movie(credit.getId(), credit.getTitle(), credit.getPosterPath(), 
                        credit.getReleaseDate() != null ? credit.getReleaseDate() : "-", 
                        isCast ? credit.getCharacter() : credit.getJob(), isCast));
            } else if ("tv".equals(credit.getMediaType())) {
                mSidingList.add(credit.getBackdropPath());
                tvCreditList.add(new TvShows(credit.getId(), credit.getName(), credit.getPosterPath(), 
                        credit.getFirstAirDate(), isCast ? credit.getCharacter() : credit.getJob(), isCast));
            }
        }
    }

    private void setupViews() {
        mSlidingViewPager = findViewById(R.id.slidingViewPager);
        mPeopleImageView = findViewById(R.id.people_image);
        mPagerIndicator = findViewById(R.id.pager_indicator);
        mSlidingProgressBar = findViewById(R.id.sliding_progress_bar);

        mTitleLayout = findViewById(R.id.title_layout);
        mMovieCreditsLayout = findViewById(R.id.movie_credits_layout);
        mTvCreditsLayout = findViewById(R.id.tv_credits_layout);
        mImageLayout = findViewById(R.id.image_layout);
        mErrorLayout = findViewById(R.id.detail_error_layout);

        mProgressBar = findViewById(R.id.progress_bar);
        mRetryBtn = findViewById(R.id.retry_btn);
        mPeopleNameTextView = findViewById(R.id.people_name);
        mBirthDateTextView = findViewById(R.id.birth_date);
        mBirthPlaceTextView = findViewById(R.id.birth_place);
        mAgeTextView = findViewById(R.id.age);
        mBiographyTextView = findViewById(R.id.expand_text_view);

        mMovieCreditsRecyclerView = findViewById(R.id.movie_credits_recycler_view);
        mTvCreditsRecyclerView = findViewById(R.id.tv_credits_recycler_view);
        mImageRecyclerView = findViewById(R.id.image_recycler_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_people_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.action_go_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (itemId == R.id.action_view_on_imdb) {
            Uri imdbUri = CineUrl.createIMDbPersonUri(mIMDbId);
            Intent imdbIntent = new Intent(Intent.ACTION_VIEW, imdbUri);
            try {
                startActivity(imdbIntent);
            } catch (ActivityNotFoundException e) {
            }
        } else if (itemId == R.id.action_view_facebook) {
            Uri facebookUri = CineUrl.createExternalWebUri("facebook", mFacebookId);
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebookUri);
            try {
                startActivity(facebookIntent);
            } catch (ActivityNotFoundException e) {
            }
        } else if (itemId == R.id.action_view_instagram) {
            Uri instagramUri = CineUrl.createExternalWebUri("instagram", mInstagramId);
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, instagramUri);
            try {
                startActivity(instagramIntent);
            } catch (ActivityNotFoundException e) {
            }
        } else if (itemId == R.id.action_view_twitter) {
            Uri twitterUri = CineUrl.createExternalWebUri("twitter", mTwitterId);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, twitterUri);
            try {
                startActivity(twitterIntent);
            } catch (ActivityNotFoundException e) {
            }
        }
        return super.onOptionsItemSelected(item);
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
    public void onTvClick(TvShows tvShows) {
        Intent intent = new Intent(this, TvDetailActivity.class);
        intent.putExtra("id", tvShows.getId());
        intent.putExtra("name", tvShows.getName());
        startActivity(intent);
    }
}
