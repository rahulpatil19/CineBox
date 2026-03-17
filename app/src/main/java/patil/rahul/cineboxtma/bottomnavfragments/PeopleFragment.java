package patil.rahul.cineboxtma.bottomnavfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import patil.rahul.cineboxtma.adapters.PeopleAdapter;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.viewmodels.PeopleViewModel;

/**
 * Created by rahul on 29/1/18.
 */

public class PeopleFragment extends Fragment implements CineListener.OnPeopleClickListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RelativeLayout mPlaceSnackBar;
    private RecyclerView mPeopleRecyclerView;
    private RecyclerView mSearchRecyclerView;
    private PeopleAdapter mPeopleAdapter, mSearchAdapter;
    private SearchView mSearchView;
    private LinearLayout mErrorLayout;
    private TextView mSearchErrorTextView;
    private Button mRetryBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<People> mPeopleList = new ArrayList<>();
    private List<People> mSearchList = new ArrayList<>();

    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isEndOfPage = true;
    private boolean isFirstLoad = true;
    private int totalApiPages = 1;
    private int currentPage = 1;

    public int mQueryLength;
    private CineListener.OnPeopleClickListener onPeopleClickListener;
    private PeopleViewModel viewModel;

    public PeopleFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPeopleAdapter = new PeopleAdapter(getContext(), this);
        viewModel = new ViewModelProvider(this).get(PeopleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_people, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        startShimmer();

        mPlaceSnackBar = view.findViewById(R.id.placeSnackBar);
        mPeopleRecyclerView = view.findViewById(R.id.people_recycler_view);
        mErrorLayout = view.findViewById(R.id.error_layout);
        mRetryBtn = view.findViewById(R.id.retry_btn);
        mSearchErrorTextView = view.findViewById(R.id.search_results);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mSearchRecyclerView = view.findViewById(R.id.people_search_recycler_view);
        mSearchView = view.findViewById(R.id.people_search_view);


        fetchPeople();

        mSearchView.setOnQueryTextListener(this);
        mSearchAdapter = new PeopleAdapter(getContext(), this);
        mSearchRecyclerView.setLayoutManager
                (new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mPeopleRecyclerView.setLayoutManager(layoutManager);
        mPeopleRecyclerView.setHasFixedSize(true);
        mPeopleRecyclerView.setAdapter(mPeopleAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        fetchPeople();
                    }
                });
            }
        };
        mPeopleRecyclerView.addOnScrollListener(scrollListener);
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryFetching();
            }
        });

        return view;
    }

    private void fetchPeople() {
        if (isEndOfPage && currentPage <= totalApiPages) {
            isEndOfPage = false;

            if (!isFirstLoad) {
                mPeopleList.add(null);
                mPeopleAdapter.notifyItemInserted(mPeopleList.size() - 1);
            }

            viewModel.getPopularPeople(currentPage).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getResults() != null) {
                    totalApiPages = response.getTotalPages();
                    int page = response.getPage();
                    currentPage = page + 1;

                    if (!isFirstLoad) {
                        mPeopleList.remove(mPeopleList.size() - 1);
                        mPeopleAdapter.notifyItemRemoved(mPeopleList.size());
                    }

                    mPeopleList.addAll(response.getResults());
                    
                    mErrorLayout.setVisibility(View.GONE);
                    stopShimmer();
                    mPeopleAdapter.addData(mPeopleList);
                    mPeopleRecyclerView.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    int currentSize = mPeopleAdapter.getItemCount();
                    mPeopleAdapter.notifyItemRangeInserted(currentSize, mPeopleList.size() - 1);
                    isFirstLoad = false;
                    isEndOfPage = true;
                } else {
                    handleError();
                }
            });
        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void handleError() {
        stopShimmer();
        mSwipeRefreshLayout.setRefreshing(false);

        if (mPeopleList.size() > 0) {
            if (mPeopleList.get(mPeopleList.size() - 1) == null) {
                mPeopleList.remove(mPeopleList.size() - 1);
                mPeopleAdapter.notifyItemRemoved(mPeopleList.size());
            }
            isEndOfPage = true;
            Snackbar.make(mPlaceSnackBar, "Couldn't load", Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retryFetching();
                }
            }).show();
        } else {
            mErrorLayout.setVisibility(View.VISIBLE);
            mPeopleRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void searchPeople(String query) {
        mSearchErrorTextView.setVisibility(View.INVISIBLE);
        startShimmer();
        viewModel.searchPeople(query).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                if (response.getResults().size() > 0) {
                    mSearchList.addAll(response.getResults());
                    stopShimmer();
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
                    mSearchAdapter.addData(mSearchList);
                    mSearchAdapter.notifyDataSetChanged();
                } else {
                    stopShimmer();
                    mSearchRecyclerView.setVisibility(View.INVISIBLE);
                    mSearchErrorTextView.setText(R.string.no_results_found);
                    mSearchErrorTextView.setVisibility(View.VISIBLE);
                }
            } else {
                stopShimmer();
                mSearchErrorTextView.setVisibility(View.VISIBLE);
                mSearchRecyclerView.setVisibility(View.INVISIBLE);
                mSearchErrorTextView.setText(getString(R.string.no_connection));
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mSearchList.size() > 0) {
            mSearchAdapter.clearAdapter();
            mSearchList.clear();
        }
        hidePeopleRecyclerView();
        searchPeople(query.trim());
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQueryLength = newText.trim().length();

        if (mSearchList.size() > 0) {
            mSearchList.clear();
            mSearchAdapter.clearAdapter();
        }
        if (mQueryLength > 3) {
            hidePeopleRecyclerView();
            searchPeople(newText.trim());
        } else if (mQueryLength <= 3) {
            hideSearchRecyclerView();
            if (mPeopleList.size() > 0) {
                stopShimmer();
                mPeopleRecyclerView.setVisibility(View.VISIBLE);
            } else if (mErrorLayout.getVisibility() == View.VISIBLE) {
                stopShimmer();
                mPeopleRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                startShimmer();
                mPeopleRecyclerView.setVisibility(View.INVISIBLE);
                isEndOfPage = true;
                fetchPeople();
            }
        }
        return true;
    }

    public void clearSearchQuery() {
        mSearchView.setQuery("", true);
        mSearchView.clearFocus();
    }

    private void hidePeopleRecyclerView() {
        mPeopleRecyclerView.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
    }

    private void hideSearchRecyclerView() {
        mSearchRecyclerView.setVisibility(View.INVISIBLE);
        mSearchErrorTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRefresh() {
        if (mSearchRecyclerView.getVisibility() == View.VISIBLE || mSearchErrorTextView.getVisibility() == View.VISIBLE
                || shimmerFrameLayout.getVisibility() == View.VISIBLE) {
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            if (mPeopleList.size() > 0) {
                fetchPeople();
            } else {
                mErrorLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(true);
                resetAllState();
                fetchPeople();
            }
        }
    }


    private void retryFetching() {
        if (mPeopleList.size() > 0) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            startShimmer();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        fetchPeople();
    }

    private void resetAllState() {
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
        mPeopleList.clear();
        mPeopleAdapter.clearAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetAllState();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPeopleClickListener = (CineListener.OnPeopleClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement OnPeopleClickListener");
        }
    }

    public void refreshPeopleList() {
        if (mPeopleList.size() > 0) {
            mPeopleRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onPeopleClick(People people) {
        onPeopleClickListener.onPeopleClick(people);
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