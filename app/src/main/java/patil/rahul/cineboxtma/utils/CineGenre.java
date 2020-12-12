package patil.rahul.cineboxtma.utils;

/**
 * Created by rahul on 22/2/18.
 */

public class CineGenre {

    public static String getGenres(int genreId) {
        String genre = null;

        switch (genreId) {
            case 28:
                genre = "Action";
                break;

            case 10759:
                genre = "Action & Adventure";
                break;

            case 12:
                genre = "Adventure";
                break;
            case 16:
                genre = "Animation";
                break;
            case 35:
                genre = "Comedy";
                break;
            case 80:
                genre = "Crime";
                break;
            case 99:
                genre = "Documentary";
                break;
            case 18:
                genre = "Drama";
                break;
            case 10751:
                genre = "Family";
                break;
            case 10762:
                genre = "kids";
                break;

            case 14:
                genre = "Fantasy";
                break;
            case 36:
                genre = "History";
                break;
            case 27:
                genre = "Horror";
                break;
            case 10402:
                genre = "Music";
                break;
            case 9648:
                genre = "Mystery";
                break;
            case 10763:
                genre = "News";
                break;

            case 10764:
                genre = "Reality";
                break;

            case 10749:
                genre = "Romance";
                break;
            case 878:
                genre = "Science Fiction";
                break;
            case 10765:
                genre = "Sci-Fi & Fantasy";
                break;

            case 10766:
                genre = "Soap";
                break;
            case 10767:
                genre = "Talk";
                break;
            case 10770:
                genre = "TvShows Movie";
                break;
            case 53:
                genre = "Thriller";
                break;
            case 10752:
                genre = "War";
                break;
            case 10768:
                genre = "War & Politics";
                break;
            case 37:
                genre = "Western";
                break;
        }
        return genre;
    }
}
