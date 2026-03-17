package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.models.PeopleResponse;
import patil.rahul.cineboxtma.repository.PeopleRepository;

public class PeopleViewModel extends ViewModel {
    private PeopleRepository repository;

    public PeopleViewModel() {
        repository = new PeopleRepository();
    }

    public LiveData<PeopleResponse> getPopularPeople(int page) {
        return repository.getPopularPeople(page);
    }

    public LiveData<PeopleResponse> searchPeople(String query) {
        return repository.searchPeople(query);
    }
}
