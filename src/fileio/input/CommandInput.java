package fileio.input;

import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Episode;
import main.audiocollections.audiosproperties.Filter;
import main.audiocollections.Song;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommandInput {

    private String command;
    private String username;
    private Integer timestamp;
    private String type;
    private Filter filters;
    private Integer itemNumber;
    private String playlistName;
    private Integer playlistId;
    private Integer seed;
    private Integer age;
    private String city;
    private String name;
    private Integer releaseYear;
    private String description;
    private List<Song> songs = new ArrayList<>();
    private String date;
    private Double price;
    private List<Episode> episodes = new ArrayList<>();
    private String nextPage;

}
