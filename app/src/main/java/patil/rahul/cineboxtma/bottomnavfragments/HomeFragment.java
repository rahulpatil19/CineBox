package patil.rahul.cineboxtma.bottomnavfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

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
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.adapters.MovieHorizontalAdapter;
import patil.rahul.cineboxtma.adapters.TvHorizontalAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.MySingleton;

/**
 * Created by rahul on 1/2/18.
 */
public class HomeFragment extends Fragment implements
        CineListener.OnMovieClickListener, CineListener.OnTvClickListener {
    public HomeFragment() {
    }

    private ProgressBar mAiringTodayProgressBar, mUpcomingMoviesProgressBar, mReleasingTodayProgressBar;
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

        fetchReleasingTodayMovies();
        fetchUpcomingMovies();
        fetchAiringToday();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_home, container, false);

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
                mReleasingTodayProgressBar.setVisibility(View.VISIBLE);
                fetchReleasingTodayMovies();
            }
        });

        mAiringTodayRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAiringTodayRetryBtn.setVisibility(View.GONE);
                mAiringTodayProgressBar.setVisibility(View.VISIBLE);
                fetchAiringToday();
            }
        });

        mUpcomingMoviesRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpcomingMoviesRetryBtn.setVisibility(View.GONE);
                mUpcomingMoviesProgressBar.setVisibility(View.VISIBLE);
                fetchUpcomingMovies();
            }
        });

        return view;
    }

    private void fetchReleasingTodayMovies() {

        String releasingTodayUrl = CineUrl.createReleasingTodayMovieUrl(1);

        JsonObjectRequest releasingTodayRequest = new JsonObjectRequest(Request.Method.GET, releasingTodayUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray carouselArray = response.getJSONArray("results");

                    if (carouselArray.length() > 0) {
                        for (int i = 0; i < carouselArray.length(); i++) {
                            JSONObject currentMovie = carouselArray.getJSONObject(i);
                            int id = currentMovie.getInt("id");
                            String posterPath = currentMovie.getString("poster_path");
                            String title = currentMovie.getString("title");
                            String release_date = currentMovie.getString("release_date");
                            releasingTodayList.add(new Movie(id, title, posterPath, release_date, false));
                        }

                        mReleasingTodayAdapter.addData(releasingTodayList);
                        mReleasingTodayProgressBar.setVisibility(View.INVISIBLE);
                        mReleasingTodayAdapter.notifyDataSetChanged();
                    }
                    else {
                        mReleasingTodayTitle.setVisibility(View.GONE);
                        mReleasingTodayRecyclerView.setVisibility(View.GONE);
                        mReleasingTodayProgressBar.setVisibility(View.GONE);
                        mReleasingTodayRetryBtn.setVisibility(View.GONE);
                    }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mReleasingTodayProgressBar.setVisibility(View.INVISIBLE);
                mReleasingTodayRetryBtn.setVisibility(View.VISIBLE);
            }
        });
        releasingTodayRequest.setTag(CineTag.RELEASING_TODAY);
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(releasingTodayRequest);
    }

    private void fetchAiringToday() {
        String airingTodayUrl = CineUrl.createTvListUrl("airing_today", 1);
        JsonObjectRequest airingTodayRequest = new JsonObjectRequest(Request.Method.GET, airingTodayUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray tvShowsArray = response.getJSONArray("results");

                    for (int i = 0; i < tvShowsArray.length(); i++) {
                        JSONObject currentShow = tvShowsArray.getJSONObject(i);
                        int id = currentShow.getInt("id");
                        String posterPath = currentShow.getString("poster_path");
                        String name = currentShow.getString("name");
                        String firstAirDate = currentShow.getString("first_air_date");
                        mTvShowList.add(new TvShows(id, name, posterPath, firstAirDate));
                    }
                    mAiringTodayAdapter.addData(mTvShowList);
                    mAiringTodayProgressBar.setVisibility(View.INVISIBLE);
                    mAiringTodayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAiringTodayProgressBar.setVisibility(View.INVISIBLE);
                mAiringTodayRetryBtn.setVisibility(View.VISIBLE);
            }
        });
        airingTodayRequest.setTag(CineTag.TV_AIRING_TODAY_TAG);
        MySingleton.getInstance(getContext()).addToRequestQueue(airingTodayRequest);
    }

    private void fetchUpcomingMovies() {
        String upcomingMovieUrl = CineUrl.createUpcomingMovieUrl(1);
        JsonObjectRequest upcomingMoviesRequest = new JsonObjectRequest(Request.Method.GET, upcomingMovieUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray topRatedArray = response.getJSONArray("results");
                    for (int i = 0; i < topRatedArray.length(); i++) {
                        JSONObject currentMovie = topRatedArray.getJSONObject(i);
                        int id = currentMovie.getInt("id");
                        String posterPath = currentMovie.getString("poster_path");
                        String movieTitle = currentMovie.getString("title");
                        String releaseDate = currentMovie.getString("release_date");
                        mUpcomingMovieList.add(new Movie(id, movieTitle, posterPath, releaseDate, true));
                    }
                    mUpcomingMoviesProgressBar.setVisibility(View.INVISIBLE);
                    mUpcomingAdapter.addData(mUpcomingMovieList);
                    mUpcomingAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mUpcomingMoviesProgressBar.setVisibility(View.INVISIBLE);
                mUpcomingMoviesRetryBtn.setVisibility(View.VISIBLE);
            }
        });
        upcomingMoviesRequest.setTag(CineTag.MOVIE_UPCOMING_TAG);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(upcomingMoviesRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.RELEASING_TODAY);
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.TV_AIRING_TODAY_TAG);
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.MOVIE_UPCOMING_TAG);
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
        mAiringTodayProgressBar = view.findViewById(R.id.airing_today_progress_bar);
        mUpcomingMoviesProgressBar = view.findViewById(R.id.upcoming_movies_progress_bar);
        mReleasingTodayProgressBar = view.findViewById(R.id.releasing_today_progress_bar);

        mReleasingTodayRetryBtn = view.findViewById(R.id.releasing_today_retry_btn);
        mAiringTodayRetryBtn = view.findViewById(R.id.airing_today_retry_btn);
        mUpcomingMoviesRetryBtn = view.findViewById(R.id.upcoming_movies_retry_btn);

        mReleasingTodayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mUpcomingMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAiringTodayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mReleasingTodayRecyclerView.setNestedScrollingEnabled(false);
        mUpcomingMoviesRecyclerView.setNestedScrollingEnabled(false);
        mAiringTodayRecyclerView.setNestedScrollingEnabled(false);

    }
}
