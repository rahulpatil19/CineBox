package patil.rahul.cineboxtma.tabfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.TvDetailActivity;
import patil.rahul.cineboxtma.adapters.TvAdapter;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.utils.MySingleton;

public class TvTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CineListener.OnTvClickListener {
    public TvTabFragment() {
    }

    private RecyclerView mRecyclerView;
    private TvAdapter mTvAdapter;
    private LinearLayout mErrorLayout;
    private Button mRetryBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private String TV_CATEGORY;
    private String requestTag;

    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isFirstLoad = true;
    private boolean isEndOfPage = true;
    private int currentPage = 1;
    private int totalPages = 1;
    private List<TvShows> mTvShowList = new ArrayList<>();

    public static TvTabFragment newInstance(String category) {
        TvTabFragment tvTabFragment = new TvTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        tvTabFragment.setArguments(bundle);
        return tvTabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageQuality = CinePreferences.getImageQualityValue(getContext());
        TV_CATEGORY = getArguments().getString("category");

        switch (TV_CATEGORY) {
            case "popular":
                requestTag = CineTag.TV_POPULAR_TAG;
                break;
            case "top_rated":
                requestTag = CineTag.TV_TOP_RATED_TAG;
                break;
            case "on_tv":
                requestTag = CineTag.ON_TV_TAG;
                break;
        }

        mTvAdapter = new TvAdapter(getContext(), this, imageQuality, (AppCompatActivity) getActivity());
        fetchTvShows();
    }

    private void fetchTvShows() {
        if (isEndOfPage && currentPage <= totalPages) {
            isEndOfPage = false;
            if (!isFirstLoad) {
                mTvShowList.add(null);
                mTvAdapter.notifyItemInserted(mTvShowList.size() - 1);
            }

            String tvListUrl = CineUrl.createTvListUrl(TV_CATEGORY, currentPage);

            JsonObjectRequest tvShowRequest = new JsonObjectRequest(Request.Method.GET, tvListUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        totalPages = response.getInt("total_pages");
                        int page = response.getInt("page");

                        JSONArray tvArray = response.getJSONArray("results");
                        if (tvArray.length() > 0) {
                            currentPage = ++page;
                            if (!isFirstLoad) {
                                mTvShowList.remove(mTvShowList.size() - 1);
                                mTvAdapter.notifyItemRemoved(mTvShowList.size());
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
                                mTvShowList.add(new TvShows(id, name, backdropPath, firstAirDate, genreList, overview, voteAverage));
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            mErrorLayout.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            int currentSize = mTvAdapter.getItemCount();
                            mTvAdapter.addData(mTvShowList);
                            mTvAdapter.notifyItemRangeInserted(currentSize, mTvShowList.size() - 1);
                            isFirstLoad = false;
                            isEndOfPage = true;
                        } else if (mTvShowList.size() > 0 && tvArray.length() == 0) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mTvShowList.remove(mTvShowList.size() - 1);
                            mTvAdapter.notifyItemRemoved(mTvShowList.size());
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
                    if (mTvShowList.size() > 0) {
                        mTvShowList.remove(mTvShowList.size() - 1);
                        mTvAdapter.notifyItemRemoved(mTvShowList.size());
                        isEndOfPage = true;
                        Snackbar.make(mRecyclerView, "Couldn't load, Try Again", Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                retryFetching();
                            }
                        });
                    } else {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    }
                }
            });
            tvShowRequest.setTag(requestTag);
            MySingleton.getInstance(getContext()).addToRequestQueue(tvShowRequest);
        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (currentPage > totalPages) {
                mSwipeRefreshLayout.setRefreshing(false);
                mTvShowList.remove(mTvShowList.size() - 1);
                mTvAdapter.notifyItemRemoved(mTvShowList.size());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.tab_recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mRetryBtn = view.findViewById(R.id.retry_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTvAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        fetchTvShows();
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

    private void resetAllState() {
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(requestTag);
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
    }

    private void retryFetching() {
        if (mTvShowList.size() > 0) {
            mSwipeRefreshLayout.setRefreshing(true);
            fetchTvShows();
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            fetchTvShows();
        }
    }

    @Override
    public void onRefresh() {
        if (mTvShowList.size() > 0) {
            fetchTvShows();
        } else if (mErrorLayout.getVisibility() == View.VISIBLE) {
            mErrorLayout.setVisibility(View.INVISIBLE);
            resetAllState();
            fetchTvShows();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetAllState();
        if (mTvShowList.size() > 0) {
            mTvAdapter.clearAdapter();
        }
    }

    public void refreshTvList(){
        if (mTvShowList.size() > 0){
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        Intent intent = new Intent(getContext(), TvDetailActivity.class);
        intent.putExtra("id", tvShows.getId());
        intent.putExtra("name", tvShows.getName());
        startActivity(intent);
    }
}
