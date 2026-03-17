package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.models.TvResponse;
import patil.rahul.cineboxtma.repository.TvRepository;

public class TvViewModel extends ViewModel {
    private TvRepository repository;

    public TvViewModel() {
        repository = new TvRepository();
    }

    public LiveData<TvResponse> getTvShows(String category, int page) {
        return repository.getTvShows(category, page);
    }
}
