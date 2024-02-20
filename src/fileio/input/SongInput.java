package fileio.input;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public final class SongInput {

    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private Integer likes = 0;
    public SongInput() {
    }

    @Override
    public String toString() {
        return name;
    }
}
