package fileio.output;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class NoUserAndResult extends CommandOutput {

    private Integer timestamp;
    private ArrayList<Object> result = new ArrayList<>();

    public NoUserAndResult() {
    }

    public NoUserAndResult(final CommandInput command) {
        this.setCommand(command.getCommand());
        this.timestamp = command.getTimestamp();
    }
}
