package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by rahul on 1/2/18.
 */

public class Movie {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("character")
    private String character;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("overview")
    private String overview;
    @SerializedName("vote_average")
    private String voteAverage;
    @SerializedName("vote_count")
    private String voteCount;
    @SerializedName("popularity")
    private String popularity;
    @SerializedName("genre_ids")
    private List<Integer> genre;
    private boolean hasCharacter;
    private boolean showReleaseDate;

    public Movie(int id, String title, String posterPath, String releaseDate, boolean showReleaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.showReleaseDate = showReleaseDate;
    }

    public Movie(int id, String title, String posterPath, String releaseDate, String character, boolean hasCharacter) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.character = character;
        this.hasCharacter = hasCharacter;
    }

    public Movie(String posterPath, String backdropPath, String overview, String releaseDate, List<Integer> genre, int id, String title, String popularity, String voteCount, String voteAverage) {
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
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

    public String getBackdropPath() {
        return backdropPath;
    }

    public List<Integer> getGenre() {
        return genre;
    }

    public String getCharacter() {
        return character;
    }

    public boolean isHasCharacter() {
        return hasCharacter;
    }

    public boolean isShowReleaseDate() {
        return showReleaseDate;
    }
}
