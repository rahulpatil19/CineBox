package patil.rahul.cineboxtma.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import patil.rahul.cineboxtma.models.MovieResponse;
import patil.rahul.cineboxtma.models.TvResponse;
import patil.rahul.cineboxtma.repository.MovieRepository;
import patil.rahul.cineboxtma.repository.TvRepository;

public class HomeViewModel extends ViewModel {
    private MovieRepository movieRepository;
    private TvRepository tvRepository;

    public HomeViewModel() {
        movieRepository = new MovieRepository();
        tvRepository = new TvRepository();
    }

    public LiveData<MovieResponse> getUpcomingMovies() {
        return movieRepository.getMovies("upcoming", 1);
    }

    public LiveData<TvResponse> getAiringTodayTvShows() {
        return tvRepository.getTvShows("airing_today", 1);
    }

    public LiveData<MovieResponse> getReleasingTodayMovies() {
        // Since the original code uses a discover URL for releasing today, 
        // we might need a specific method in repository if it's different from category.
        // For now, using the general category if applicable or assuming we'll add it.
        return movieRepository.getMovies("now_playing", 1); 
    }
}
