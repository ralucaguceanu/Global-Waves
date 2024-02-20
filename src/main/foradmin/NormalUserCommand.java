package main.foradmin;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.MessageOnly;
import main.audiocollections.Audio;
import main.foruser.User;

import static main.audiocollections.Podcast.updatePodcastStats;

public final class NormalUserCommand {

    private NormalUserCommand() {
    }

    /**
     * metoda pentru schimbarea statusului unui user normal, din online in offline sau invers
     *
     * @param switchConnectionStatusCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput switchConnectionStatus(
            final CommandInput switchConnectionStatusCommand) {
        MessageOnly commandOutput = new MessageOnly(switchConnectionStatusCommand);

        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(switchConnectionStatusCommand.getUsername(),
                        switchConnectionStatusCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + switchConnectionStatusCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (currentUser.getType().equals("artist") || currentUser.getType().equals("host")) {
            commandOutput.setMessage(currentUser.getUsername() + " is not a normal user.");
            return commandOutput;
        }

        if (currentUser.getIsOnline()) { // cand userul devine offline actualizam statusurile
            currentUser.setIsOnline(Boolean.FALSE);
            if (lastLoadedAudio != null) {
                if (lastLoadedAudio.getType().equals("song")
                        || lastLoadedAudio.getType().equals("playlist")) {
                    Integer remainedTime = lastLoadedAudio.getStats().getRemainedTime()
                            - (switchConnectionStatusCommand.getTimestamp()
                            - lastLoadedAudio.getTimestamp());
                    lastLoadedAudio.getStats().setRemainedTime(remainedTime);
                } else {
                    for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
                        if (ongoingPodcast.getPodcast().getName()
                                .equals(lastLoadedAudio.getName())) {
                            updatePodcastStats(switchConnectionStatusCommand, ongoingPodcast,
                                    lastLoadedAudio);
                        }
                    }

                }
            }
        } else {
            currentUser.setIsOnline(Boolean.TRUE);
            if (lastLoadedAudio != null) {
                lastLoadedAudio.setTimestamp(switchConnectionStatusCommand.getTimestamp());
            }
        }

        commandOutput.setMessage(currentUser.getUsername() + " has changed status successfully.");

        if (lastLoadedAudio != null) {
            lastLoadedAudio.setTimestamp(switchConnectionStatusCommand.getTimestamp());
        }

        return commandOutput;
    }
}
