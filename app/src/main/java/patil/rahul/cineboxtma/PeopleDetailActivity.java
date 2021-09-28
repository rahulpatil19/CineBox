package patil.rahul.cineboxtma;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;
import patil.rahul.cineboxtma.adapters.MovieCreditAdapter;
import patil.rahul.cineboxtma.adapters.PeopleImageAdapter;
import patil.rahul.cineboxtma.adapters.TvCreditAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.pageradapters.SlidingPagerAdapter;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineDateFormat;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.MySingleton;

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

    private String urlPersonDetail;
    private int personId;
    private String mIMDbId;
    private String mFacebookId;
    private String mInstagramId;
    private String mTwitterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        setupViews();
        String imageQuality = CinePreferences.getImageQualityValue(this);

        Intent intent = getIntent();
        if (intent != null) {
            personId = intent.getExtras().getInt(Cine.PersonEntry.ID);
            final String personName = intent.getExtras().getString(Cine.PersonEntry.NAME);

            urlPersonDetail = CineUrl.createPersonDetailUrl(personId);
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
                        collapsingToolbarLayout.setTitle(" "); //careful there should a space between double quote otherwise it wont work
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

            mMovieCreditsRecyclerView.setNestedScrollingEnabled(false);
            mTvCreditsRecyclerView.setNestedScrollingEnabled(false);
            mImageRecyclerView.setNestedScrollingEnabled(false);
            fetchPersonDetail();

            mRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mErrorLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    fetchPersonDetail();
                }
            });
        }
    }


    private void fetchPersonDetail() {
        final JsonObjectRequest personDetailRequest = new JsonObjectRequest(Request.Method.GET, urlPersonDetail, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String name = response.getString("name");
                    String profilePath = response.getString("profile_path");
                    String biography = response.getString("biography");

                    Uri uri = CineUrl.createImageUri("w185", profilePath);

                    mPeopleImageView.setImageURI(uri);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mTitleLayout.setVisibility(View.VISIBLE);
                    mPeopleNameTextView.setText(name);
                    mBiographyTextView.setText(biography);

                    if (response.has("birthday")) {
                        String birthday = response.getString("birthday");
                        mBirthDateTextView.setText(CineDateFormat.formatDate(birthday));
                        mAgeTextView.setText(CineDateFormat.calculateAge(birthday));
                    } else {
                        mBirthDateTextView.setText("N/A");
                        mAgeTextView.setText("N/A");
                    }

                    if (response.has("place_of_birth")) {
                        String placeOfBirth = response.getString("place_of_birth");
                        if (placeOfBirth.equals("null")) {
                            mBirthPlaceTextView.setVisibility(View.INVISIBLE);
                        } else {
                            mBirthPlaceTextView.setText(placeOfBirth);
                        }
                    } else {
                        mBirthPlaceTextView.setText("N/A");
                    }

                    JSONObject externalIdsObject = response.getJSONObject("external_ids");
                    mIMDbId = externalIdsObject.getString("imdb_id");
                    mInstagramId = externalIdsObject.getString("instagram_id");
                    mTwitterId = externalIdsObject.getString("twitter_id");
                    mFacebookId = externalIdsObject.getString("facebook_id");

                    JSONObject combinedCreditObject = response.getJSONObject("combined_credits");
                    JSONArray castCombinedArray = combinedCreditObject.getJSONArray("cast");
                    JSONArray crewCombinedArray = combinedCreditObject.getJSONArray("crew");

                    if (castCombinedArray.length() > 0) {
                        for (int i = 0; i < castCombinedArray.length(); i++) {
                            JSONObject currentObject = castCombinedArray.getJSONObject(i);

                            String mediaType = currentObject.getString("media_type");
                            if (mediaType.equals("movie")) {
                                int id = currentObject.getInt("id");
                                String backdrop_path = currentObject.getString("backdrop_path");
                                String releaseDate;
                                if (currentObject.has("release_date")) {
                                    releaseDate = currentObject.getString("release_date");
                                } else {
                                    releaseDate = "-";
                                }
                                Log.e("PeopleActivity", String.valueOf(releaseDate));
                                String title = currentObject.getString("title");
                                String character = currentObject.getString("character");
                                String posterPath = currentObject.getString("poster_path");
                                mSidingList.add(backdrop_path);
                                movieCreditList.add(new Movie(id, title, posterPath, releaseDate, character, true));
                            } else if (mediaType.equals("tv")) {
                                int id = currentObject.getInt("id");
                                String backdrop_path = currentObject.getString("backdrop_path");
                                String firstAirDate;
                                if (currentObject.has("first_air_date")) {
                                    firstAirDate = currentObject.getString("first_air_date");
                                } else {
                                    firstAirDate = null;
                                }
                                String tvName = currentObject.getString("name");
                                String character = currentObject.getString("character");
                                String posterPath = currentObject.getString("poster_path");
                                mSidingList.add(backdrop_path);
                                tvCreditList.add(new TvShows(id, tvName, posterPath, firstAirDate, character, true));
                            }
                        }
                    }

                    if (crewCombinedArray.length() > 0) {
                        for (int i = 0; i < crewCombinedArray.length(); i++) {
                            JSONObject currentObject = crewCombinedArray.getJSONObject(i);

                            String mediaType = currentObject.getString("media_type");
                            if (mediaType.equals("movie")) {
                                int movieCastId = currentObject.getInt("id");
                                String backdrop_path = currentObject.getString("backdrop_path");
                                String movieCastReleaseDate;
                                if (currentObject.has("release_date")) {
                                    movieCastReleaseDate = currentObject.getString("release_date");
                                } else {
                                    movieCastReleaseDate = "-";
                                }
                                String movieCastTitle = currentObject.getString("title");
                                String movieCastCharacter = currentObject.getString("job");
                                String movieCastPosterPath = currentObject.getString("poster_path");
                                mSidingList.add(backdrop_path);
                                movieCreditList.add(new Movie(movieCastId, movieCastTitle, movieCastPosterPath, movieCastReleaseDate, movieCastCharacter, false));
                            } else if (mediaType.equals("tv")) {
                                int id = currentObject.getInt("id");
                                String backdrop_path = currentObject.getString("backdrop_path");
                                String firstAirDate;
                                if (currentObject.has("first_air_date")) {
                                    firstAirDate = currentObject.getString("first_air_date");
                                } else {
                                    firstAirDate = null;
                                }
                                String tvName = currentObject.getString("name");
                                String job = currentObject.getString("job");
                                String posterPath = currentObject.getString("poster_path");
                                mSidingList.add(backdrop_path);
                                tvCreditList.add(new TvShows(id, tvName, posterPath, firstAirDate, job, false));
                            }
                        }
                    }

                    if (movieCreditList.size() > 0) {
                        mMovieCreditsLayout.setVisibility(View.VISIBLE);
                        mMovieCreditAdapter.addData(movieCreditList);
                        mMovieCreditAdapter.notifyDataSetChanged();
                    }

                    if (tvCreditList.size() > 0) {
                        mTvCreditsLayout.setVisibility(View.VISIBLE);
                        mTvCreditAdapter.addData(tvCreditList);
                        mTvCreditAdapter.notifyDataSetChanged();
                    }

                    if (mSidingList.size() > 0) {
                        mSlidingPagerAdapter = new SlidingPagerAdapter(getApplicationContext(), mSidingList);
                        mSlidingViewPager.setAdapter(mSlidingPagerAdapter);
                        mPagerIndicator.setViewPager(mSlidingViewPager);
                        mSlidingProgressBar.setVisibility(View.INVISIBLE);
                        mSlidingPagerAdapter.notifyDataSetChanged();
                    } else {
                        mPagerIndicator.setVisibility(View.INVISIBLE);
                        mSidingList.add(profilePath);
                        mSlidingPagerAdapter = new SlidingPagerAdapter(getApplicationContext(), mSidingList);
                        mSlidingViewPager.setAdapter(mSlidingPagerAdapter);
                        mPagerIndicator.setViewPager(mSlidingViewPager);
                        mSlidingProgressBar.setVisibility(View.INVISIBLE);
                        mSlidingPagerAdapter.notifyDataSetChanged();
                    }

                    JSONObject imageObject = response.getJSONObject("images");
                    JSONArray imagesArray = imageObject.getJSONArray("profiles");
                    for (int i = 0; i < imagesArray.length(); i++) {
                        JSONObject currentObject = imagesArray.getJSONObject(i);
                        String imagePath = currentObject.getString("file_path");
                        mImageList.add(imagePath);
                    }
                    if (mImageList.size() > 0) {
                        mImageLayout.setVisibility(View.VISIBLE);
                        mImageAdapter.addImages(mImageList);
                        mImageAdapter.notifyDataSetChanged();
                    } else {
                        mImageLayout.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            mSlidingProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mErrorLayout.setVisibility(View.VISIBLE);
        });
        personDetailRequest.setTag(CineTag.PEOPLE_DETAIL_TAG);
        MySingleton.getInstance(this).getRequestQueue().add(personDetailRequest);
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
    protected void onDestroy() {
        super.onDestroy();
        MySingleton.getInstance(this).getRequestQueue().cancelAll(CineTag.PEOPLE_DETAIL_TAG);
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
                // Define what your app should do if no activity can handle the intent.
            }
        } else if (itemId == R.id.action_view_facebook) {
            Uri facebookUri = CineUrl.createExternalWebUri("facebook", mFacebookId);
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebookUri);
            try {
                startActivity(facebookIntent);
            } catch (ActivityNotFoundException e) {
                // Define what your app should do if no activity can handle the intent.
            }
        } else if (itemId == R.id.action_view_instagram) {
            Uri instagramUri = CineUrl.createExternalWebUri("instagram", mInstagramId);
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, instagramUri);
            try {
                startActivity(instagramIntent);
            } catch (ActivityNotFoundException e) {
                // Define what your app should do if no activity can handle the intent.
            }
        } else if (itemId == R.id.action_view_twitter) {
            Uri twitterUri = CineUrl.createExternalWebUri("twitter", mTwitterId);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, twitterUri);
            try {
                startActivity(twitterIntent);
            } catch (ActivityNotFoundException e) {
                // Define what your app should do if no activity can handle the intent.
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
