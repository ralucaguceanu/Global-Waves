package fileio.output;

import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class MessageAndResults extends CommandOutput {

    private String user;
    private Integer timestamp;
    private String message;
    private ArrayList<String> results = new ArrayList<>();

    public MessageAndResults() {
    }

    public MessageAndResults(final CommandInput commandInput) {
        this.setCommand(commandInput.getCommand());
        this.user = commandInput.getUsername();
        this.timestamp = commandInput.getTimestamp();
    }
}
