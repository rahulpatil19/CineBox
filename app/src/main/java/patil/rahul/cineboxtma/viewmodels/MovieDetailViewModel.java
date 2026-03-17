package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import patil.rahul.cineboxtma.models.MovieDetailResponse;
import patil.rahul.cineboxtma.repository.MovieRepository;

public class MovieDetailViewModel extends ViewModel {
    private MovieRepository repository;

    public MovieDetailViewModel() {
        repository = new MovieRepository();
    }

    public LiveData<MovieDetailResponse> getMovieDetail(int movieId) {
        return repository.getMovieDetail(movieId);
    }
}
