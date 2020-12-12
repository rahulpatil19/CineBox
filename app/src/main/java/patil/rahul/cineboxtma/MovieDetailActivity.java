package patil.rahul.cineboxtma;

import android.content.Intent;
import android.content.pm.ResolveInfo;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;
import patil.rahul.cineboxtma.adapters.MovieHorizontalAdapter;
import patil.rahul.cineboxtma.adapters.PeopleCreditAdapter;
import patil.rahul.cineboxtma.adapters.YoutubeListAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.VideoEntry;
import patil.rahul.cineboxtma.pageradapters.SlidingPagerAdapter;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.Cine.MovieEntry;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.DeveloperKey;
import patil.rahul.cineboxtma.utils.MySingleton;

public class MovieDetailActivity extends AppCompatActivity implements CineListener.OnPeopleClickListener,
        YoutubeListAdapter.OnYoutubeItemClickListener, CineListener.OnMovieClickListener {

    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private CircleIndicator mPagerIndicator;
    private SlidingPagerAdapter mSlidingAdapter;
    private ViewPager mSlidingViewPager;
    private ProgressBar mSlidingProgressBar;
    private List<String> slidingList = new ArrayList<>();

    private String videoKey;
    private boolean mLightModeBox;

    private int mId;
    private PeopleCreditAdapter mCastAdapter, mCrewAdapter;
    private MovieHorizontalAdapter mSimilarMovieAdapter;
    private YoutubeListAdapter mYoutubeAdapter;
    private RecyclerView mCastRecyclerView, mCrewRecyclerView, mVideoRecyclerView, mSimilarMovieRecyclerView;
    private LinearLayout mMainLayout, mVideoLayout, mCastLayout, mCrewLayout, mSimilarMoviesLayout, mErrorLayout;
    private LinearLayout mRatingLayout;
    private ProgressBar mProgressBar;
    private Button mRetryBtn;
    private TextView titleView, releaseDateView, runtimeView, directorView, genresView, voteAverageView, voteCountView;
    private ExpandableTextView overView;

    private String mImdbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        boolean isVideoPrefChecked = CinePreferences.getVideoMode(this);

        mLightModeBox = !isVideoPrefChecked;

        mSlidingViewPager = findViewById(R.id.slidingViewPager);
        mPagerIndicator = findViewById(R.id.pager_indicator);
        mSlidingProgressBar = findViewById(R.id.sliding_progress_bar);
        mMainLayout = findViewById(R.id.main_layout);
        mRatingLayout = findViewById(R.id.rating_view);
        mVideoLayout = findViewById(R.id.video_layout);
        mCastLayout = findViewById(R.id.cast_layout);
        mCrewLayout = findViewById(R.id.crew_layout);
        mSimilarMoviesLayout = findViewById(R.id.similar_movies_layout);
        mErrorLayout = findViewById(R.id.detail_error_layout);
        mRetryBtn = findViewById(R.id.retry_btn);

        mProgressBar = findViewById(R.id.progress_bar);
        mCastRecyclerView = findViewById(R.id.cast_recycler_view);
        mCrewRecyclerView = findViewById(R.id.crew_recycler_view);
        mVideoRecyclerView = findViewById(R.id.video_recycler_view);
        mSimilarMovieRecyclerView = findViewById(R.id.similar_movies_recycler_view);

        titleView = findViewById(R.id.movie_title);
        releaseDateView = findViewById(R.id.movie_release_date);
        runtimeView = findViewById(R.id.movie_runtime);
        voteAverageView = findViewById(R.id.movie_voteAverage);
        voteCountView = findViewById(R.id.movie_voteCount);
        directorView = findViewById(R.id.movie_director);
        genresView = findViewById(R.id.movie_genres);
        overView = findViewById(R.id.expand_text_view);

        final Bundle intent = getIntent().getExtras();
        if (intent != null) {
            mId = intent.getInt(MovieEntry.ID);
            final String title = intent.getString(MovieEntry.TITLE);

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

            mCastAdapter = new PeopleCreditAdapter(this, this);
            mCrewAdapter = new PeopleCreditAdapter(this, this);
            mYoutubeAdapter = new YoutubeListAdapter(this, this);
            mSimilarMovieAdapter = new MovieHorizontalAdapter(this, this, CinePreferences.getImageQualityValue(this));

            mCastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mCrewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mSimilarMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            mCastRecyclerView.setNestedScrollingEnabled(false);
            mCrewRecyclerView.setNestedScrollingEnabled(false);
            mVideoRecyclerView.setNestedScrollingEnabled(false);
            mSimilarMovieRecyclerView.setNestedScrollingEnabled(false);
            mCastRecyclerView.setHasFixedSize(true);
            mCrewRecyclerView.setHasFixedSize(true);
            mSimilarMovieRecyclerView.setHasFixedSize(true);
            mVideoRecyclerView.setHasFixedSize(true);
            mCastRecyclerView.setAdapter(mCastAdapter);
            mCrewRecyclerView.setAdapter(mCrewAdapter);
            mVideoRecyclerView.setAdapter(mYoutubeAdapter);
            mSimilarMovieRecyclerView.setAdapter(mSimilarMovieAdapter);

            fetchMovieData(mId);

            mRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mErrorLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mSlidingProgressBar.setVisibility(View.VISIBLE);
                    fetchMovieData(mId);
                }
            });
        }
    }

    private void fetchMovieData(int id) {
        final List<VideoEntry> videoList = new ArrayList<>();
        final List<People> castList = new ArrayList<>();
        final List<People> crewList = new ArrayList<>();
        final List<Movie> similarMovieList = new ArrayList<>();

        final JsonObjectRequest movieDetailRequest = new JsonObjectRequest(Request.Method.GET, CineUrl.createMovieDetailUrl(id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String backdrop_path = response.getString("backdrop_path");
                    String overview = response.getString("overview");
                    String release_date = response.getString("release_date");
                    String title = response.getString("title");
                    String voteCount = String.valueOf(response.getInt("vote_count"));
                    String voteAverage = String.valueOf(response.get("vote_average"));
                    String runtime = response.getString("runtime");
                    JSONArray genresArray = response.getJSONArray("genres");

                    mMainLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                    titleView.setText(title);

                    int voteValue = Integer.parseInt(voteCount);

                    if (voteValue > 10) {
                        voteAverageView.setText(voteAverage);
                        voteCountView.setText(String.format("%s votes", voteCount));
                    } else {
                        mRatingLayout.setVisibility(View.GONE);
                    }

                    overView.setText(overview);
                    releaseDateView.setText(CineDateFormat.formatDate(release_date));
                    releaseDateView.setVisibility(View.VISIBLE);

                    if (!runtime.equals("null")) {
                        runtimeView.setText(String.format("%s min", runtime));
                    } else {
                        runtimeView.setText(R.string.no_runtime_min);
                    }

                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < genresArray.length(); j++) {
                        JSONObject object = genresArray.getJSONObject(j);
                        builder = builder.append(object.getString("name"));
                        if (j < genresArray.length() - 1) {
                            builder.append(", ");
                        }
                    }
                    genresView.setText(builder.toString());

                    JSONObject externalIdsObject = response.getJSONObject("external_ids");
                    mImdbId = externalIdsObject.getString("imdb_id");

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
                        slidingList.add(backdrop_path);
                        mSlidingAdapter = new SlidingPagerAdapter(getBaseContext(), slidingList);
                        mSlidingViewPager.setAdapter(mSlidingAdapter);
                        mPagerIndicator.setViewPager(mSlidingViewPager);
                        mSlidingProgressBar.setVisibility(View.INVISIBLE);
                        mSlidingAdapter.notifyDataSetChanged();
                        mPagerIndicator.setVisibility(View.INVISIBLE);
                    }

                    JSONObject videosObject = response.getJSONObject("videos");
                    JSONArray videosArray = videosObject.getJSONArray("results");
                    if (videosArray.length() > 0) {
                        for (int i = 0; i < videosArray.length(); i++) {
                            JSONObject currentObject = videosArray.getJSONObject(i);
                            String videoKey = currentObject.getString("key");
                            String videoTitle = currentObject.getString("name");
                            videoList.add(new VideoEntry(videoTitle, videoKey));
                        }
                        if (videoList.size() > 0) {
                            mVideoLayout.setVisibility(View.VISIBLE);
                            mYoutubeAdapter.addVideoEntries(videoList);
                        } else {
                            mVideoLayout.setVisibility(View.GONE);
                        }
                    }

                    JSONObject creditsObjects = response.getJSONObject("credits");
                    JSONArray castArray = creditsObjects.getJSONArray("cast");
                    if (castArray.length() > 0) {
                        for (int i = 0; i < castArray.length(); i++) {
                            JSONObject currentObject = castArray.getJSONObject(i);
                            String character = currentObject.getString("character");
                            int id = currentObject.getInt("id");
                            String name = currentObject.getString("name");
                            String profile_path = currentObject.getString("profile_path");

                            castList.add(new People(id, name, profile_path, character, true));
                        }
                        if (castList.size() > 0) {
                            mCastLayout.setVisibility(View.VISIBLE);
                            mCastAdapter.addCredits(castList);
                        } else {
                            mCastLayout.setVisibility(View.GONE);
                        }
                    }
                    JSONArray crewArray = creditsObjects.getJSONArray("crew");
                    if (crewArray.length() > 0) {
                        for (int i = 0; i < crewArray.length(); i++) {
                            JSONObject currentObject = crewArray.getJSONObject(i);
                            int id = currentObject.getInt("id");
                            String job = currentObject.getString("job");
                            String name = currentObject.getString("name");
                            String profile_path = currentObject.getString("profile_path");

                            crewList.add(new People(id, name, profile_path, job, false));
                            if (job.equals("Director")) {
                                directorView.setText(name);
                            }
                        }
                        if (crewList.size() > 0) {
                            mCrewLayout.setVisibility(View.VISIBLE);
                            mCrewAdapter.addCredits(crewList);
                        } else {
                            mCrewLayout.setVisibility(View.GONE);
                        }

                        JSONObject similarMoviesObjest = response.getJSONObject("similar_movies");
                        JSONArray similarMovieArray = similarMoviesObjest.getJSONArray("results");

                        if (similarMovieArray.length() > 0) {
                            for (int i = 0; i < similarMovieArray.length(); i++) {
                                JSONObject currentObject = similarMovieArray.getJSONObject(i);
                                int id = currentObject.getInt("id");
                                String similarMovieTitle = currentObject.getString("title");
                                String posterPath = currentObject.getString("poster_path");
                                String releaseDate = currentObject.getString("release_date");

                                similarMovieList.add(new Movie(id, similarMovieTitle, posterPath, releaseDate, true));
                            }
                            mSimilarMovieAdapter.addData(similarMovieList);
                            mSimilarMovieAdapter.notifyDataSetChanged();
                            mSimilarMoviesLayout.setVisibility(View.VISIBLE);
                        } else {
                            mSimilarMoviesLayout.setVisibility(View.GONE);
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
                mSlidingProgressBar.setVisibility(View.INVISIBLE);
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        });
        movieDetailRequest.setTag(CineTag.MOVIE_DETAIL_TAG);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieDetailRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_go_home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.action_share:
                shareMovie();
                break;

            case R.id.action_view_on_imdb:
                Uri imdbUri = CineUrl.createExternalWebUri("IMDb", mImdbId);
                Intent imdbIntent = new Intent(Intent.ACTION_VIEW, imdbUri);
                if (imdbIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(imdbIntent);
                }
                break;

            case R.id.action_view_on_tmdb:
                Uri tmdbUri = CineUrl.createTMDbWebUri(mId, "movie");
                Intent tmdbIntent = new Intent(Intent.ACTION_VIEW, tmdbUri);
                if (tmdbIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(tmdbIntent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareMovie() {
        Uri uri = CineUrl.createTMDbWebUri(mId, "movie");
        String mimeType = "text/plane";
        String textToShare = String.valueOf(uri);
        String title = "Complete action using";
        ShareCompat.IntentBuilder.from(this).setType(mimeType)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }

    @Override
    public void onVideoClick(VideoEntry videoEntry) {
        videoKey = videoEntry.getVideoId();
        Intent intent;
        intent = YouTubeStandalonePlayer.createVideoIntent(this, DeveloperKey.DEVELOPER_KEY, videoKey, 0, true, mLightModeBox);
        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            }
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
        MySingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(CineTag.MOVIE_DETAIL_TAG);
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
    public void onMovieClick(Movie movie) {
        Intent movieIntent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Cine.MovieEntry.TITLE, movie.getTitle());
        bundle.putInt(Cine.MovieEntry.ID, movie.getId());
        movieIntent.putExtras(bundle);
        startActivity(movieIntent);
    }
}