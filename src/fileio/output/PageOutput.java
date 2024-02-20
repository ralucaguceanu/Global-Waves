package fileio.output;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageOutput implements CommandOutputInterface {

    private String user;
    private String command;
    private Integer timestamp;
    private String message;

    public PageOutput(final CommandInput command) {
        this.user = command.getUsername();
        this.command = command.getCommand();
        this.timestamp = command.getTimestamp();
    }
}
