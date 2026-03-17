package patil.rahul.cineboxtma.network;

import patil.rahul.cineboxtma.models.MovieDetailResponse;
import patil.rahul.cineboxtma.models.MovieResponse;
import patil.rahul.cineboxtma.models.PeopleDetailResponse;
import patil.rahul.cineboxtma.models.PeopleResponse;
import patil.rahul.cineboxtma.models.TvDetailResponse;
import patil.rahul.cineboxtma.models.TvResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {
    @GET("movie/{category}")
    Call<MovieResponse> getMovies(
            @Path("category") String category,
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/{movie_id}")
    Call<MovieDetailResponse> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String appendToResponse
    );

    @GET("tv/{category}")
    Call<TvResponse> getTvShows(
            @Path("category") String category,
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("tv/{tv_id}")
    Call<TvDetailResponse> getTvShowDetail(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String appendToResponse
    );

    @GET("tv/{tv_id}/season/{season_number}")
    Call<TvDetailResponse> getTvSeasonDetail(
            @Path("tv_id") int tvId,
            @Path("season_number") int seasonNumber,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String appendToResponse
    );

    @GET("person/popular")
    Call<PeopleResponse> getPopularPeople(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("person/{person_id}")
    Call<PeopleDetailResponse> getPersonDetail(
            @Path("person_id") int personId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String appendToResponse
    );

    @GET("search/{type}")
    Call<MovieResponse> searchMovies(
            @Path("type") String type,
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("search/{type}")
    Call<TvResponse> searchTvShows(
            @Path("type") String type,
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("search/{type}")
    Call<PeopleResponse> searchPeople(
            @Path("type") String type,
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("discover/movie")
    Call<MovieResponse> discoverMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page,
            @Query("primary_release_date.gte") String gteDate,
            @Query("primary_release_date.lte") String lteDate
    );
}
