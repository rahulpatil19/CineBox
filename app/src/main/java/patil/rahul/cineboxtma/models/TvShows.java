package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by rahul on 4/4/18.
 */

public class TvShows {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("first_air_date")
    private String firstAirDate;
    @SerializedName("overview")
    private String overview;
    @SerializedName("vote_average")
    private String voteAverage;
    @SerializedName("vote_count")
    private String voteCount;
    @SerializedName("popularity")
    private String popularity;
    @SerializedName("genre_ids")
    private List<Integer> genres;
    @SerializedName("character")
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
