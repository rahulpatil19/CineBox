package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.models.MovieResponse;
import patil.rahul.cineboxtma.repository.MovieRepository;

public class MovieViewModel extends ViewModel {
    private MovieRepository repository;

    public MovieViewModel() {
        repository = new MovieRepository();
    }

    public LiveData<MovieResponse> getMovies(String category, int page) {
        return repository.getMovies(category, page);
    }
}
