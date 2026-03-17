package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TvDetailResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("first_air_date")
    private String firstAirDate;
    @SerializedName("name")
    private String name;
    @SerializedName("number_of_episodes")
    private int numberOfEpisodes;
    @SerializedName("number_of_seasons")
    private int numberOfSeasons;
    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("created_by")
    private List<CreatedBy> createdBy;
    @SerializedName("images")
    private Images images;
    @SerializedName("seasons")
    private List<Season> seasons;
    @SerializedName("external_ids")
    private ExternalIds externalIds;
    @SerializedName("credits")
    private Credits credits;
    @SerializedName("videos")
    private VideoResponse videos;

    public int getId() { return id; }
    public String getBackdropPath() { return backdropPath; }
    public String getFirstAirDate() { return firstAirDate; }
    public String getName() { return name; }
    public int getNumberOfEpisodes() { return numberOfEpisodes; }
    public int getNumberOfSeasons() { return numberOfSeasons; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public double getVoteAverage() { return voteAverage; }
    public int getVoteCount() { return voteCount; }
    public List<Genre> getGenres() { return genres; }
    public List<CreatedBy> getCreatedBy() { return createdBy; }
    public Images getImages() { return images; }
    public List<Season> getSeasons() { return seasons; }
    public ExternalIds getExternalIds() { return externalIds; }
    public Credits getCredits() { return credits; }
    public VideoResponse getVideos() { return videos; }

    public static class Genre {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }

    public static class CreatedBy {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
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

    public static class Season {
        @SerializedName("air_date")
        private String airDate;
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private int id;
        @SerializedName("season_number")
        private int seasonNumber;
        @SerializedName("poster_path")
        private String posterPath;

        public String getAirDate() { return airDate; }
        public String getName() { return name; }
        public int getId() { return id; }
        public int getSeasonNumber() { return seasonNumber; }
        public String getPosterPath() { return posterPath; }
    }

    public static class ExternalIds {
        @SerializedName("imdb_id")
        private String imdbId;
        public String getImdbId() { return imdbId; }
    }

    public static class Credits {
        @SerializedName("cast")
        private List<People> cast;
        @SerializedName("crew")
        private List<People> crew;
        public List<People> getCast() { return cast; }
        public List<People> getCrew() { return crew; }
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
}
