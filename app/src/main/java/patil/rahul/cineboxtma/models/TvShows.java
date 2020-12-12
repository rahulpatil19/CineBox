package patil.rahul.cineboxtma.models;

import java.util.List;

/**
 * Created by rahul on 4/4/18.
 */

public class TvShows {
    private int id;
    private String name;
    private String posterPath;
    private String backdropPath;
    private String firstAirDate;
    private String overview;
    private String voteAverage;
    private String voteCount;
    private String popularity;
    private List<Integer> genres;
    private String character;
    private boolean hasCharacter;

    public TvShows(int id, String name, String posterPath, String firstAirDate) {
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
        this.firstAirDate = firstAirDate;
    }

    public TvShows(int id, String name, String posterPath, String firstAirDate, String character, boolean hasCharacter) {
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
        this.firstAirDate = firstAirDate;
        this.character = character;
        this.hasCharacter = hasCharacter;
    }

    public TvShows(int id, String name, String backdropPath, String firstAirDate, List<Integer> genres, String overview, String voteAverage) {
        this.id = id;
        this.name = name;
        this.backdropPath = backdropPath;
        this.firstAirDate = firstAirDate;
        this.genres = genres;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public String getPopularity() {
        return popularity;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public String getCharacter() {
        return character;
    }

    public boolean isHasCharacter() {
        return hasCharacter;
    }
}
