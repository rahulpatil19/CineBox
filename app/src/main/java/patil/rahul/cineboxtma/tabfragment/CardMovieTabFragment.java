package patil.rahul.cineboxtma.tabfragment;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.adapters.MovieCardAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.utils.MySingleton;

/**
 * Created by rahul on 20/2/18.
 */

public class CardMovieTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CineListener.OnMovieClickListener {
    public CardMovieTabFragment() {
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private LinearLayout mErrorLayout;
    private Button mRetryBtn;
    private RecyclerView mMovieRecyclerView;
    private MovieCardAdapter mMovieAdapter;
    private CineListener.OnMovieClickListener onMovieClickListener;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int currentPage = 1;
    private int apiTotalPages = 1;
    private boolean isEndOfPage = true;
    private boolean isFirstLoad = true;

    private String requestTag;
    private static final String MOVIE_CATEGORY = "movie_category";
    private String mMovieCategory;

    List<Movie> mMovieList = new ArrayList<>();

    public static CardMovieTabFragment newInstance(String movieCategory) {
        CardMovieTabFragment fragment = new CardMovieTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_CATEGORY, movieCategory);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageQuality = CinePreferences.getImageQualityValue(getContext());
        mMovieCategory = getArguments().getString(MOVIE_CATEGORY);
        requestTag = mMovieCategory;
        mMovieAdapter = new MovieCardAdapter(getContext(), this, imageQuality, getActivity());
        fetchMovieList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        mProgressBar = view.findViewById(R.id.progress_bar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mRetryBtn = view.findViewById(R.id.retry_btn);
        mMovieRecyclerView = view.findViewById(R.id.tab_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

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
        mMovieRecyclerView.addOnScrollListener(scrollListener);
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryFetching();
            }
        });
        return view;
    }

    private void fetchMovieList() {
        if (isEndOfPage && currentPage <= apiTotalPages) {
            isEndOfPage = false;
            if (!isFirstLoad) {
                mMovieList.add(null);
                mMovieAdapter.notifyItemInserted(mMovieList.size() - 1);
            }

            final String fullMovieUrl = buildMovieUrl(currentPage);
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
                            mSwipeRefreshLayout.setRefreshing(false);
                            mErrorLayout.setVisibility(View.GONE);
                            mMovieRecyclerView.setVisibility(View.VISIBLE);
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
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (mMovieList.size() > 0) {
                        mMovieList.remove(mMovieList.size() - 1);
                        mMovieAdapter.notifyItemRemoved(mMovieList.size());
                        isEndOfPage = true;
                        Snackbar.make(mMovieRecyclerView, "Couldn't load, Try Again", Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                retryFetching();
                            }
                        }).show();
                    } else {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mMovieRecyclerView.setVisibility(View.INVISIBLE);
                    }
                }
            });
            movieRequest.setTag(requestTag);
            MySingleton.getInstance(getContext()).getRequestQueue().add(movieRequest);
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
        if (mMovieList.size() > 0) {
            fetchMovieList();
        } else if (mErrorLayout.getVisibility() == View.VISIBLE) {
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
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(requestTag);
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetAllState();
        if (mMovieList.size() > 0) {
            mMovieAdapter.clearAdapter();
        }
    }

    public void refreshMovieFragment() {
        if (mMovieList.size() > 0) {
            mMovieRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onMovieClickListener = (CineListener.OnMovieClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement OnMovieClickListener");
        }
    }

    private String buildMovieUrl(int page) {
        return CineUrl.createMovieListUrl(mMovieCategory,page);
    }

    @Override
    public void onMovieClick(Movie movie) {
        onMovieClickListener.onMovieClick(movie);
    }

}
