package main.audiocollections;

import fileio.input.CommandInput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.foruser.User;

import java.util.LinkedHashSet;

@Getter
@Setter
public final class Episode extends Audio {

    private String description;

    public Episode() {
    }

    public Episode(final String name, final Integer duration, final String description) {
        super(name, duration);
        this.description = description;
    }

    @Override
    public void deepCopy(final Audio episode) {
        super.setName(episode.getName());
        this.setDuration(episode.getDuration());
        this.description = ((Episode) episode).getDescription();
    }

    @Override
    public String toString() {
        return super.toString() + " - " + description;
    }

    @Override
    public void updateStatus(final User currentUser, final Integer currentTimestamp) {

    }

    @Override
    public boolean load(final User currentUser, final CommandInput commandInput) {
        return false;
    }

    @Override
    public void playPause(final User currentUser, final CommandInput commandInput) {

    }

    @Override
    public void addRemoveInPlaylist(final User currentUser,
                                    final CommandInput commandInput,
                                    final MessageOnly commandOutput) {
        commandOutput.setMessage("The loaded source is not a song.");
    }

    @Override
    public void like(final User currentUser,
                     final CommandInput commandInput,
                     final MessageOnly commandOutput,
                     final LinkedHashSet<Song> allSongs) {

    }

    @Override
    public void follow(final User currentUser,
                       final CommandInput commandInput,
                       final MessageOnly commandOutput) {
        commandOutput.setMessage("The selected source is not a playlist.");
    }

    @Override
    public void backward(final User currentUser,
                         final CommandInput commandInput,
                         final MessageOnly commandOutput) {
        commandOutput.setMessage("The loaded source is not a podcast.");
    }

    @Override
    public void forward(final User currentUser,
                        final CommandInput commandInput,
                        final MessageOnly commandOutput) {
        commandOutput.setMessage("The loaded source is not a podcast.");
    }

    @Override
    public void next(final User currentUser,
                     final CommandInput commandInput,
                     final MessageOnly commandOutput) {

    }

    @Override
    public void prev(final User currentUser,
                     final CommandInput commandInput,
                     final MessageOnly commandOutput) {

    }

    @Override
    public void changeRepeatStatus(final Integer timestamp) {

    }
}
