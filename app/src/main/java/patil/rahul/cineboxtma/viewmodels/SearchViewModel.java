package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.BuildConfig;
import patil.rahul.cineboxtma.models.MovieResponse;
import patil.rahul.cineboxtma.models.PeopleResponse;
import patil.rahul.cineboxtma.models.TvResponse;
import patil.rahul.cineboxtma.network.RetrofitClient;
import patil.rahul.cineboxtma.network.TmdbApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private TmdbApiService apiService;
    private String apiKey = BuildConfig.TMDB_API_KEY;

    public SearchViewModel() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<MovieResponse> searchMovies(String query) {
        MutableLiveData<MovieResponse> data = new MutableLiveData<>();
        apiService.searchMovies("movie", apiKey, query).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<TvResponse> searchTvShows(String query) {
        MutableLiveData<TvResponse> data = new MutableLiveData<>();
        apiService.searchTvShows("tv", apiKey, query).enqueue(new Callback<TvResponse>() {
            @Override
            public void onResponse(Call<TvResponse> call, Response<TvResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<TvResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<PeopleResponse> searchPeople(String query) {
        MutableLiveData<PeopleResponse> data = new MutableLiveData<>();
        apiService.searchPeople("person", apiKey, query).enqueue(new Callback<PeopleResponse>() {
            @Override
            public void onResponse(Call<PeopleResponse> call, Response<PeopleResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PeopleResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
