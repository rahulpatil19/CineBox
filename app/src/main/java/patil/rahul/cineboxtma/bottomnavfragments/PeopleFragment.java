package patil.rahul.cineboxtma.bottomnavfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.adapters.PeopleAdapter;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.utils.CineListener;
import patil.rahul.cineboxtma.utils.CineTag;
import patil.rahul.cineboxtma.utils.CineUrl;
import patil.rahul.cineboxtma.utils.EndlessRecyclerViewScrollListener;
import patil.rahul.cineboxtma.utils.MySingleton;

/**
 * Created by rahul on 29/1/18.
 */

public class PeopleFragment extends Fragment implements CineListener.OnPeopleClickListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private RelativeLayout mPlaceSnackBar;
    private RecyclerView mPeopleRecyclerView;
    private RecyclerView mSearchRecyclerView;
    private PeopleAdapter mPeopleAdapter, mSearchAdapter;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
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

    public PeopleFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPeopleAdapter = new PeopleAdapter(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_people, container, false);
        mPlaceSnackBar = view.findViewById(R.id.placeSnackBar);
        mPeopleRecyclerView = view.findViewById(R.id.people_recycler_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
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

            String completePeopleUrl = buildPeopleUrl(currentPage);
            final JsonObjectRequest peopleRequest = new JsonObjectRequest(Request.Method.GET, completePeopleUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        totalApiPages = response.getInt("total_pages");
                        int page = response.getInt("page");
                        JSONArray peopleArray = response.getJSONArray("results");

                        /* if peopleArray length is greater than 0 then we increment current page value + 1
                        & if it is not first load then remove the already inserted progress bar from the bottom
                        & adds the people array data to the people array list*/
                        if (peopleArray.length() > 0) {
                            currentPage = page + 1;
                            if (!isFirstLoad) {
                                mPeopleList.remove(mPeopleList.size() - 1);
                                mPeopleAdapter.notifyItemRemoved(mPeopleList.size());
                            }
                            for (int i = 0; i < peopleArray.length(); i++) {
                                JSONObject currentObject = peopleArray.getJSONObject(i);
                                int id = currentObject.getInt("id");
                                String profile_path = currentObject.getString("profile_path");
                                String name = currentObject.getString("name");

                                mPeopleList.add(new People(id, name, profile_path));
                            }
                            mErrorLayout.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mPeopleAdapter.addData(mPeopleList);
                            mPeopleRecyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            int currentSize = mPeopleAdapter.getItemCount();
                            mPeopleAdapter.notifyItemRangeInserted(currentSize, mPeopleList.size() - 1);
                            isFirstLoad = false;
                            isEndOfPage = true;
                        }

                        else if (peopleArray.length() == 0 && mPeopleList.size() > 0) {
                            mPeopleList.remove(mPeopleList.size() - 1);
                            mPeopleAdapter.notifyItemRemoved(mPeopleList.size());
                            isEndOfPage = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (mPeopleList.size() > 0) {
                        mPeopleList.remove(mPeopleList.size() - 1);
                        mPeopleAdapter.notifyItemRemoved(mPeopleList.size());
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
            });
            peopleRequest.setTag(CineTag.PEOPLE_REQUEST);
            MySingleton.getInstance(getContext()).addToRequestQueue(peopleRequest);
        } else {
            if (!isEndOfPage) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void searchPeople(String query) {
        final List<String> knownForList = new ArrayList<>();
        mSearchErrorTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        String url = createSearchUrl(query);
        final JsonObjectRequest peopleSearchRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray movieResultsArray = response.getJSONArray("results");

                    if (movieResultsArray.length() > 0) {

                        for (int i = 0; i < movieResultsArray.length(); i++) {
                            JSONObject currentObject = movieResultsArray.getJSONObject(i);

                            int id = currentObject.getInt("id");
                            String name = currentObject.getString("name");
                            String poster_path = currentObject.getString("profile_path");

                            mSearchList.add(new People(id, name, poster_path));
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mSearchRecyclerView.setVisibility(View.VISIBLE);
                        mSearchAdapter.addData(mSearchList);
                        mSearchAdapter.notifyDataSetChanged();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mSearchRecyclerView.setVisibility(View.INVISIBLE);
                        mSearchErrorTextView.setText(R.string.no_results_found);
                        mSearchErrorTextView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                mSearchErrorTextView.setVisibility(View.VISIBLE);
                mSearchRecyclerView.setVisibility(View.INVISIBLE);
                mSearchErrorTextView.setText(getString(R.string.no_connection));
            }
        });
        peopleSearchRequest.setTag(CineTag.SEARCH_PEOPLE_TAG);
        MySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().add(peopleSearchRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.SEARCH_PEOPLE_TAG);
        if (mSearchList.size() > 0) {
            mSearchAdapter.clearAdapter();
        }
        hidePeopleRecyclerView();
        searchPeople(query.trim());
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.SEARCH_PEOPLE_TAG);
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
                mProgressBar.setVisibility(View.INVISIBLE);
                mPeopleRecyclerView.setVisibility(View.VISIBLE);
            } else if (mErrorLayout.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mPeopleRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
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
                || mProgressBar.getVisibility() == View.VISIBLE) {
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
            fetchPeople();
        } else {
            resetAllState();
            mErrorLayout.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            fetchPeople();
        }
    }

    private void resetAllState() {
        MySingleton.getInstance(getContext()).getRequestQueue().cancelAll(CineTag.PEOPLE_REQUEST);
        scrollListener.resetState();
        currentPage = 1;
        isFirstLoad = true;
        isEndOfPage = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetAllState();
        if (mPeopleList.size() > 0) {
            mPeopleList.clear();
        }
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

    private String buildPeopleUrl(int currentPage) {
        return CineUrl.createPersonListUrl(currentPage);
    }

    private String createSearchUrl(String query) {
        return CineUrl.createSearchUrl("person", query);
    }

    public void refreshPeopleList(){
        if (mPeopleList.size() > 0){
            mPeopleRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onPeopleClick(People people) {
        onPeopleClickListener.onPeopleClick(people);
    }
}