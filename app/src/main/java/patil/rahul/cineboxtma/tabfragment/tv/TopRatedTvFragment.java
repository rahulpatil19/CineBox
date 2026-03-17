package patil.rahul.cineboxtma.tabfragment.tv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import patil.rahul.cineboxtma.TvDetailActivity;
import patil.rahul.cineboxtma.adapters.TvAdapter;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.viewmodels.TvViewModel;

public class TopRatedTvFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CineListener.OnTvClickListener {
    public TopRatedTvFragment() {
    }

    private ShimmerFrameLayout shimmerFrameLayout;

    private RecyclerView mRecyclerView;
    private TvAdapter mTvAdapter;
    private LinearLayout mErrorLayout;
    private Button mRetryBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isFirstLoad = true;
    private boolean isEndOfPage = true;
    private int currentPage = 1;
    private int totalPages = 1;
    private List<TvShows> mTvShowList = new ArrayList<>();
    private TvViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageQuality = CinePreferences.getImageQualityValue(getContext());

        mTvAdapter = new TvAdapter(getContext(), this, imageQuality, (AppCompatActivity) getActivity());
        viewModel = new ViewModelProvider(this).get(TvViewModel.class);
    }

    private void fetchTvShows() {
        if (isEndOfPage && currentPage <= totalPages) {
            isEndOfPage = false;
            if (!isFirstLoad) {
                mTvShowList.add(null);
                mTvAdapter.notifyItemInserted(mTvShowList.size() - 1);
            }

            viewModel.getTvShows("top_rated", currentPage).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getResults() != null) {
                    totalPages = response.getTotalPages();
                    int page = response.getPage();
                    currentPage = page + 1;

                    if (!isFirstLoad) {
                        mTvShowList.remove(mTvShowList.size() - 1);
                        mTvAdapter.notifyItemRemoved(mTvShowList.size());
                    }

                    mTvShowList.addAll(response.getResults());

                    stopShimmer();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mErrorLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    int currentSize = mTvAdapter.getItemCount();
                    mTvAdapter.addData(mTvShowList);
                    mTvAdapter.notifyItemRangeInserted(currentSize, mTvShowList.size() - 1);
                    isFirstLoad = false;
                    isEndOfPage = true;
                } else {
                    handleError();
                }
            });
        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (currentPage > totalPages) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (mTvShowList.size() > 0 && mTvShowList.get(mTvShowList.size() - 1) == null) {
                    mTvShowList.remove(mTvShowList.size() - 1);
                    mTvAdapter.notifyItemRemoved(mTvShowList.size());
                }
            }
        }
    }

    private void handleError() {
        stopShimmer();
        mSwipeRefreshLayout.setRefreshing(false);
        if (mTvShowList.size() > 0) {
            if (mTvShowList.get(mTvShowList.size() - 1) == null) {
                mTvShowList.remove(mTvShowList.size() - 1);
                mTvAdapter.notifyItemRemoved(mTvShowList.size());
            }
            isEndOfPage = true;
            Snackbar.make(mRecyclerView, "Couldn't load, Try Again", Snackbar.LENGTH_LONG).setAction(R.string.retry, view -> retryFetching()).show();
        } else {
            mErrorLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv_toprated, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        startShimmer();

        mRecyclerView = view.findViewById(R.id.tab_recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
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
                view.post(() -> fetchTvShows());
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
        mRetryBtn.setOnClickListener(view1 -> retryFetching());

        fetchTvShows();
    }

    private void resetAllState() {
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
        mTvShowList.clear();
        mTvAdapter.clearAdapter();
    }

    private void retryFetching() {
        if (mTvShowList.size() > 0) {
            mSwipeRefreshLayout.setRefreshing(true);
            fetchTvShows();
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            startShimmer();
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

    private void startShimmer() {
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
    }

    private void stopShimmer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.INVISIBLE);
    }
}
