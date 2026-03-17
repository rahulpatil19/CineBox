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
import patil.rahul.cineboxtma.adapters.PeopleCreditAdapter;
import patil.rahul.cineboxtma.adapters.SeasonAdapter;
import patil.rahul.cineboxtma.adapters.YoutubeListAdapter;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvDetailResponse;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.models.VideoEntry;
import patil.rahul.cineboxtma.pageradapters.SlidingPagerAdapter;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.DeveloperKey;
import patil.rahul.cineboxtma.viewmodels.TvDetailViewModel;

public class TvDetailActivity extends AppCompatActivity implements YoutubeListAdapter.OnYoutubeItemClickListener,
        CineListener.OnPeopleClickListener, CineListener.OnTvClickListener {

    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private String videoKey;
    private boolean mLightModeBox;

    private CircleIndicator mPagerIndicator;
    private SlidingPagerAdapter mSlidingAdapter;
    private ViewPager mSlidingViewPager;
    private ProgressBar mSlidingProgressBar;
    private List<String> slidingList = new ArrayList<>();

    private int mId;
    private String mImdbId;
    private PeopleCreditAdapter mCastAdapter, mCrewAdapter;
    private YoutubeListAdapter mYoutubeAdapter;
    private SeasonAdapter mSeasonAdapter;

    private RecyclerView mCastRecyclerView, mCrewRecyclerView, mSeasonRecyclerView, mVideoRecyclerView;
    private LinearLayout mFullLayout, mCastLayout, mCrewLayout, mVideoLayout, mSeasonLayout, mErrorLayout;
    private ProgressBar mProgressBar;
    private Button mRetryBtn;
    private TextView titleView, firstAirDateView, genresView;
    private ExpandableTextView overView;
    private List<VideoEntry> mVideoList = new ArrayList<>();

    private String imageQuality;
    private TemplateView templateView;
    private TvDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        imageQuality = CinePreferences.getImageQualityValue(this);

        viewModel = new ViewModelProvider(this).get(TvDetailViewModel.class);

        boolean isVideoPrefChecked = CinePreferences.getVideoMode(this);
        mLightModeBox = !isVideoPrefChecked;

        initAds();

        mSlidingViewPager = findViewById(R.id.slidingViewPager);
        mPagerIndicator = findViewById(R.id.pager_indicator);
        mSlidingProgressBar = findViewById(R.id.sliding_progress_bar);

        mFullLayout = findViewById(R.id.full_layout);
        mProgressBar = findViewById(R.id.progress_bar);
        titleView = findViewById(R.id.tv_title);
        firstAirDateView = findViewById(R.id.first_air_date);
        genresView = findViewById(R.id.genres);
        overView = findViewById(R.id.expand_text_view);
        mSeasonLayout = findViewById(R.id.season_layout);
        mCastLayout = findViewById(R.id.cast_layout);
        mCrewLayout = findViewById(R.id.crew_layout);
        mVideoLayout = findViewById(R.id.video_layout);
        mErrorLayout = findViewById(R.id.detail_error_layout);
        mRetryBtn = findViewById(R.id.retry_btn);

        mVideoRecyclerView = findViewById(R.id.video_recycler_view);
        mCastRecyclerView = findViewById(R.id.cast_recycler_view);
        mCrewRecyclerView = findViewById(R.id.crew_recycler_view);
        mSeasonRecyclerView = findViewById(R.id.season_recycler_view);

        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCrewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSeasonRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            mId = intentExtras.getInt("id");
            final String title = intentExtras.getString("name");
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
                        collapsingToolbarLayout.setTitle(title);
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle(" "); 
                        isShow = false;
                    }
                }
            });

            mYoutubeAdapter = new YoutubeListAdapter(this, this);
            mCastAdapter = new PeopleCreditAdapter(this, this);
            mCrewAdapter = new PeopleCreditAdapter(this, this);
            mSeasonAdapter = new SeasonAdapter(this, imageQuality);

            mCastRecyclerView.setAdapter(mCastAdapter);
            mCrewRecyclerView.setAdapter(mCrewAdapter);
            mSeasonRecyclerView.setAdapter(mSeasonAdapter);
            mVideoRecyclerView.setAdapter(mYoutubeAdapter);
            
            fetchTvShowDetail();

            mRetryBtn.setOnClickListener(view -> {
                mErrorLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mSlidingProgressBar.setVisibility(View.VISIBLE);
                fetchTvShowDetail();
            });
        }
    }

    private void initAds() {
        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-9660112888704846/7876629286")
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                            .withCallToActionBackgroundColor(new ColorDrawable(getResources().getColor(R.color.primary)))
                            .withMainBackgroundColor(new ColorDrawable(getResources().getColor(R.color.surfaceColor)))
                            .build();
                    templateView = findViewById(R.id.ad_template_tv);
                    templateView.setStyles(styles);
                    templateView.setNativeAd(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        templateView.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        templateView.setVisibility(View.GONE);
                    }
                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void fetchTvShowDetail() {
        viewModel.getTvShowDetail(mId).observe(this, response -> {
            if (response != null) {
                updateUI(response);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mErrorLayout.setVisibility(View.VISIBLE);
                mSlidingProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateUI(TvDetailResponse response) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mFullLayout.setVisibility(View.VISIBLE);
        firstAirDateView.setText(CineDateFormat.formatDate(response.getFirstAirDate()));
        titleView.setText(response.getName());
        overView.setText(response.getOverview());

        StringBuilder genresBuilder = new StringBuilder();
        if (response.getGenres() != null) {
            for (int i = 0; i < response.getGenres().size(); i++) {
                genresBuilder.append(response.getGenres().get(i).getName());
                if (i < response.getGenres().size() - 1) {
                    genresBuilder.append(", ");
                }
            }
        }
        genresView.setText(genresBuilder.toString());

        slidingList.clear();
        if (response.getImages() != null && response.getImages().getBackdrops() != null && !response.getImages().getBackdrops().isEmpty()) {
            for (TvDetailResponse.Images.Backdrop backdrop : response.getImages().getBackdrops()) {
                slidingList.add(backdrop.getFilePath());
            }
        } else {
            slidingList.add(response.getBackdropPath());
        }

        if (!slidingList.isEmpty()) {
            mSlidingAdapter = new SlidingPagerAdapter(getBaseContext(), slidingList);
            mSlidingViewPager.setAdapter(mSlidingAdapter);
            mPagerIndicator.setViewPager(mSlidingViewPager);
            mSlidingProgressBar.setVisibility(View.INVISIBLE);
            if (slidingList.size() <= 1) {
                mPagerIndicator.setVisibility(View.INVISIBLE);
            }
        }

        if (response.getSeasons() != null && !response.getSeasons().isEmpty()) {
            List<TvShows> seasonList = new ArrayList<>();
            for (TvDetailResponse.Season season : response.getSeasons()) {
                seasonList.add(new TvShows(season.getId(), season.getName(), season.getPosterPath(), season.getAirDate()));
                fetchSeasonVideos(mId, season.getSeasonNumber());
            }
            mSeasonLayout.setVisibility(View.VISIBLE);
            mSeasonAdapter.addData(seasonList);
        } else {
            mSeasonLayout.setVisibility(View.GONE);
        }

        if (response.getExternalIds() != null) {
            mImdbId = response.getExternalIds().getImdbId();
        }

        if (response.getCredits() != null) {
            if (response.getCredits().getCast() != null && !response.getCredits().getCast().isEmpty()) {
                mCastLayout.setVisibility(View.VISIBLE);
                mCastAdapter.addCredits(response.getCredits().getCast());
            } else {
                mCastLayout.setVisibility(View.GONE);
            }

            if (response.getCredits().getCrew() != null && !response.getCredits().getCrew().isEmpty()) {
                mCrewLayout.setVisibility(View.VISIBLE);
                mCrewAdapter.addCredits(response.getCredits().getCrew());
            } else {
                mCrewLayout.setVisibility(View.GONE);
            }
        }

        if (response.getVideos() != null && response.getVideos().getResults() != null && !response.getVideos().getResults().isEmpty()) {
            mVideoList.clear();
            for (TvDetailResponse.VideoResponse.Video video : response.getVideos().getResults()) {
                mVideoList.add(new VideoEntry(video.getName(), video.getKey()));
            }
            mVideoLayout.setVisibility(View.VISIBLE);
            mYoutubeAdapter.addVideoEntries(mVideoList);
        } else {
            mVideoLayout.setVisibility(View.GONE);
        }
    }

    private void fetchSeasonVideos(int tvId, int seasonNumber) {
        viewModel.getSeasonVideos(tvId, seasonNumber).observe(this, response -> {
            if (response != null && response.getVideos() != null && response.getVideos().getResults() != null) {
                for (TvDetailResponse.VideoResponse.Video video : response.getVideos().getResults()) {
                    VideoEntry entry = new VideoEntry(video.getName(), video.getKey());
                    if (!mVideoList.contains(entry)) {
                        mVideoList.add(entry);
                    }
                }
                if (!mVideoList.isEmpty()) {
                    mVideoLayout.setVisibility(View.VISIBLE);
                    mYoutubeAdapter.addVideoEntries(mVideoList);
                }
            }
        });
    }

    @Override
    public void onPeopleClick(People people) {
        Intent intent = new Intent(this, PeopleDetailActivity.class);
        intent.putExtra(Cine.PersonEntry.ID, people.getPeopleId());
        intent.putExtra(Cine.PersonEntry.NAME, people.getPeopleName());
        startActivity(intent);
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        Intent intent = new Intent(this, TvDetailActivity.class);
        intent.putExtra("id", tvShows.getId());
        intent.putExtra("name", tvShows.getName());
        startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tv_detail, menu);
        return true;
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
        } else if (itemId == R.id.action_share) {
            shareTvShow();
        } else if (itemId == R.id.action_view_on_imdb) {
            Uri imdbUri = CineUrl.createExternalWebUri("IMDb", mImdbId);
            Intent imdbIntent = new Intent(Intent.ACTION_VIEW, imdbUri);
            try {
                startActivity(imdbIntent);
            } catch (ActivityNotFoundException e) {
            }
        } else if (itemId == R.id.action_view_on_tmdb) {
            Uri tmdbUri = CineUrl.createTMDbWebUri(mId, "tv");
            Intent tmdbIntent = new Intent(Intent.ACTION_VIEW, tmdbUri);
            try {
                startActivity(tmdbIntent);
            } catch (ActivityNotFoundException e) {
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareTvShow() {
        Uri uri = CineUrl.createTMDbWebUri(mId, "tv");
        String textToShare = String.valueOf(uri);
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Complete action using")
                .setText(textToShare)
                .startChooser();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) {
                    YouTubeInitializationResult errorReason = YouTubeStandalonePlayer.getReturnedInitializationResult(result.getData());
                    if (errorReason != null) {
                        if (errorReason.isUserRecoverableError()) {
                            errorReason.getErrorDialog(TvDetailActivity.this, 0).show();
                        } else {
                            String errorMessage = getString(R.string.error_player) + errorReason.toString();
                            Toast.makeText(TvDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

}
