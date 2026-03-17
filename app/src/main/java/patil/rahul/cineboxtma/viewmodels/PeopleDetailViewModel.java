package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.BuildConfig;
import patil.rahul.cineboxtma.models.PeopleDetailResponse;
import patil.rahul.cineboxtma.network.RetrofitClient;
import patil.rahul.cineboxtma.network.TmdbApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleDetailViewModel extends ViewModel {
    private TmdbApiService apiService;
    private String apiKey = BuildConfig.TMDB_API_KEY;

    public PeopleDetailViewModel() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<PeopleDetailResponse> getPersonDetail(int personId) {
        MutableLiveData<PeopleDetailResponse> data = new MutableLiveData<>();
        apiService.getPersonDetail(personId, apiKey, "en-US", "combined_credits,images,external_ids").enqueue(new Callback<PeopleDetailResponse>() {
            @Override
            public void onResponse(Call<PeopleDetailResponse> call, Response<PeopleDetailResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PeopleDetailResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
