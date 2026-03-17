package patil.rahul.cineboxtma.tabfragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.SearchActivity;
import patil.rahul.cineboxtma.adapters.MovieAdapter;
import patil.rahul.cineboxtma.adapters.PeopleAdapter;
import patil.rahul.cineboxtma.adapters.TvAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.viewmodels.SearchViewModel;

/**
 * Created by rahul on 11/3/18.
 */

public class SearchFragment extends Fragment implements CineListener.OnMovieClickListener, CineListener.OnTvClickListener,
        CineListener.OnPeopleClickListener {
    public SearchFragment() {
    }

    public static SearchFragment newInstance(int searchType) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("search_type", searchType);
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    private List<Movie> mMovieList = new ArrayList<>();
    private List<People> mPeopleList = new ArrayList<>();
    private List<TvShows> mTvList = new ArrayList<>();

    private CineListener.OnMovieClickListener onMovieClickListener;
    private CineListener.OnPeopleClickListener onPeopleClickListener;
    private CineListener.OnTvClickListener onTvClickListener;
    private int searchType;
    private TextView mSearchQueryTextView;
    private LinearLayout mSearchLayout;
    private PeopleAdapter mPeopleAdapter;
    private MovieAdapter mMovieAdapter;
    private TvAdapter mTvAdapter;
    private ProgressBar mProgressBar;
    private SearchViewModel viewModel;

    public static final String SEARCH_TYPE = "search_type";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String imageQuality = CinePreferences.getImageQualityValue(getContext());
        searchType = getArguments().getInt(SEARCH_TYPE);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        if (searchType == SearchActivity.SEARCH_TYPE_MOVIE) {
            mMovieAdapter = new MovieAdapter(getContext(), this, imageQuality);
        } else if (searchType == SearchActivity.SEARCH_TYPE_PEOPLE) {
            mPeopleAdapter = new PeopleAdapter(getContext(), this);
        } else if (searchType == SearchActivity.SEARCH_TYPE_TV_SHOW) {
            mTvAdapter = new TvAdapter(getContext(), this, imageQuality, (AppCompatActivity) getActivity());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchLayout = view.findViewById(R.id.search_progress_layout);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mSearchQueryTextView = view.findViewById(R.id.search_query);
        RecyclerView mSearchRecyclerView = view.findViewById(R.id.search_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mSearchRecyclerView.setLayoutManager(layoutManager);

        if (searchType == SearchActivity.SEARCH_TYPE_MOVIE) {
            mSearchRecyclerView.setAdapter(mMovieAdapter);
        } else if (searchType == SearchActivity.SEARCH_TYPE_PEOPLE) {
            mSearchRecyclerView.setAdapter(mPeopleAdapter);
        } else if (searchType == SearchActivity.SEARCH_TYPE_TV_SHOW) {
            mSearchRecyclerView.setAdapter(mTvAdapter);
        }
        return view;
    }

    public void searchMovie(final String query) {
        mSearchQueryTextView.setText(String.format("Searching for %s", query));
        mSearchLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        
        viewModel.searchMovies(query).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                if (response.getResults().size() > 0) {
                    mMovieList.clear();
                    mMovieList.addAll(response.getResults());
                    mSearchLayout.setVisibility(View.GONE);
                    mMovieAdapter.addData(mMovieList);
                    mMovieAdapter.notifyDataSetChanged();
                } else {
                    mSearchLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSearchQueryTextView.setText(String.format("No Results found for %s", query));
                }
            } else {
                mSearchLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSearchQueryTextView.setText(getString(R.string.no_connection));
            }
        });
    }

    public void searchTvShow(final String query) {
        mSearchLayout.setVisibility(View.VISIBLE);
        mSearchQueryTextView.setText(String.format("Searching for %s", query));
        mProgressBar.setVisibility(View.VISIBLE);
        
        viewModel.searchTvShows(query).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                if (response.getResults().size() > 0) {
                    mTvList.clear();
                    mTvList.addAll(response.getResults());
                    mSearchLayout.setVisibility(View.GONE);
                    mTvAdapter.addData(mTvList);
                    mTvAdapter.notifyDataSetChanged();
                } else {
                    mSearchLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSearchQueryTextView.setText(String.format("No Results found for %s", query));
                }
            } else {
                mSearchLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSearchQueryTextView.setText(getString(R.string.no_connection));
            }
        });
    }

    public void searchPeople(final String query) {
        mSearchLayout.setVisibility(View.VISIBLE);
        mSearchQueryTextView.setText(String.format("Searching for %s", query));
        mProgressBar.setVisibility(View.VISIBLE);
        
        viewModel.searchPeople(query).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                if (response.getResults().size() > 0) {
                    mPeopleList.clear();
                    mPeopleList.addAll(response.getResults());
                    mSearchLayout.setVisibility(View.GONE);
                    mPeopleAdapter.addData(mPeopleList);
                    mPeopleAdapter.notifyDataSetChanged();
                } else {
                    mSearchLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSearchQueryTextView.setText(String.format("No Results found for %s", query));
                }
            } else {
                mProgressBar.setVisibility(View.GONE);
                mSearchLayout.setVisibility(View.VISIBLE);
                mSearchQueryTextView.setText(getString(R.string.no_connection));
            }
        });
    }

    public void clearMovieAdapter() {
        if (mMovieList.size() > 0) {
            mMovieAdapter.clearAdapter();
            mMovieList.clear();
        }
    }

    public void clearPeopleAdapter() {
        if (mPeopleList.size() > 0) {
            mPeopleAdapter.clearAdapter();
            mPeopleList.clear();
        }
    }

    public void clearTvAdapter() {
        if (mTvList.size() > 0) {
            mTvAdapter.clearAdapter();
            mTvList.clear();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onMovieClickListener = (CineListener.OnMovieClickListener) context;
            onPeopleClickListener = (CineListener.OnPeopleClickListener) context;
            onTvClickListener = (CineListener.OnTvClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "Must implement CineListener");
        }

    }

    @Override
    public void onMovieClick(Movie movie) {
        onMovieClickListener.onMovieClick(movie);
    }

    @Override
    public void onPeopleClick(People people) {
        onPeopleClickListener.onPeopleClick(people);
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        onTvClickListener.onTvClick(tvShows);
    }
}
