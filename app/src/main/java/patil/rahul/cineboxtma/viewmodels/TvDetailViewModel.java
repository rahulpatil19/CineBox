package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.BuildConfig;
import patil.rahul.cineboxtma.models.TvDetailResponse;
import patil.rahul.cineboxtma.models.TvResponse;
import patil.rahul.cineboxtma.network.RetrofitClient;
import patil.rahul.cineboxtma.network.TmdbApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvDetailViewModel extends ViewModel {
    private TmdbApiService apiService;
    private String apiKey = BuildConfig.TMDB_API_KEY;

    public TvDetailViewModel() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<TvDetailResponse> getTvShowDetail(int tvId) {
        MutableLiveData<TvDetailResponse> data = new MutableLiveData<>();
        apiService.getTvShowDetail(tvId, apiKey, "images,credits,external_ids,videos").enqueue(new Callback<TvDetailResponse>() {
            @Override
            public void onResponse(Call<TvDetailResponse> call, Response<TvDetailResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<TvDetailResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<TvDetailResponse> getSeasonVideos(int tvId, int seasonNumber) {
        MutableLiveData<TvDetailResponse> data = new MutableLiveData<>();
        apiService.getTvSeasonDetail(tvId, seasonNumber, apiKey, "videos").enqueue(new Callback<TvDetailResponse>() {
            @Override
            public void onResponse(Call<TvDetailResponse> call, Response<TvDetailResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<TvDetailResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
