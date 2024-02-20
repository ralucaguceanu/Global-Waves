package fileio.output;

import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Audio;
import main.audiocollections.Podcast;

import java.util.List;

@Getter
@Setter
public class PodcastOutput {

    private String name;
    private List<String> episodes;

    public PodcastOutput(final Podcast podcast) {
        this.name = podcast.getName();
        this.episodes = podcast.getEpisodes().stream().map(Audio::getName).toList();
    }

}
