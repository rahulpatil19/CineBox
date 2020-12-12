package patil.rahul.cineboxtma.utils;


import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rahul on 6/2/18.
 */

public class CineUrl {

    private CineUrl() {
    }

    // Query parameter key
    private static final String LANGUAGE = "language";
    private static final String API_KEY = "api_key";
    private static final String PAGE = "page";
    private static final String QUERY = "query";
    private static final String SORT_BY = "sort_by";
    private static final String APPEND_TO_RESPONSE = "append_to_response";
    private static final String VALUE_API_KEY = "4491464d1c866d233cd6e2ef3d1b80b3";
    private static final String PRIMARY_RELEASE_DATE_GTE = "primary_release_date.gte";
    private static final String PRIMARY_RELEASE_DATE_LTE = "primary_release_date.lte";

    private static final String PRIMARY_RELEASE_DATE_ASC = "primary_release_date.asc";

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String TV_BASE_URL = "https://api.themoviedb.org/3/tv";
    private static final String PERSON_BASE_URL = "https://api.themoviedb.org/3/person";
    private static final String MOVIE_DISCOVER_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String SEARCH_BASE_URL = "https://api.themoviedb.org/3/search";

    public static final String CATEGORY_POPULAR = "popular";
    public static final String CATEGORY_TOP_RATED = "top_rated";
    public static final String CATEGORY_NOW_PLAYING = "now_playing";
    public static final String CATEGORY_UPCOMING = "upcoming";
    public static final String CATEGORY_AIRING_TODAY = "airing_today";
    public static final String CATEGORY_ON_TV = "on_tv";

    public static final String DISCOVER_RELEASING_TODAY_MOVIE = "https://api.themoviedb.org/3/discover/movie?api_key=4491464d1c866d233cd6e2ef3d1b80b3&language=en-US&page=1&primary_release_date.gte=2018-04-02&primary_release_date.lte=2018-04-02";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";

    private static final String TMDB_MOVIE_URL = "https://www.themoviedb.org";
    private static final String IMDB_MOVIE_URL = "https://www.imdb.com/title";
    private static final String IMDB_PERSON_URL = "https://www.imdb.com/name";
    private static final String FACEBOOK_PROFILE_URL = "https://www.facebook.com";
    private static final String INSTAGRAM_PROFILE_URL = "https://www.instagram.com";
    private static final String TWITTER_PROFILE_URL = "https://www.twitter.com";


   /* Movie Urls */

    public static String createMovieListUrl(String category, int page) {
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(String.valueOf(category))
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PAGE, String.valueOf(page))
                .build();
        return uri.toString();
    }

    public static String createReleasingTodayMovieUrl(int page) {
        Date date = new Date();
        long currentTime = date.getTime();
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = originalFormat.format(currentTime);

        Uri uri = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PAGE, String.valueOf(page))
                .appendQueryParameter(PRIMARY_RELEASE_DATE_GTE, currentDate)
                .appendQueryParameter(PRIMARY_RELEASE_DATE_LTE, currentDate)
                .build();
        return uri.toString();
    }

    public static String createUpcomingMovieUrl(int page) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String currentDate = originalFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 30);
        String nextDate = originalFormat.format(calendar.getTime());

        Uri uri = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PAGE, String.valueOf(page))
                .appendQueryParameter(PRIMARY_RELEASE_DATE_GTE, currentDate)
                .appendQueryParameter(PRIMARY_RELEASE_DATE_LTE, nextDate)
                .build();
        return uri.toString();
    }

    public static String createMovieDetailUrl(int movieId) {
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(APPEND_TO_RESPONSE, "external_ids,images,videos,credits,similar_movies")
                .build();
        return uri.toString();
    }


    /*  Tv Shows Urls */

    public static String createTvListUrl(String category, int page) {
        Uri uri = Uri.parse(TV_BASE_URL).buildUpon()
                .appendPath(category)
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PAGE, String.valueOf(page))
                .build();
        return uri.toString();
    }

    public static String createTvSeasonDetailUrl(int tvId, int seasonNumber) {
        Uri uri = Uri.parse(TV_BASE_URL).buildUpon()
                .appendPath(String.valueOf(tvId))
                .appendPath("season")
                .appendPath(String.valueOf(seasonNumber))
                .appendQueryParameter(API_KEY, VALUE_API_KEY).
                        appendQueryParameter(APPEND_TO_RESPONSE, "videos").
                        build();
        return uri.toString();
    }

    public static String createTvDetailUrl(int tvID) {
        Uri uri = Uri.parse(TV_BASE_URL).buildUpon()
                .appendPath(String.valueOf(tvID))
                .appendQueryParameter(API_KEY, VALUE_API_KEY).
                        appendQueryParameter(APPEND_TO_RESPONSE, "images,credits,external_ids").
                        build();
        return uri.toString();
    }

    /* Person urls */
    public static String createPersonListUrl(int page) {
        Uri uri = Uri.parse(PERSON_BASE_URL).buildUpon()
                .appendPath(CATEGORY_POPULAR)
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PAGE, String.valueOf(page))
                .build();
        return uri.toString();
    }

    public static String createPersonDetailUrl(int personId) {
        Uri uri = Uri.parse(PERSON_BASE_URL).buildUpon()
                .appendPath(String.valueOf(personId))
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(LANGUAGE, "en-US")
                .appendQueryParameter(APPEND_TO_RESPONSE, "combined_credits,images,external_ids").build();
        return uri.toString();
    }

    /* Search and image url */

    public static String createSearchUrl(String type, String query) {
        Uri uri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                .appendPath(type)
                .appendQueryParameter(API_KEY, VALUE_API_KEY)
                .appendQueryParameter(QUERY, query)
                .build();
        return uri.toString();
    }


    public static Uri createImageUri(String imageQuality, String imagePath) {
        return Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(imageQuality).appendEncodedPath(imagePath)
                .build();
    }


    /* Web Urls  */

    public static Uri createExternalWebUri(String idType, String externalId) {
        Uri uri = null;
        switch (idType) {
            case "IMDb":
                uri = Uri.parse(IMDB_MOVIE_URL).buildUpon()
                        .appendPath(externalId)
                        .build();
                break;
            case "facebook":
                uri = Uri.parse(FACEBOOK_PROFILE_URL).buildUpon()
                        .appendPath(externalId)
                        .build();
                break;
            case "instagram":
                uri = Uri.parse(INSTAGRAM_PROFILE_URL).buildUpon()
                        .appendPath(externalId)
                        .build();
                break;
            case "twitter":
                uri = Uri.parse(TWITTER_PROFILE_URL).buildUpon()
                        .appendPath(externalId)
                        .build();
                break;
        }
        return uri;
    }

    public static Uri createIMDbPersonUri(String externalId) {
        return Uri.parse(IMDB_PERSON_URL).buildUpon()
                .appendPath(externalId)
                .build();
    }

    public static Uri createTMDbWebUri(int id, String mediaType) {
        return Uri.parse(TMDB_MOVIE_URL).buildUpon()
                .appendPath(mediaType)
                .appendPath(String.valueOf(id))
                .build();
    }

}