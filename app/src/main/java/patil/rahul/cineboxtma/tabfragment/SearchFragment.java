package patil.rahul.cineboxtma.tabfragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

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
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.SearchActivity;
import patil.rahul.cineboxtma.adapters.MovieAdapter;
import patil.rahul.cineboxtma.adapters.PeopleAdapter;
import patil.rahul.cineboxtma.adapters.TvAdapter;
import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvShows;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.preferenceutils.CinePreferences;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.MySingleton;

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

    public static final String SEARCH_TYPE = "search_type";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String imageQuality = CinePreferences.getImageQualityValue(getContext());
        searchType = getArguments().getInt(SEARCH_TYPE);

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
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.SEARCH_MOVIE_TAG);
        mSearchQueryTextView.setText(String.format("Searching for %s", query));
        mSearchLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        String url = createSearchUrl("movie", query);
        JsonObjectRequest movieSearchRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray movieArray = response.getJSONArray("results");

                    if (movieArray.length() > 0) {

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
                        mSearchLayout.setVisibility(View.GONE);
                        mMovieAdapter.addData(mMovieList);
                        mMovieAdapter.notifyDataSetChanged();
                    } else {
                        mSearchLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mSearchQueryTextView.setText(String.format("No Results found for %s", query));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSearchLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSearchQueryTextView.setText(getString(R.string.no_connection));
            }
        });
        movieSearchRequest.setTag(CineTag.SEARCH_MOVIE_TAG);
        MySingleton.getInstance(getContext()).getRequestQueue().add(movieSearchRequest);
    }

    public void searchTvShow(final String query) {
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.SEARCH_TV_TAG);
        mSearchLayout.setVisibility(View.VISIBLE);
        mSearchQueryTextView.setText(String.format("Searching for %s", query));
        mProgressBar.setVisibility(View.VISIBLE);
        String url = createSearchUrl("tv", query);
        JsonObjectRequest tvShowRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray tvArray = response.getJSONArray("results");
                    if (tvArray.length() > 0) {

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
                        mSearchLayout.setVisibility(View.GONE);
                        mTvAdapter.addData(mTvList);
                        mTvAdapter.notifyDataSetChanged();
                    } else {
                        mSearchLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mSearchQueryTextView.setText(String.format("No Results found for %s", query));
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSearchLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSearchQueryTextView.setText(getString(R.string.no_connection));
            }
        });
        tvShowRequest.setTag(CineTag.SEARCH_TV_TAG);
        MySingleton.getInstance(getContext()).getRequestQueue().add(tvShowRequest);
    }

    public void searchPeople(final String query) {
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.SEARCH_PEOPLE_TAG);
        mSearchLayout.setVisibility(View.VISIBLE);
        mSearchQueryTextView.setText(String.format("Searching for %s", query));
        mProgressBar.setVisibility(View.VISIBLE);
        String url = createSearchUrl("person", query);
        JsonObjectRequest peopleSearchRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray movieResultsArray = response.getJSONArray("results");

                    if (movieResultsArray != null) {

                        for (int i = 0; i < movieResultsArray.length(); i++) {
                            JSONObject currentObject = movieResultsArray.getJSONObject(i);

                            int id = currentObject.getInt("id");
                            String name = currentObject.getString("name");
                            String poster_path = currentObject.getString("profile_path");
                            mPeopleList.add(new People(id, name, poster_path));
                        }
                        mSearchLayout.setVisibility(View.GONE);
                        mPeopleAdapter.addData(mPeopleList);
                        mPeopleAdapter.notifyDataSetChanged();
                    }
                    else {
                        mSearchLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mSearchQueryTextView.setText(String.format("No Results found for %s", query));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                mSearchLayout.setVisibility(View.VISIBLE);
                mSearchQueryTextView.setText(getString(R.string.no_connection));
            }
        });
        peopleSearchRequest.setTag(CineTag.SEARCH_PEOPLE_TAG);
        MySingleton.getInstance(getContext()).getRequestQueue().add(peopleSearchRequest);
    }

    private String createSearchUrl(String type, String query) {
        return CineUrl.createSearchUrl(type, query);
    }

    public void clearMovieAdapter() {
        if (mMovieList.size() > 0) {
            mMovieAdapter.clearAdapter();
        }
    }

    public void clearPeopleAdapter() {
        if (mPeopleList.size() > 0) {
            mPeopleAdapter.clearAdapter();
        }
    }

    public void clearTvAdapter() {
        if (mTvList.size() > 0) {
            mTvAdapter.clearAdapter();
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
