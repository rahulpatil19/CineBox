package patil.rahul.cineboxtma.tabfragment.movie;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.adapters.MovieCardAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.viewmodels.MovieViewModel;

/**
 * Created by rahul on 20/2/18.
 */

public class InCinemasMovieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CineListener.OnMovieClickListener {
    public InCinemasMovieFragment() {
    }

    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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

    private List<Movie> mMovieList = new ArrayList<>();
    private MovieViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageQuality = CinePreferences.getImageQualityValue(getContext());
        mMovieAdapter = new MovieCardAdapter(getContext(), this, imageQuality, getActivity());
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_cinemas, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        startShimmer();

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
                view.post(() -> fetchMovieList());
            }
        };
        mMovieRecyclerView.addOnScrollListener(scrollListener);
        mRetryBtn.setOnClickListener(view1 -> retryFetching());

        fetchMovieList();
        return view;
    }

    private void fetchMovieList() {
        if (isEndOfPage && currentPage <= apiTotalPages) {
            isEndOfPage = false;
            if (!isFirstLoad) {
                mMovieList.add(null);
                mMovieAdapter.notifyItemInserted(mMovieList.size() - 1);
            }

            viewModel.getMovies("now_playing", currentPage).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getResults() != null) {
                    apiTotalPages = response.getTotalPages();
                    int page = response.getPage();
                    currentPage = page + 1;

                    if (!isFirstLoad) {
                        mMovieList.remove(mMovieList.size() - 1);
                        mMovieAdapter.notifyItemRemoved(mMovieList.size());
                    }

                    mMovieList.addAll(response.getResults());

                    mSwipeRefreshLayout.setRefreshing(false);
                    mErrorLayout.setVisibility(View.GONE);
                    mMovieRecyclerView.setVisibility(View.VISIBLE);
                    int currentSize = mMovieAdapter.getItemCount();
                    mMovieAdapter.addData(mMovieList);
                    mMovieAdapter.notifyItemRangeInserted(currentSize, mMovieList.size() - 1);
                    stopShimmer();
                    isFirstLoad = false;
                    isEndOfPage = true;
                } else {
                    handleError();
                }
            });
        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (currentPage > apiTotalPages) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (mMovieList.size() > 0 && mMovieList.get(mMovieList.size() - 1) == null) {
                    mMovieList.remove(mMovieList.size() - 1);
                    mMovieAdapter.notifyItemRemoved(mMovieList.size());
                }
            }
        }
    }

    private void handleError() {
        stopShimmer();
        mSwipeRefreshLayout.setRefreshing(false);
        if (mMovieList.size() > 0) {
            if (mMovieList.get(mMovieList.size() - 1) == null) {
                mMovieList.remove(mMovieList.size() - 1);
                mMovieAdapter.notifyItemRemoved(mMovieList.size());
            }
            isEndOfPage = true;
            Snackbar.make(mMovieRecyclerView, "Couldn't load, Try Again", Snackbar.LENGTH_LONG).setAction(R.string.retry, view -> retryFetching()).show();
        } else {
            mErrorLayout.setVisibility(View.VISIBLE);
            mMovieRecyclerView.setVisibility(View.INVISIBLE);
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
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            startShimmer();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        fetchMovieList();
    }

    private void resetAllState() {
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
        mMovieList.clear();
        mMovieAdapter.clearAdapter();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onMovieClickListener = (CineListener.OnMovieClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement OnMovieClickListener");
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        onMovieClickListener.onMovieClick(movie);
    }

    private void startShimmer(){
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
    }

    private void stopShimmer(){
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.INVISIBLE);
    }

}
