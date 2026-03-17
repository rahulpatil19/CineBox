package patil.rahul.cineboxtma.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import patil.rahul.cineboxtma.BuildConfig;
import patil.rahul.cineboxtma.models.PeopleResponse;
import patil.rahul.cineboxtma.network.RetrofitClient;
import patil.rahul.cineboxtma.network.TmdbApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleRepository {
    private TmdbApiService apiService;
    private String apiKey = BuildConfig.TMDB_API_KEY;

    public PeopleRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<PeopleResponse> getPopularPeople(int page) {
        MutableLiveData<PeopleResponse> data = new MutableLiveData<>();
        apiService.getPopularPeople(apiKey, page).enqueue(new Callback<PeopleResponse>() {
            @Override
            public void onResponse(Call<PeopleResponse> call, Response<PeopleResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PeopleResponse> call, Throwable t) {
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
