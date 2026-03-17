package patil.rahul.cineboxtma.bottomnavfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.adapters.MovieHorizontalAdapter;
import patil.rahul.cineboxtma.adapters.TvHorizontalAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.viewmodels.HomeViewModel;

/**
 * Created by rahul on 1/2/18.
 */
public class HomeFragment extends Fragment implements
        CineListener.OnMovieClickListener, CineListener.OnTvClickListener {
    public HomeFragment() {
    }

    private ShimmerFrameLayout shimmerReleasingTodayLayout, shimmerAiringTodayLayout,
            shimmerUpcomingLayout;

    private RecyclerView mUpcomingMoviesRecyclerView;
    private RecyclerView mAiringTodayRecyclerView;
    private RecyclerView mReleasingTodayRecyclerView;
    private Button mReleasingTodayRetryBtn, mAiringTodayRetryBtn, mUpcomingMoviesRetryBtn;
    private TextView mReleasingTodayTitle;
    private MovieHorizontalAdapter mUpcomingAdapter;
    private TvHorizontalAdapter mAiringTodayAdapter;
    private MovieHorizontalAdapter mReleasingTodayAdapter;
    private List<Movie> mUpcomingMovieList = new ArrayList<>();
    private List<TvShows> mTvShowList = new ArrayList<>();
    private List<Movie> releasingTodayList = new ArrayList<>();

    private OnMoreClickListener onMoreClickListener;
    private CineListener.OnMovieClickListener onMovieClickListener;
    private CineListener.OnTvClickListener onTvClickListener;

    private HomeViewModel viewModel;


    @Override
    public void onMovieClick(Movie movie) {
        onMovieClickListener.onMovieClick(movie);
    }

    @Override
    public void onTvClick(TvShows tvShows) {
        onTvClickListener.onTvClick(tvShows);
    }

    public interface OnMoreClickListener {
        void onMoreClick(String mediaType);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageQuality = CinePreferences.getImageQualityValue(getContext());
        mReleasingTodayAdapter = new MovieHorizontalAdapter(getContext(), this, imageQuality);
        mAiringTodayAdapter = new TvHorizontalAdapter(getContext(), this, imageQuality);
        mUpcomingAdapter = new MovieHorizontalAdapter(getContext(), this, imageQuality);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_home, container, false);


        shimmerReleasingTodayLayout = view.findViewById(R.id.shimmer_layout_releasing_today);
        shimmerAiringTodayLayout = view.findViewById(R.id.shimmer_layout_airing_today);
        shimmerUpcomingLayout = view.findViewById(R.id.shimmer_layout_upcoming);


        startAiringTodayShimmer();
        startReleasingTodayShimmer();
        startUpcomingShimmer();

        MaterialButton airingTodayMoreBtn = view.findViewById(R.id.airing_today_more_btn);
        MaterialButton upcomingMovieMoreBtn = view.findViewById(R.id.upcoming_movies_more_btn);
        setupViews(view);
        mReleasingTodayTitle = view.findViewById(R.id.releasing_today_title);
        mAiringTodayRecyclerView.setAdapter(mAiringTodayAdapter);
        mUpcomingMoviesRecyclerView.setAdapter(mUpcomingAdapter);
        mReleasingTodayRecyclerView.setAdapter(mReleasingTodayAdapter);
        upcomingMovieMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreClickListener.onMoreClick("movie");
            }
        });
        airingTodayMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreClickListener.onMoreClick("tv");
            }
        });

        mReleasingTodayRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReleasingTodayRetryBtn.setVisibility(View.GONE);
                startReleasingTodayShimmer();
                fetchReleasingTodayMovies();
            }
        });

        mAiringTodayRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAiringTodayRetryBtn.setVisibility(View.GONE);
                startAiringTodayShimmer();
                fetchAiringToday();
            }
        });

        mUpcomingMoviesRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpcomingMoviesRetryBtn.setVisibility(View.GONE);
                startUpcomingShimmer();
                fetchUpcomingMovies();
            }
        });

        fetchReleasingTodayMovies();
        fetchUpcomingMovies();
        fetchAiringToday();

        return view;
    }

    private void fetchReleasingTodayMovies() {
        viewModel.getReleasingTodayMovies().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                if (response.getResults().size() > 0) {
                    releasingTodayList.clear();
                    releasingTodayList.addAll(response.getResults());
                    mReleasingTodayAdapter.addData(releasingTodayList);
                    stopRealisingTodayShimmer();
                    mReleasingTodayAdapter.notifyDataSetChanged();
                } else {
                    mReleasingTodayTitle.setVisibility(View.GONE);
                    stopRealisingTodayShimmer();
                    mReleasingTodayRecyclerView.setVisibility(View.GONE);
                    mReleasingTodayRetryBtn.setVisibility(View.GONE);
                }
            } else {
                mReleasingTodayRetryBtn.setVisibility(View.VISIBLE);
                stopRealisingTodayShimmer();
            }
        });
    }

    private void fetchAiringToday() {
        viewModel.getAiringTodayTvShows().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                mTvShowList.clear();
                mTvShowList.addAll(response.getResults());
                mAiringTodayAdapter.addData(mTvShowList);
                stopAiringTodayShimmer();
                mAiringTodayAdapter.notifyDataSetChanged();
            } else {
                stopAiringTodayShimmer();
                mAiringTodayRetryBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchUpcomingMovies() {
        viewModel.getUpcomingMovies().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getResults() != null) {
                mUpcomingMovieList.clear();
                mUpcomingMovieList.addAll(response.getResults());
                stopUpcomingShimmer();
                mUpcomingAdapter.addData(mUpcomingMovieList);
                mUpcomingAdapter.notifyDataSetChanged();
            } else {
                stopUpcomingShimmer();
                mUpcomingMoviesRetryBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onMoreClickListener = (OnMoreClickListener) context;
            onMovieClickListener = (CineListener.OnMovieClickListener) context;
            onTvClickListener = (CineListener.OnTvClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement");
        }
    }

    private void setupViews(View view) {

        mReleasingTodayRecyclerView = view.findViewById(R.id.releasing_today_recycler_view);
        mAiringTodayRecyclerView = view.findViewById(R.id.airing_today_recycler_view);
        mUpcomingMoviesRecyclerView = view.findViewById(R.id.upcoming_movies_recycler_view);

        mReleasingTodayRecyclerView.setNestedScrollingEnabled(false);
        mAiringTodayRecyclerView.setNestedScrollingEnabled(false);
        mUpcomingMoviesRecyclerView.setNestedScrollingEnabled(false);

        mReleasingTodayRetryBtn = view.findViewById(R.id.releasing_today_retry_btn);
        mAiringTodayRetryBtn = view.findViewById(R.id.airing_today_retry_btn);
        mUpcomingMoviesRetryBtn = view.findViewById(R.id.upcoming_movies_retry_btn);

        mReleasingTodayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mUpcomingMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAiringTodayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void startReleasingTodayShimmer(){
        shimmerReleasingTodayLayout.startShimmer();
        shimmerReleasingTodayLayout.setVisibility(View.VISIBLE);
    }

    private void startAiringTodayShimmer(){
        shimmerAiringTodayLayout.startShimmer();
        shimmerAiringTodayLayout.setVisibility(View.VISIBLE);
    }

    private void startUpcomingShimmer(){
        shimmerUpcomingLayout.startShimmer();
        shimmerUpcomingLayout.setVisibility(View.VISIBLE);
    }

    private void stopRealisingTodayShimmer() {
        shimmerReleasingTodayLayout.stopShimmer();
        shimmerReleasingTodayLayout.setVisibility(View.INVISIBLE);
    }

    private void stopAiringTodayShimmer() {
        shimmerAiringTodayLayout.stopShimmer();
        shimmerAiringTodayLayout.setVisibility(View.INVISIBLE);
    }

    private void stopUpcomingShimmer() {
        shimmerUpcomingLayout.stopShimmer();
        shimmerUpcomingLayout.setVisibility(View.INVISIBLE);
    }
}
