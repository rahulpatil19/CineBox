package patil.rahul.cineboxtma.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import patil.rahul.cineboxtma.BuildConfig;
import patil.rahul.cineboxtma.models.MovieDetailResponse;
import patil.rahul.cineboxtma.models.MovieResponse;
import patil.rahul.cineboxtma.network.RetrofitClient;
import patil.rahul.cineboxtma.network.TmdbApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private TmdbApiService apiService;
    private String apiKey = BuildConfig.TMDB_API_KEY;

    public MovieRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<MovieResponse> getMovies(String category, int page) {
        MutableLiveData<MovieResponse> data = new MutableLiveData<>();
        apiService.getMovies(category, apiKey, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<MovieResponse> discoverMovies(int page, String gteDate, String lteDate) {
        MutableLiveData<MovieResponse> data = new MutableLiveData<>();
        apiService.discoverMovies(apiKey, page, gteDate, lteDate).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<MovieDetailResponse> getMovieDetail(int movieId) {
        MutableLiveData<MovieDetailResponse> data = new MutableLiveData<>();
        apiService.getMovieDetail(movieId, apiKey, "external_ids,images,videos,credits,similar_movies").enqueue(new Callback<MovieDetailResponse>() {
            @Override
            public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
