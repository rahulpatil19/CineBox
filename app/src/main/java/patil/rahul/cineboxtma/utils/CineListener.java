package patil.rahul.cineboxtma.utils;

import patil.rahul.cineboxtma.models.Movie;
import patil.rahul.cineboxtma.models.People;
import patil.rahul.cineboxtma.models.TvShows;

public class CineListener {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public interface OnTvClickListener {
        void onTvClick(TvShows tvShows);
    }

    public interface OnPeopleClickListener {
        void onPeopleClick(People people);
    }
}
