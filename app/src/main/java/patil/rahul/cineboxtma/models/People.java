package patil.rahul.cineboxtma.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by rahul on 1/2/18.
 */

public class People {

    @SerializedName("id")
    private int peopleId;
    @SerializedName("name")
    private String peopleName;
    @SerializedName("profile_path")
    private String peopleImage;
    @SerializedName(value = "character", alternate = {"job"})
    private String peopleCharacter;
    private List<String> peopleKnownFor;
    private boolean isCharacter;

    public People(int peopleId, String peopleName, String peopleImage, String peopleCharacter, boolean isCharacter) {
        this.peopleId = peopleId;
        this.peopleName = peopleName;
        this.peopleImage = peopleImage;
        this.peopleCharacter = peopleCharacter;
        this.isCharacter = isCharacter;
    }

    public People(int peopleId, String peopleName, String peopleImage) {
        this.peopleId = peopleId;
        this.peopleName = peopleName;
        this.peopleImage = peopleImage;
    }

    public int getPeopleId() {
        return peopleId;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public String getPeopleImage() {
        return peopleImage;
    }

    public List<String> getPeopleKnownFor() {
        return peopleKnownFor;
    }

    public String getPeopleCharacter() {
        return peopleCharacter;
    }

    public boolean isCharacter() {
        return isCharacter;
    }
}
