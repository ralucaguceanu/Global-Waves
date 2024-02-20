package fileio.output;

import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Audio;
import main.audiocollections.Playlist;

import java.util.List;

@Getter
@Setter
public class PlaylistOutput {

    private String name;
    private List<String> songs;
    private String visibility;
    private Integer followers;

    public PlaylistOutput(final Playlist playlist) {
        this.name = playlist.getName();
        this.songs = playlist.getSongs().stream().map(Audio::getName).toList();
        this.visibility = playlist.getVisibility() ? "public" : "private";
        this.followers = playlist.getFollowers();
    }
}
