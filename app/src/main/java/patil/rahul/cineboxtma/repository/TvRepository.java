package patil.rahul.cineboxtma.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import patil.rahul.cineboxtma.BuildConfig;
import patil.rahul.cineboxtma.models.TvResponse;
import patil.rahul.cineboxtma.network.RetrofitClient;
import patil.rahul.cineboxtma.network.TmdbApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvRepository {
    private TmdbApiService apiService;
    private String apiKey = BuildConfig.TMDB_API_KEY;

    public TvRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<TvResponse> getTvShows(String category, int page) {
        MutableLiveData<TvResponse> data = new MutableLiveData<>();
        apiService.getTvShows(category, apiKey, page).enqueue(new Callback<TvResponse>() {
            @Override
            public void onResponse(Call<TvResponse> call, Response<TvResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<TvResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
