package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieDetailResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("title")
    private String title;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("runtime")
    private String runtime;
    @SerializedName("tagline")
    private String tagline;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("external_ids")
    private ExternalIds externalIds;
    @SerializedName("images")
    private Images images;
    @SerializedName("videos")
    private VideoResponse videos;
    @SerializedName("credits")
    private Credits credits;
    @SerializedName("similar_movies")
    private MovieResponse similarMovies;

    public int getId() { return id; }
    public String getBackdropPath() { return backdropPath; }
    public String getOverview() { return overview; }
    public String getReleaseDate() { return releaseDate; }
    public String getTitle() { return title; }
    public int getVoteCount() { return voteCount; }
    public double getVoteAverage() { return voteAverage; }
    public String getRuntime() { return runtime; }
    public String getTagline() { return tagline; }
    public List<Genre> getGenres() { return genres; }
    public ExternalIds getExternalIds() { return externalIds; }
    public Images getImages() { return images; }
    public VideoResponse getVideos() { return videos; }
    public Credits getCredits() { return credits; }
    public MovieResponse getSimilarMovies() { return similarMovies; }

    public static class Genre {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }

    public static class ExternalIds {
        @SerializedName("imdb_id")
        private String imdbId;
        public String getImdbId() { return imdbId; }
    }

    public static class Images {
        @SerializedName("backdrops")
        private List<Backdrop> backdrops;
        public List<Backdrop> getBackdrops() { return backdrops; }

        public static class Backdrop {
            @SerializedName("file_path")
            private String filePath;
            public String getFilePath() { return filePath; }
        }
    }

    public static class VideoResponse {
        @SerializedName("results")
        private List<Video> results;
        public List<Video> getResults() { return results; }

        public static class Video {
            @SerializedName("key")
            private String key;
            @SerializedName("name")
            private String name;
            public String getKey() { return key; }
            public String getName() { return name; }
        }
    }

    public static class Credits {
        @SerializedName("cast")
        private List<People> cast;
        @SerializedName("crew")
        private List<People> crew;
        public List<People> getCast() { return cast; }
        public List<People> getCrew() { return crew; }
    }
}
