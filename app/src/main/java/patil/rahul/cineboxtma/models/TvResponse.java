package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TvResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<TvShows> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return page;
    }

    public List<TvShows> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
