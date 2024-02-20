package main.foradmin;

import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Podcast;

@Getter
@Setter
public class OngoingPodcast {

    private Podcast podcast;
    private Integer playedFromTotal;

    public OngoingPodcast() {
    }

    public OngoingPodcast(final Podcast podcast, final Integer playedFromTotal) {
        this.podcast = podcast;
        this.playedFromTotal = playedFromTotal;
    }
}
