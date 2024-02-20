package fileio.output;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageOnly extends CommandOutput {

    private String user;
    private Integer timestamp;
    private String message;

    public MessageOnly() {
    }

    public MessageOnly(final CommandInput command) {
        this.setCommand(command.getCommand());
        this.user = command.getUsername();
        this.timestamp = command.getTimestamp();
    }
}
