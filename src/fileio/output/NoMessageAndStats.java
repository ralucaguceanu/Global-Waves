package fileio.output;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.audiosproperties.Stats;

@Getter
@Setter
public class NoMessageAndStats extends CommandOutput {

    private String user;
    private Integer timestamp;
    private Stats stats;

    public NoMessageAndStats() {
    }

    public NoMessageAndStats(final CommandInput command) {
        this.setCommand(command.getCommand());
        this.user = command.getUsername();
        this.timestamp = command.getTimestamp();
    }
}
