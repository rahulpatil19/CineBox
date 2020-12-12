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
import patil.rahul.cineboxtma.adapters.TvAdapter;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.utils.MySingleton;

public class TvMoreActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, CineListener.OnTvClickListener {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button mRetryBtn;
    private LinearLayout mErrorLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private TvAdapter mTvAdapter;
    private boolean isEndOfPage = true;
    private boolean isFirstLoad = true;
    private int currentPage = 1;
    private int apiTotalPages = 1;
    private List<TvShows> mTvList = new ArrayList<>();

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
            if (mediaType.equals("tv")) {
                setTitle(R.string.airing_today);
                mTvAdapter = new TvAdapter(this, this, imageQuality, this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mTvAdapter);
                fetchTvList();

                scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                fetchTvList();
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

    private void fetchTvList() {
        if (isEndOfPage && currentPage <= apiTotalPages) {
            isEndOfPage = false;
            if (!isFirstLoad) {
                mTvList.add(null);
                mTvAdapter.notifyItemInserted(mTvList.size() - 1);
            }
            String tvListUrl = CineUrl.createTvListUrl(CineUrl.CATEGORY_AIRING_TODAY, currentPage);

            JsonObjectRequest tvShowRequest = new JsonObjectRequest(Request.Method.GET, tvListUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        apiTotalPages = response.getInt("total_pages");
                        int page = response.getInt("page");

                        JSONArray tvArray = response.getJSONArray("results");
                        if (tvArray.length() > 0) {
                            currentPage = ++page;
                            if (!isFirstLoad) {
                                mTvList.remove(mTvList.size() - 1);
                                mTvAdapter.notifyItemRemoved(mTvList.size());
                            }
                            for (int i = 0; i < tvArray.length(); i++) {
                                JSONObject currentObject = tvArray.getJSONObject(i);
                                int id = currentObject.getInt("id");
                                String backdropPath = currentObject.getString("backdrop_path");
                                String voteAverage = String.valueOf(currentObject.get("vote_average"));
                                String overview = currentObject.getString("overview");
                                String firstAirDate = currentObject.getString("first_air_date");
                                JSONArray genreIds = currentObject.getJSONArray("genre_ids");
                                String name = currentObject.getString("name");

                                List<Integer> genreList = new ArrayList<>();
                                for (int j = 0; j < genreIds.length(); j++) {
                                    genreList.add((Integer) genreIds.get(j));
                                }
                                mTvList.add(new TvShows(id, name, backdropPath, firstAirDate, genreList, overview, voteAverage));
                            }
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            mErrorLayout.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            int currentSize = mTvAdapter.getItemCount();
                            mTvAdapter.addData(mTvList);
                            mTvAdapter.notifyItemRangeInserted(currentSize, mTvList.size() - 1);
                            isFirstLoad = false;
                            isEndOfPage = true;
                        } else if (mTvList.size() > 0 && tvArray.length() == 0) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mTvList.remove(mTvList.size() - 1);
                            mTvAdapter.notifyItemRemoved(mTvList.size());
                            isEndOfPage = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (mTvList.size() > 0) {
                        mTvList.remove(mTvList.size() - 1);
                        mTvAdapter.notifyItemRemoved(mTvList.size());
                        isEndOfPage = true;
                        Snackbar.make(mRecyclerView, "Could'nt load, Try Again", Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchTvList();
                            }
                        });
                    } else {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            tvShowRequest.setTag(CineTag.TV_AIRING_TODAY_TAG);
            MySingleton.getInstance(this).getRequestQueue().add(tvShowRequest);

        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (currentPage > apiTotalPages) {
                mTvList.remove(mTvList.size() - 1);
                mTvAdapter.notifyItemRemoved(mTvList.size());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        Intent intent = new Intent(this, TvDetailActivity.class);
        intent.putExtra("id", tvShows.getId());
        intent.putExtra("name", tvShows.getName());
        startActivity(intent);
    }

    private void resetAllState() {
        MySingleton.getInstance(this).getRequestQueue().cancelAll(CineTag.TV_AIRING_TODAY_TAG);
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
    }

    private void retryFetching() {
        if (mTvList.size() > 0) {
            mSwipeRefreshLayout.setRefreshing(true);
            fetchTvList();
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            fetchTvList();
        }
    }

    @Override
    public void onRefresh() {
        MySingleton.getInstance(this).getRequestQueue().cancelAll(CineTag.TV_AIRING_TODAY_TAG);
        if (mTvList.size() > 0) {
            fetchTvList();
        } else {
            mErrorLayout.setVisibility(View.INVISIBLE);
            resetAllState();
            fetchTvList();
        }
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
