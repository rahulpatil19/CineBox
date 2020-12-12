package patil.rahul.cineboxtma.models;

import java.util.List;

/**
 * Created by rahul on 1/2/18.
 */

public class People {

    private int peopleId;
    private String peopleName;
    private String peopleImage;
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

