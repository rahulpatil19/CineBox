package patil.rahul.cineboxtma;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import me.relex.circleindicator.CircleIndicator;
import patil.rahul.cineboxtma.adapters.PeopleCreditAdapter;
import patil.rahul.cineboxtma.adapters.SeasonAdapter;
import patil.rahul.cineboxtma.adapters.YoutubeListAdapter;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.models.VideoEntry;
import patil.rahul.cineboxtma.pageradapters.SlidingPagerAdapter;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.DeveloperKey;
import patil.rahul.cineboxtma.utils.MySingleton;

public class TvDetailActivity extends AppCompatActivity implements YoutubeListAdapter.OnYoutubeItemClickListener,
        CineListener.OnPeopleClickListener, CineListener.OnTvClickListener {

    private static final int REQ_START_STANDALONE_PLAYER = 1;
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
    private TextView titleView, firstAirDateView, genresView, runtimeView, episodeCountView, seasonCountView,
            voteAverageView, voteCountView, createdByView;
    private ExpandableTextView overView;
    private List<VideoEntry> mVideoList = new ArrayList<>();
    private List<TvShows> mSeasonList = new ArrayList<>();

    private String imageQuality;
    private List<People> mCastList = new ArrayList<>();
    private List<People> mCrewList = new ArrayList<>();

    private TemplateView templateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        imageQuality = CinePreferences.getImageQualityValue(this);

        boolean isVideoPrefChecked = CinePreferences.getVideoMode(this);

        mLightModeBox = !isVideoPrefChecked;

        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-9660112888704846/7876629286")
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder()
                            .withCallToActionBackgroundColor(new ColorDrawable(getResources().getColor(R.color.primary)))
                            .withMainBackgroundColor(new ColorDrawable(getResources().getColor(R.color.surfaceColor)))
                            .build();
                    templateView = findViewById(R.id.ad_template_tv);
                    templateView.setStyles(styles);
                    templateView.setNativeAd(nativeAd);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        templateView.setVisibility(View.GONE);
                    }

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

        mSeasonRecyclerView.setHasFixedSize(true);
        mVideoRecyclerView.setHasFixedSize(true);
        mCastRecyclerView.setHasFixedSize(true);
        mCrewRecyclerView.setHasFixedSize(true);
        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCrewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSeasonRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mCastRecyclerView.setNestedScrollingEnabled(false);
        mCrewRecyclerView.setNestedScrollingEnabled(false);
        mVideoRecyclerView.setNestedScrollingEnabled(false);
        mSeasonRecyclerView.setNestedScrollingEnabled(false);

        final Bundle intent = getIntent().getExtras();
        if (intent != null) {
            mId = intent.getInt("id");
            final String title = intent.getString("name");
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
                        collapsingToolbarLayout.setTitle(" "); //careful there should a space between double quote otherwise it wont work
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

            mRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mErrorLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mSlidingProgressBar.setVisibility(View.VISIBLE);
                    fetchTvShowDetail();
                }
            });
        }
    }

    private void fetchTvShowDetail() {
        JsonObjectRequest tvDetailRequest = new JsonObjectRequest(Request.Method.GET, CineUrl.createTvDetailUrl(mId),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String backdropPath = response.getString("backdrop_path");
                    String firstAirDate = response.getString("first_air_date");
                    //  String homePage = response.getString("homepage");
                    int id = response.getInt("id");
                    //  String last_air_date = response.getString("last_air_date");
                    String name = response.getString("name");
                    int numberOfEpisodes = response.getInt("number_of_episodes");
                    int numberOfSeasons = response.getInt("number_of_seasons");
                    String overview = response.getString("overview");
                    String posterPath = response.getString("poster_path");

                    Uri uri = CineUrl.createImageUri(imageQuality, posterPath);

                    mProgressBar.setVisibility(View.INVISIBLE);
                    mFullLayout.setVisibility(View.VISIBLE);
                    firstAirDateView.setText(CineDateFormat.formatDate(firstAirDate));
                    titleView.setText(name);
                    //  episodeCountView.setText(String.valueOf(numberOfEpisodes));
                    //  seasonCountView.setText(String.valueOf(numberOfSeasons));
                    overView.setText(overview);

                    String voteAverage = String.valueOf(response.get("vote_average"));
                    int voteCount = response.getInt("vote_count");

                    StringBuilder createdByBuilder = new StringBuilder();
                    JSONArray createdByArray = response.getJSONArray("created_by");
                    for (int i = 0; i < createdByArray.length(); i++) {
                        JSONObject currentObject = createdByArray.getJSONObject(i);
                        String peopleName = currentObject.getString("name");
                        createdByBuilder.append(peopleName);
                        if (i < createdByArray.length() - 1) {
                            createdByBuilder.append(", ");
                        }
                    }
                    // createdByView.setText(createdByBuilder.toString());

                    JSONArray genreArray = response.getJSONArray("genres");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int g = 0; g < genreArray.length(); g++) {
                        JSONObject currentObject = genreArray.getJSONObject(g);
                        String genreName = currentObject.getString("name");
                        stringBuilder.append(genreName);
                        if (g < genreArray.length() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    genresView.setText(stringBuilder.toString());

                    JSONObject imagesObject = response.getJSONObject("images");
                    JSONArray backdropArray = imagesObject.getJSONArray("backdrops");
                    if (backdropArray.length() > 0) {
                        for (int i = 0; i < backdropArray.length(); i++) {
                            JSONObject currentBackdropObject = backdropArray.getJSONObject(i);
                            String imagePath = currentBackdropObject.getString("file_path");
                            slidingList.add(imagePath);
                        }
                        if (slidingList.size() > 0) {
                            mSlidingAdapter = new SlidingPagerAdapter(getBaseContext(), slidingList);
                            mSlidingViewPager.setAdapter(mSlidingAdapter);
                            mPagerIndicator.setViewPager(mSlidingViewPager);
                            mSlidingProgressBar.setVisibility(View.INVISIBLE);
                            mSlidingAdapter.notifyDataSetChanged();
                            if (slidingList.size() == 1) {
                                mPagerIndicator.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        slidingList.add(backdropPath);
                        mSlidingAdapter = new SlidingPagerAdapter(getBaseContext(), slidingList);
                        mSlidingViewPager.setAdapter(mSlidingAdapter);
                        mPagerIndicator.setViewPager(mSlidingViewPager);
                        mSlidingProgressBar.setVisibility(View.INVISIBLE);
                        mSlidingAdapter.notifyDataSetChanged();
                        mPagerIndicator.setVisibility(View.INVISIBLE);
                    }

              /*      JSONArray runtimeArray = response.getJSONArray("runtime");
                    for (int r=0; r< runtimeArray.length(); r++){
                        JSONObject currentObject = runtimeArray.getJSONObject(r);
                    }
                    JSONArray networkArray = response.getJSONArray("networks");
                    for (int i=0; i< networkArray.length(); i++){
                        JSONObject currentObject = networkArray.getJSONObject(i);
                        String networkName = currentObject.getString("name");
                    }
                    JSONArray productionCompaniesArray = response.getJSONArray("production_companies");
                    for (int i=0; i< productionCompaniesArray.length(); i++){
                        JSONObject currentObject = productionCompaniesArray.getJSONObject(i);
                        String companyName = currentObject.getString("name");
                        String country = currentObject.getString("origin_country");
                    }
*/
                    JSONArray seasonsArray = response.getJSONArray("seasons");
                    if (seasonsArray.length() > 0) {
                        for (int i = 0; i < seasonsArray.length(); i++) {
                            JSONObject currentObject = seasonsArray.getJSONObject(i);
                            String airDate = currentObject.getString("air_date");
                            String seasonName = currentObject.getString("name");
                            int seasonId = currentObject.getInt("id");
                            int seasonNumber = currentObject.getInt("season_number");
                            String seasonPosterPath = currentObject.getString("poster_path");
                            mSeasonList.add(new TvShows(seasonId, seasonName, seasonPosterPath, airDate));
                            fetchSeasonVideos(id, seasonNumber);
                        }
                        if (mSeasonList.size() > 0) {
                            mSeasonLayout.setVisibility(View.VISIBLE);
                            mSeasonAdapter.addData(mSeasonList);
                        } else {
                            mSeasonLayout.setVisibility(View.GONE);
                        }
                    }
                    JSONObject externalIdsObject = response.getJSONObject("external_ids");
                    mImdbId = externalIdsObject.getString("imdb_id");

                    JSONObject creditsObject = response.getJSONObject("credits");
                    JSONArray castArray = creditsObject.getJSONArray("cast");
                    JSONArray crewArray = creditsObject.getJSONArray("crew");

                    for (int i = 0; i < castArray.length(); i++) {
                        JSONObject currentObject = castArray.getJSONObject(i);
                        String castName = currentObject.getString("name");
                        int castId = currentObject.getInt("id");
                        String profilePath = currentObject.getString("profile_path");
                        String character = currentObject.getString("character");
                        mCastList.add(new People(castId, castName, profilePath, character, true));
                    }
                    if (mCastList.size() > 0) {
                        mCastLayout.setVisibility(View.VISIBLE);
                        mCastAdapter.addCredits(mCastList);
                    } else {
                        mCastLayout.setVisibility(View.GONE);
                    }

                    for (int j = 0; j < crewArray.length(); j++) {
                        JSONObject currentObject = crewArray.getJSONObject(j);
                        String crewName = currentObject.getString("name");
                        int crewId = currentObject.getInt("id");
                        String profilePath = currentObject.getString("profile_path");
                        String job = currentObject.getString("job");
                        mCrewList.add(new People(crewId, crewName, profilePath, job, false));
                    }
                    if (mCrewList.size() > 0) {
                        mCrewLayout.setVisibility(View.VISIBLE);
                        mCrewAdapter.addCredits(mCrewList);
                    } else {
                        mCrewLayout.setVisibility(View.GONE);
                    }

                    JSONObject videosObject = response.getJSONObject("videos");
                    JSONArray videosArray = videosObject.getJSONArray("results");
                    if (videosArray.length() > 0) {
                        for (int v = 0; v < videosArray.length(); v++) {
                            JSONObject currentObject = videosArray.getJSONObject(v);
                            String videoKey = currentObject.getString("key");
                            String videoTitle = currentObject.getString("name");
                            mVideoList.add(new VideoEntry(videoTitle, videoKey));
                        }
                        if (mVideoList.size() > 0) {
                            mVideoLayout.setVisibility(View.VISIBLE);
                            mYoutubeAdapter.addVideoEntries(mVideoList);
                        } else {
                            mVideoLayout.setVisibility(View.GONE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mErrorLayout.setVisibility(View.VISIBLE);
                mSlidingProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        tvDetailRequest.setTag(CineTag.TV_DETAIL_TAG);
        tvDetailRequest.setShouldCache(false);
        MySingleton.getInstance(this).addToRequestQueue(tvDetailRequest);
    }

    private void fetchSeasonVideos(int tvId, int seasonNumber) {
        JsonObjectRequest videoRequest = new JsonObjectRequest(Request.Method.GET, CineUrl.createTvSeasonDetailUrl(tvId, seasonNumber), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject videosObject = response.getJSONObject("videos");
                    JSONArray videosArray = videosObject.getJSONArray("results");
                    if (videosArray.length() > 0) {
                        for (int v = 0; v < videosArray.length(); v++) {
                            JSONObject currentObject = videosArray.getJSONObject(v);
                            String videoKey = currentObject.getString("key");
                            String videoTitle = currentObject.getString("name");
                            mVideoList.add(new VideoEntry(videoTitle, videoKey));
                        }
                        if (mVideoList.size() > 0) {
                            mVideoLayout.setVisibility(View.VISIBLE);
                            mYoutubeAdapter.addVideoEntries(mVideoList);
                        } else {
                            mVideoLayout.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        videoRequest.setTag("video_request");
        MySingleton.getInstance(this).getRequestQueue().add(videoRequest);
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
        Intent intent;
        intent = YouTubeStandalonePlayer.createVideoIntent(this, DeveloperKey.DEVELOPER_KEY, videoKey, 0, true, mLightModeBox);
        if (intent != null) {
            someActivityResultLauncher.launch(intent);
        } else {
            YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason = YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage = getString(R.string.error_player) + errorReason.toString();
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(CineTag.TV_DETAIL_TAG);
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
                // Define what your app should do if no activity can handle the intent.
            }
        } else if (itemId == R.id.action_view_on_tmdb) {
            Uri tmdbUri = CineUrl.createTMDbWebUri(mId, "tv");
            Intent tmdbIntent = new Intent(Intent.ACTION_VIEW, tmdbUri);
            try {
                startActivity(tmdbIntent);
            } catch (ActivityNotFoundException e) {
                // Define what your app should do if no activity can handle the intent.
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareTvShow() {
        Uri uri = CineUrl.createTMDbWebUri(mId, "tv");
        String mimeType = "text/plane";
        String textToShare = String.valueOf(uri);
        String title = "Complete action using";

        ShareCompat.IntentBuilder builder = new ShareCompat.IntentBuilder(TvDetailActivity.this);
        builder.setType(mimeType).setChooserTitle(title).setText(textToShare).startChooser();
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                } else {
                    YouTubeInitializationResult errorReason = YouTubeStandalonePlayer.getReturnedInitializationResult(result.getData());
                    if (errorReason.isUserRecoverableError()) {
                        errorReason.getErrorDialog(TvDetailActivity.this, 0).show();
                    } else {
                        String errorMessage = getString(R.string.error_player) + errorReason.toString();
                        Toast.makeText(TvDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

}
