package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PeopleResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<People> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return page;
    }

    public List<People> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
