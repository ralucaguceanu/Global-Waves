package fileio.output;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ResultsOnly extends CommandOutput {

    private String user;
    private Integer timestamp;
    private ArrayList<Object> result = new ArrayList<>();

    public ResultsOnly() {
    }

    public ResultsOnly(final CommandInput commandInput) {
        this.setCommand(commandInput.getCommand());
        this.user = commandInput.getUsername();
        this.timestamp = commandInput.getTimestamp();
    }
}
