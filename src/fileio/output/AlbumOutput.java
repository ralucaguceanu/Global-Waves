package fileio.output;

import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Album;
import main.audiocollections.Audio;

import java.util.List;

@Getter
@Setter
public class AlbumOutput {

    private String name;
    private List<String> songs;

    public AlbumOutput(final Album album) {
        this.name = album.getName();
        this.songs = album.getSongs().stream().map(Audio::getName).toList();
    }
}
