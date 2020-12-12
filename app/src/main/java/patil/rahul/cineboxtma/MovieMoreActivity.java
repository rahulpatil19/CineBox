package patil.rahul.cineboxtma;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import patil.rahul.cineboxtma.adapters.MovieAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.Cine;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.utils.MySingleton;

public class MovieMoreActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        CineListener.OnMovieClickListener {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Button mRetryBtn;
    private LinearLayout mErrorLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private MovieAdapter mMovieAdapter;
    private boolean isEndOfPage = true;
    private boolean isFirstLoad = true;
    private int currentPage = 1;
    private int apiTotalPages = 1;
    private List<Movie> mMovieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = findViewById(R.id.progress_bar);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.recycler_view_activity_more);
        mErrorLayout = findViewById(R.id.error_layout);
        mRetryBtn = findViewById(R.id.retry_btn);
        String imageQuality = CinePreferences.getImageQualityValue(this);

        Intent intent = getIntent();
        if (intent != null) {
            String mediaType = intent.getExtras().getString(MainActivity.MEDIA_TYPE);

            if (mediaType.equals("movie")) {
                setTitle(R.string.upcoming_movies);
                mMovieAdapter = new MovieAdapter(this, this, imageQuality);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mMovieAdapter);
                fetchMovieList();
                scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                fetchMovieList();
                            }
                        });
                    }
                };
                mRecyclerView.addOnScrollListener(scrollListener);
                mRetryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retryFetching();
                    }
                });
            }
        }
    }

    private void fetchMovieList() {
        if (isEndOfPage && currentPage <= apiTotalPages) {
            isEndOfPage = false;
            if (!isFirstLoad) {
                mMovieList.add(null);
                mMovieAdapter.notifyItemInserted(mMovieList.size() - 1);
            }

            final String fullMovieUrl = CineUrl.createUpcomingMovieUrl(currentPage);
            final JsonObjectRequest movieRequest = new JsonObjectRequest(Request.Method.GET, fullMovieUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        apiTotalPages = response.getInt("total_pages");
                        int page = response.getInt("page");
                        JSONArray movieArray = response.getJSONArray("results");
                        if (movieArray.length() > 0) {
                            currentPage = page + 1;
                            if (!isFirstLoad) {
                                mMovieList.remove(mMovieList.size() - 1);
                                mMovieAdapter.notifyItemRemoved(mMovieList.size());
                            }
                            for (int i = 0; i < movieArray.length(); i++) {
                                JSONObject currentObject = movieArray.getJSONObject(i);
                                String poster_path = currentObject.getString("poster_path");
                                String backdrop_path = currentObject.getString("backdrop_path");
                                String overview = currentObject.getString("overview");
                                String release_date = currentObject.getString("release_date");
                                JSONArray genre_ids = currentObject.getJSONArray("genre_ids");

                                List<Integer> genreList = new ArrayList<>();
                                for (int j = 0; j < genre_ids.length(); j++) {
                                    genreList.add((Integer) genre_ids.get(j));
                                }

                                int id = currentObject.getInt("id");
                                String title = currentObject.getString("title");
                                String popularity = String.valueOf(currentObject.getInt("popularity"));
                                String voteCount = String.valueOf(currentObject.getInt("vote_count"));
                                String voteAverage = String.valueOf(currentObject.get("vote_average"));

                                mMovieList.add(new Movie(poster_path, backdrop_path, overview, release_date, genreList, id,
                                        title, popularity, voteCount, voteAverage));
                            }
                            mErrorLayout.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            int currentSize = mMovieAdapter.getItemCount();
                            mMovieAdapter.addData(mMovieList);
                            mMovieAdapter.notifyItemRangeInserted(currentSize, mMovieList.size() - 1);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            isFirstLoad = false;
                            isEndOfPage = true;
                        } else if (movieArray.length() == 0 && mMovieList.size() > 0) {
                            mMovieList.remove(mMovieList.size() - 1);
                            mMovieAdapter.notifyItemRemoved(mMovieList.size());
                            isEndOfPage = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    if (mMovieList.size() > 0) {
                        mMovieList.remove(mMovieList.size() - 1);
                        mMovieAdapter.notifyItemRemoved(mMovieList.size());
                        isEndOfPage = true;
                        Snackbar.make(mRecyclerView, "No connection, Try Again", Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                retryFetching();
                            }
                        }).show();
                    } else {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        Toast.makeText(getBaseContext(), "Couldn't load", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            movieRequest.setTag(CineTag.MOVIE_UPCOMING_TAG);
            MySingleton.getInstance(this).getRequestQueue().add(movieRequest);
        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (currentPage > apiTotalPages) {
                mSwipeRefreshLayout.setRefreshing(false);
                mMovieList.remove(mMovieList.size() - 1);
                mMovieAdapter.notifyItemRemoved(mMovieList.size());
            }
        }
    }

    @Override
    public void onRefresh() {
        MySingleton.getInstance(this).getRequestQueue().cancelAll(CineTag.MOVIE_UPCOMING_TAG);
        if (mMovieList.size() > 0) {
            fetchMovieList();
        } else {
            mErrorLayout.setVisibility(View.INVISIBLE);
            resetAllState();
            fetchMovieList();
        }
    }

    private void retryFetching() {
        if (mMovieList.size() > 0) {
            mSwipeRefreshLayout.setRefreshing(true);
            fetchMovieList();
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            fetchMovieList();
        }
    }

    private void resetAllState() {
        MySingleton.getInstance(this).getRequestQueue().cancelAll(CineTag.MOVIE_UPCOMING_TAG);
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}