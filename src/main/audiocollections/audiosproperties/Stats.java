package main.audiocollections.audiosproperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stats {

    private String name;
    private Integer remainedTime;
    private String repeat = "No Repeat";
    private Boolean shuffle = Boolean.FALSE;
    private Boolean paused = Boolean.FALSE;

    public Stats() {
    }

    public Stats(final Stats stats) {
        this.name = stats.getName();
        this.remainedTime = stats.getRemainedTime();
        this.repeat = stats.getRepeat();
        this.shuffle = stats.getShuffle();
        this.paused = stats.getPaused();
    }

}
