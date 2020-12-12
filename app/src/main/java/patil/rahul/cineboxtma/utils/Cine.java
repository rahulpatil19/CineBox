package patil.rahul.cineboxtma.utils;

/**
 * Created by rahul on 23/2/18.
 */

public class Cine {

    private Cine() {
    }

    public static final class MovieEntry {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String POSTER_PATH = "poster_path";
        public static final String RELEASE_DATE = "release_date";
        public static final String OVERVIEW = "overview";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String VOTE_COUNT = "vote_count";
        public static final String POPULARITY = "popularity";
        public static final String GENRES = "genres";
    }

    public static final class PersonEntry {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String BIRTHPLACE = "birth_place";
        public static final String BIRTH_DATE = "birth_date";
        public static final String IMAGE_PATH = "image_path";
    }
}
