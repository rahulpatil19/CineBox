package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PeopleDetailResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("profile_path")
    private String profilePath;
    @SerializedName("biography")
    private String biography;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("place_of_birth")
    private String placeOfBirth;
    @SerializedName("external_ids")
    private ExternalIds externalIds;
    @SerializedName("combined_credits")
    private CombinedCredits combinedCredits;
    @SerializedName("images")
    private Images images;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getProfilePath() { return profilePath; }
    public String getBiography() { return biography; }
    public String getBirthday() { return birthday; }
    public String getPlaceOfBirth() { return placeOfBirth; }
    public ExternalIds getExternalIds() { return externalIds; }
    public CombinedCredits getCombinedCredits() { return combinedCredits; }
    public Images getImages() { return images; }

    public static class ExternalIds {
        @SerializedName("imdb_id")
        private String imdbId;
        @SerializedName("instagram_id")
        private String instagramId;
        @SerializedName("twitter_id")
        private String twitterId;
        @SerializedName("facebook_id")
        private String facebookId;

        public String getImdbId() { return imdbId; }
        public String getInstagramId() { return instagramId; }
        public String getTwitterId() { return twitterId; }
        public String getFacebookId() { return facebookId; }
    }

    public static class CombinedCredits {
        @SerializedName("cast")
        private List<Credit> cast;
        @SerializedName("crew")
        private List<Credit> crew;

        public List<Credit> getCast() { return cast; }
        public List<Credit> getCrew() { return crew; }
    }

    public static class Credit {
        @SerializedName("id")
        private int id;
        @SerializedName("media_type")
        private String mediaType;
        @SerializedName("backdrop_path")
        private String backdropPath;
        @SerializedName("release_date")
        private String releaseDate;
        @SerializedName("first_air_date")
        private String firstAirDate;
        @SerializedName("title")
        private String title;
        @SerializedName("name")
        private String name;
        @SerializedName("character")
        private String character;
        @SerializedName("job")
        private String job;
        @SerializedName("poster_path")
        private String posterPath;

        public int getId() { return id; }
        public String getMediaType() { return mediaType; }
        public String getBackdropPath() { return backdropPath; }
        public String getReleaseDate() { return releaseDate; }
        public String getFirstAirDate() { return firstAirDate; }
        public String getTitle() { return title; }
        public String getName() { return name; }
        public String getCharacter() { return character; }
        public String getJob() { return job; }
        public String getPosterPath() { return posterPath; }
    }

    public static class Images {
        @SerializedName("profiles")
        private List<Profile> profiles;
        public List<Profile> getProfiles() { return profiles; }

        public static class Profile {
            @SerializedName("file_path")
            private String filePath;
            public String getFilePath() { return filePath; }
        }
    }
}
