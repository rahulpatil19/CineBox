package patil.rahul.cineboxtma;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import patil.rahul.cineboxtma.adapters.MovieHorizontalAdapter;
import patil.rahul.cineboxtma.adapters.PeopleCreditAdapter;
import patil.rahul.cineboxtma.adapters.YoutubeListAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.MovieDetailResponse;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.VideoEntry;
import patil.rahul.cineboxtma.pageradapters.SlidingPagerAdapter;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.DeveloperKey;
import patil.rahul.cineboxtma.viewmodels.MovieDetailViewModel;

public class MovieDetailActivity extends AppCompatActivity implements YoutubeListAdapter.OnYoutubeItemClickListener,
        CineListener.OnPeopleClickListener, CineListener.OnMovieClickListener {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private int mId;
    private String mTitle;
    private String videoKey;
    private boolean mLightModeBox;

    private MovieDetailViewModel mViewModel;
    private YoutubeListAdapter mYoutubeAdapter;
    private PeopleCreditAdapter mCastAdapter, mCrewAdapter;
    private MovieHorizontalAdapter mSimilarAdapter;
    private SlidingPagerAdapter mSlidingPagerAdapter;

    private TextView mTitleTextView, mReleaseDateTextView, mRuntimeTextView, mRatingTextView, mVoteCountTextView, mGenreTextView, mTaglineTextView, mDirectorTextView;
    private ExpandableTextView mOverviewTextView;
    private ProgressBar mProgressBar, mSlidingProgressBar;
    private LinearLayout mDetailLayout, mVideoLayout, mCastLayout, mCrewLayout, mSimilarMoviesLayout, mRatingLayout;
    private ViewPager mViewPager;
    private CircleIndicator mIndicator;
    private String mImdbId;
    private TemplateView mAdTemplateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Cine.MovieEntry.ID)) {
            mId = intent.getIntExtra(Cine.MovieEntry.ID, 0);
            mTitle = intent.getStringExtra(Cine.MovieEntry.TITLE);
        }

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(mTitle);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        mTitleTextView = findViewById(R.id.movie_title);
        mReleaseDateTextView = findViewById(R.id.movie_release_date);
        mRuntimeTextView = findViewById(R.id.movie_runtime);
        mRatingTextView = findViewById(R.id.movie_voteAverage);
        mVoteCountTextView = findViewById(R.id.movie_voteCount);
        mGenreTextView = findViewById(R.id.movie_genres);
        mTaglineTextView = findViewById(R.id.tv_detail_tagline);
        mDirectorTextView = findViewById(R.id.movie_director);
        mOverviewTextView = findViewById(R.id.expand_text_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mSlidingProgressBar = findViewById(R.id.sliding_progress_bar);
        mDetailLayout = findViewById(R.id.main_layout);
        mRatingLayout = findViewById(R.id.rating_view);
        mVideoLayout = findViewById(R.id.video_layout);
        mCastLayout = findViewById(R.id.cast_layout);
        mCrewLayout = findViewById(R.id.crew_layout);
        mSimilarMoviesLayout = findViewById(R.id.similar_movies_layout);
        mViewPager = findViewById(R.id.slidingViewPager);
        mIndicator = findViewById(R.id.pager_indicator);
        mAdTemplateView = findViewById(R.id.ad_template_movie);

        RecyclerView youtubeRecyclerView = findViewById(R.id.video_recycler_view);
        youtubeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mYoutubeAdapter = new YoutubeListAdapter(this, this);
        youtubeRecyclerView.setAdapter(mYoutubeAdapter);

        RecyclerView castRecyclerView = findViewById(R.id.cast_recycler_view);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCastAdapter = new PeopleCreditAdapter(this, this);
        castRecyclerView.setAdapter(mCastAdapter);

        RecyclerView crewRecyclerView = findViewById(R.id.crew_recycler_view);
        crewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCrewAdapter = new PeopleCreditAdapter(this, this);
        crewRecyclerView.setAdapter(mCrewAdapter);

        RecyclerView similarRecyclerView = findViewById(R.id.similar_movies_recycler_view);
        similarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSimilarAdapter = new MovieHorizontalAdapter(this, this, CinePreferences.getImageQualityValue(this));
        similarRecyclerView.setAdapter(mSimilarAdapter);

        mLightModeBox = !CinePreferences.getVideoMode(this);

        mViewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        mViewModel.getMovieDetail(mId).observe(this, this::setupUI);

        loadNativeAd();
    }

    private void setupUI(MovieDetailResponse movieDetail) {
        mProgressBar.setVisibility(View.GONE);
        if (movieDetail != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
            mTitleTextView.setText(movieDetail.getTitle());
            mReleaseDateTextView.setText(CineDateFormat.formatDate(movieDetail.getReleaseDate()));
            mReleaseDateTextView.setVisibility(View.VISIBLE);

            if (movieDetail.getRuntime() != null && !movieDetail.getRuntime().equals("null")) {
                mRuntimeTextView.setText(String.format("%s min", movieDetail.getRuntime()));
            } else {
                mRuntimeTextView.setText(R.string.no_runtime_min);
            }

            if (movieDetail.getVoteCount() > 10) {
                mRatingTextView.setText(String.valueOf(movieDetail.getVoteAverage()));
                mVoteCountTextView.setText(String.format("%s votes", movieDetail.getVoteCount()));
            } else {
                mRatingLayout.setVisibility(View.GONE);
            }

            StringBuilder genres = new StringBuilder();
            if (movieDetail.getGenres() != null) {
                for (int i = 0; i < movieDetail.getGenres().size(); i++) {
                    genres.append(movieDetail.getGenres().get(i).getName());
                    if (i != movieDetail.getGenres().size() - 1) {
                        genres.append(", ");
                    }
                }
            }
            mGenreTextView.setText(genres.toString());
            mTaglineTextView.setText(movieDetail.getTagline());
            mOverviewTextView.setText(movieDetail.getOverview());

            if (movieDetail.getExternalIds() != null) {
                mImdbId = movieDetail.getExternalIds().getImdbId();
            }

            if (movieDetail.getVideos() != null && movieDetail.getVideos().getResults() != null && !movieDetail.getVideos().getResults().isEmpty()) {
                mVideoLayout.setVisibility(View.VISIBLE);
                List<VideoEntry> videoEntries = new ArrayList<>();
                for (MovieDetailResponse.VideoResponse.Video video : movieDetail.getVideos().getResults()) {
                    videoEntries.add(new VideoEntry(video.getName(), video.getKey()));
                }
                mYoutubeAdapter.addVideoEntries(videoEntries);
            } else {
                mVideoLayout.setVisibility(View.GONE);
            }

            if (movieDetail.getCredits() != null) {
                if (movieDetail.getCredits().getCast() != null && !movieDetail.getCredits().getCast().isEmpty()) {
                    mCastLayout.setVisibility(View.VISIBLE);
                    mCastAdapter.addCredits(movieDetail.getCredits().getCast());
                } else {
                    mCastLayout.setVisibility(View.GONE);
                }

                if (movieDetail.getCredits().getCrew() != null && !movieDetail.getCredits().getCrew().isEmpty()) {
                    mCrewLayout.setVisibility(View.VISIBLE);
                    mCrewAdapter.addCredits(movieDetail.getCredits().getCrew());
                    for (People person : movieDetail.getCredits().getCrew()) {
                        if ("Director".equals(person.getPeopleCharacter())) {
                            mDirectorTextView.setText(person.getPeopleName());
                            break;
                        }
                    }
                } else {
                    mCrewLayout.setVisibility(View.GONE);
                }
            }

            if (movieDetail.getSimilarMovies() != null && movieDetail.getSimilarMovies().getResults() != null && !movieDetail.getSimilarMovies().getResults().isEmpty()) {
                mSimilarMoviesLayout.setVisibility(View.VISIBLE);
                mSimilarAdapter.addData(movieDetail.getSimilarMovies().getResults());
            } else {
                mSimilarMoviesLayout.setVisibility(View.GONE);
            }

            List<String> images = new ArrayList<>();
            if (movieDetail.getImages() != null && movieDetail.getImages().getBackdrops() != null && !movieDetail.getImages().getBackdrops().isEmpty()) {
                for (MovieDetailResponse.Images.Backdrop backdrop : movieDetail.getImages().getBackdrops()) {
                    images.add(backdrop.getFilePath());
                }
            } else {
                images.add(movieDetail.getBackdropPath());
            }

            mSlidingPagerAdapter = new SlidingPagerAdapter(this, images);
            mViewPager.setAdapter(mSlidingPagerAdapter);
            mIndicator.setViewPager(mViewPager);
            mSlidingProgressBar.setVisibility(View.INVISIBLE);
            if (images.size() <= 1) {
                mIndicator.setVisibility(View.INVISIBLE);
            }
        } else {
            findViewById(R.id.detail_error_layout).setVisibility(View.VISIBLE);
            Button retryButton = findViewById(R.id.retry_btn);
            retryButton.setOnClickListener(v -> {
                findViewById(R.id.detail_error_layout).setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mSlidingProgressBar.setVisibility(View.VISIBLE);
                mViewModel.getMovieDetail(mId).observe(this, this::setupUI);
            });
        }
    }

    private void loadNativeAd() {
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.native_ad_unit_id))
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                            .withCallToActionBackgroundColor(new ColorDrawable(getResources().getColor(R.color.primary)))
                            .withMainBackgroundColor(new ColorDrawable(getResources().getColor(R.color.surfaceColor)))
                            .build();
                    mAdTemplateView.setStyles(styles);
                    mAdTemplateView.setNativeAd(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        mAdTemplateView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mAdTemplateView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        mAdTemplateView.setVisibility(View.GONE);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_share) {
            shareMovie();
            return true;
        } else if (id == R.id.action_go_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_view_on_imdb) {
            if (mImdbId != null) {
                Uri imdbUri = CineUrl.createExternalWebUri("IMDb", mImdbId);
                Intent imdbIntent = new Intent(Intent.ACTION_VIEW, imdbUri);
                try {
                    startActivity(imdbIntent);
                } catch (ActivityNotFoundException e) {
                }
            }
            return true;
        } else if (id == R.id.action_view_on_tmdb) {
            Uri tmdbUri = CineUrl.createTMDbWebUri(mId, "movie");
            Intent tmdbIntent = new Intent(Intent.ACTION_VIEW, tmdbUri);
            try {
                startActivity(tmdbIntent);
            } catch (ActivityNotFoundException e) {
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareMovie() {
        Uri uri = CineUrl.createTMDbWebUri(mId, "movie");
        String textToShare = String.valueOf(uri);
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Complete action using")
                .setText(textToShare)
                .startChooser();
    }

    @Override
    public void onVideoClick(VideoEntry videoEntry) {
        videoKey = videoEntry.getVideoId();
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, DeveloperKey.DEVELOPER_KEY, videoKey, 0, true, mLightModeBox);
        if (intent != null) {
            try {
                someActivityResultLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                playVideoInYouTubeAppOrBrowser(videoKey);
            }
        } else {
            YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
        }
    }

    private void playVideoInYouTubeAppOrBrowser(String videoKey) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            try {
                startActivity(webIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application can handle this request.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPeopleClick(People people) {
        Intent intent = new Intent(this, PeopleDetailActivity.class);
        intent.putExtra(Cine.PersonEntry.ID, people.getPeopleId());
        intent.putExtra(Cine.PersonEntry.NAME, people.getPeopleName());
        startActivity(intent);
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

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) {
                    YouTubeInitializationResult errorReason = YouTubeStandalonePlayer.getReturnedInitializationResult(result.getData());
                    if (errorReason != null) {
                        if (errorReason.isUserRecoverableError()) {
                            errorReason.getErrorDialog(MovieDetailActivity.this, 0).show();
                        } else {
                            String errorMessage = getString(R.string.error_player) + errorReason.toString();
                            Toast.makeText(MovieDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
}
