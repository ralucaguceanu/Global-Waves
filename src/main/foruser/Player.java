package main.foruser;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.MessageOnly;
import fileio.output.ResultsOnly;
import fileio.output.NoMessageAndStats;
import fileio.output.PlaylistOutput;
import main.audiocollections.Audio;
import main.audiocollections.Song;
import main.audiocollections.Playlist;
import main.audiocollections.Library;
import main.audiocollections.audiosproperties.Stats;
import main.foradmin.UsersHistory;

import java.util.LinkedHashSet;
import java.util.Objects;

public class Player {

    public static final String NO_REPEAT = "No Repeat";
    public static final String REPEAT_ONCE = "Repeat Once";
    public static final String REPEAT_INFINITE = "Repeat Infinite";
    public static final String REPEAT_ALL = "Repeat All";
    public static final String REPEAT_CURRENT_SONG = "Repeat Current Song";

    public static final Integer SKIP_TIME = 90;

    protected Player() {
    }

    /**
     * inceperea fisierului audio selectat
     *
     * @param loadCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput load(final CommandInput loadCommand) {
        MessageOnly commandOutput = new MessageOnly(loadCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(loadCommand.getUsername(),
                loadCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + loadCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        // load se poate face doar dupa select
        if (currentUser.getLastSelectedAudio() == null) {
            commandOutput.setMessage("Please select a source before attempting to load.");
            return commandOutput;
        }

        Audio lastSelectedAudio = currentUser.getLastSelectedAudio();

        if (lastSelectedAudio.load(currentUser, loadCommand)) {
            commandOutput.setMessage("Playback loaded successfully.");
        } else {
            commandOutput.setMessage("You can't load an empty audio collection!");
        }

        return commandOutput;
    }

    /**
     * userul pune pauza sau revine la fisierul audio curent
     *
     * @param playPauseCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput playPause(final CommandInput playPauseCommand) {
        MessageOnly commandOutput = new MessageOnly(playPauseCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                playPauseCommand.getUsername(), playPauseCommand.getCommand());
        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        // dam play/pauza doar daca am dat load la un fisier audio inainte
        if (lastLoadedAudio == null) {
            commandOutput.setMessage(
                    "Please load a source before attempting to pause or resume playback.");
            return commandOutput;
        }

        if (lastLoadedAudio.getStats().getPaused()) {
            lastLoadedAudio.getStats().setPaused(Boolean.FALSE);
            commandOutput.setMessage("Playback resumed successfully.");
            lastLoadedAudio.setTimestamp(playPauseCommand.getTimestamp());
        } else {  // la pauza setam statusurile
            commandOutput.setMessage("Playback paused successfully.");
            lastLoadedAudio.playPause(currentUser, playPauseCommand);
        }

        return commandOutput;
    }

    /**
     * calcularea si afisarea starii in care se afla fisierul audio curent
     *
     * @param statusCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput status(final CommandInput statusCommand) {

        NoMessageAndStats commandOutput = new NoMessageAndStats(statusCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                statusCommand.getUsername(), statusCommand.getCommand());

        if (currentUser == null) {
            return commandOutput;
        }

        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        // putem afisa statusul doar daca am dat load la un fisier audio inainte
        if (lastLoadedAudio == null) {
            Stats stats = new Stats();
            stats.setName("");
            stats.setRemainedTime(0);
            stats.setPaused(Boolean.TRUE);
            commandOutput.setStats(stats);
            return commandOutput;
        }

        Integer currentTimestamp = statusCommand.getTimestamp();

        lastLoadedAudio.updateStatus(currentUser, currentTimestamp);
        commandOutput.setStats(new Stats(lastLoadedAudio.getStats()));
        return commandOutput;
    }

    /**
     * crearea unui playlist de catre un user
     *
     * @param createPlaylistCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput createPlaylist(final CommandInput createPlaylistCommand) {
        MessageOnly commandOutput = new MessageOnly(createPlaylistCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                createPlaylistCommand.getUsername(), createPlaylistCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + createPlaylistCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (UsersHistory.getUsersHistory()
                .checkIfPlaylistExists(createPlaylistCommand.getPlaylistName())) {
            commandOutput.setMessage("A playlist with the same name already exists.");
            return commandOutput;
        } else {
            Playlist playlist = new Playlist(createPlaylistCommand.getPlaylistName(),
                    createPlaylistCommand.getUsername(), currentUser.generatePlaylistId(),
                    createPlaylistCommand.getTimestamp(), createPlaylistCommand.getSongs(),
                    Boolean.TRUE);
            currentUser.getPlaylists().add(playlist);
            commandOutput.setMessage("Playlist created successfully.");
            Library.getLibrary().getPlaylists().add(playlist);
        }
        return commandOutput;
    }


    /**
     * adaugarea sau eliminarea dintr-un playlist
     *
     * @param addRemoveInPlaylistCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput addRemoveInPlaylist(final CommandInput addRemoveInPlaylistCommand) {
        MessageOnly commandOutput = new MessageOnly(addRemoveInPlaylistCommand);
        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(addRemoveInPlaylistCommand.getUsername(),
                        addRemoveInPlaylistCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + addRemoveInPlaylistCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage(
                    "Please load a source before adding to or removing from the playlist.");
            return commandOutput;
        }

        lastLoadedAudio.addRemoveInPlaylist(currentUser, addRemoveInPlaylistCommand,
                commandOutput);

        return commandOutput;
    }

    /**
     * actualizarea numarului de like-uri ale unei melodii, fie ca aceasta se afla
     * intr-un playlist sau nu
     *
     * @param likeCommand comanda actuala
     * @param allSongs    lista globala de melodii
     * @return rezultatul afisat
     */
    public static CommandOutput like(final CommandInput likeCommand,
                                     final LinkedHashSet<Song> allSongs) {
        MessageOnly commandOutput = new MessageOnly(likeCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                likeCommand.getUsername(), likeCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + likeCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getIsOnline()) {
            commandOutput.setMessage(likeCommand.getUsername() + " is offline.");
            return commandOutput;
        }

        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage("Please load a source before liking or unliking.");
            return commandOutput;
        }

        lastLoadedAudio.like(currentUser, likeCommand, commandOutput, allSongs);

        return commandOutput;
    }

    /**
     * decrementarea numarului de like-uri pentru o melodie
     *
     * @param allSongs lista globala de melodii
     * @param name     numele melodiei
     */
    public static void decreaseLikes(final LinkedHashSet<Song> allSongs, final String name) {
        for (Song s : allSongs) {
            if (s.getName().equals(name)) {
                s.setLikes(s.getLikes() - 1);
            }
        }
    }

    /**
     * incremenatarea numarului de like-uri pentru o melodie
     *
     * @param allSongs lista globala de melodii
     * @param name     numele melodiei
     */
    public static void increaseLikes(final LinkedHashSet<Song> allSongs, final String name) {
        for (Song s : allSongs) {
            if (s.getName().equals(name)) {
                s.setLikes(s.getLikes() + 1);
            }
        }
    }

    /**
     * afisarea playlisturilor unui user
     *
     * @param showPlaylistsCommand comanda actuala
     * @return rezultatul
     */
    public static CommandOutput showPlaylists(final CommandInput showPlaylistsCommand) {
        ResultsOnly commandOutput = new ResultsOnly(showPlaylistsCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                showPlaylistsCommand.getUsername(), showPlaylistsCommand.getCommand());
        currentUser.getPlaylists().forEach(p ->
                commandOutput.getResult().add(new PlaylistOutput(p)));
        return commandOutput;
    }

    /**
     * schimbarea statusului unui playlist; din public in privat sau invers
     *
     * @param showVisibilityCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput switchVisibility(final CommandInput showVisibilityCommand) {
        MessageOnly commandOutput = new MessageOnly(showVisibilityCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                showVisibilityCommand.getUsername(), showVisibilityCommand.getCommand());

        for (Playlist p : currentUser.getPlaylists()) {
            if (Objects.equals(p.getPlaylistId(), showVisibilityCommand.getPlaylistId())) {
                p.setVisibility(p.getVisibility() ? Boolean.FALSE : Boolean.TRUE);
                if (p.getVisibility()) {
                    commandOutput.setMessage(
                            "Visibility status updated successfully to public.");
                } else {
                    commandOutput.setMessage(
                            "Visibility status updated successfully to private.");
                }
                return commandOutput;
            }
        }

        commandOutput.setMessage("The specified playlist ID is too high.");
        return commandOutput;
    }

    /**
     * actualizarea urmaritorilor unui playlist
     *
     * @param followCommand comanda actuala
     * @return rezultatul
     */
    public static CommandOutput follow(final CommandInput followCommand) {
        MessageOnly commandOutput = new MessageOnly(followCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                followCommand.getUsername(), followCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + followCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        Audio lastSelectedAudio = currentUser.getLastSelectedAudio();

        if (lastSelectedAudio == null) {
            commandOutput.setMessage("Please select a source before following or unfollowing.");
            return commandOutput;
        }

        lastSelectedAudio.follow(currentUser, followCommand, commandOutput);

        return commandOutput;
    }

    /**
     * verificarea starii de repeat a unui fisier audio
     *
     * @param repeatCommand comanda actuala
     * @return rezultaul afisat
     */
    public static CommandOutput repeat(final CommandInput repeatCommand) {
        MessageOnly commandOutput = new MessageOnly(repeatCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                repeatCommand.getUsername(), repeatCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + repeatCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage("Please load a source before setting the repeat status.");
            return commandOutput;
        } else {
            commandOutput.setMessage(updateRepeatStatus(lastLoadedAudio,
                    repeatCommand.getTimestamp()));
        }
        return commandOutput;
    }

    /**
     * actualizarea starii de repeat a unui fisier audio
     *
     * @param lastLoadedAudio comanda actuala
     * @param timestamp       timestampul actual, al comenzii
     * @return rezultatul afisat
     */
    private static String updateRepeatStatus(final Audio lastLoadedAudio, final Integer timestamp) {
        lastLoadedAudio.changeRepeatStatus(timestamp);

        return "Repeat mode changed to "
                + lastLoadedAudio.getStats().getRepeat().toLowerCase() + ".";
    }

    /**
     * mutare la fisierul audio urmator
     *
     * @param nextCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput next(final CommandInput nextCommand) {
        MessageOnly commandOutput = new MessageOnly(nextCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                nextCommand.getUsername(), nextCommand.getCommand());
        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage("Please load a source before skipping to the next track.");
            return commandOutput;
        }

        lastLoadedAudio.next(currentUser, nextCommand, commandOutput);

        return commandOutput;
    }

    /**
     * mutare la fisierul audio anterior
     *
     * @param prevCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput prev(final CommandInput prevCommand) {
        MessageOnly commandOutput = new MessageOnly(prevCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                prevCommand.getUsername(), prevCommand.getCommand());
        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage(
                    "Please load a source before returning to the previous track.");
            return commandOutput;
        }

        lastLoadedAudio.prev(currentUser, prevCommand, commandOutput);

        return commandOutput;
    }

    /**
     * avansare 90 de secunde in fisierul audio curent
     *
     * @param forwardCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput forward(final CommandInput forwardCommand) {
        MessageOnly commandOutput = new MessageOnly(forwardCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                forwardCommand.getUsername(), forwardCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + forwardCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (currentUser.getLastLoadedAudio() != null) {
            currentUser.getLastLoadedAudio()
                    .updateStatus(currentUser, forwardCommand.getTimestamp());
        }
        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage("Please load a source before attempting to forward.");
            return commandOutput;
        }

        lastLoadedAudio.forward(currentUser, forwardCommand, commandOutput);
        return commandOutput;
    }

    /**
     * intoarcere cu 90 de secunde in fisierul audio curent
     *
     * @param backwardCommand comanda actuala
     * @return rezultatul afisat
     */
    public static CommandOutput backward(final CommandInput backwardCommand) {
        MessageOnly commandOutput = new MessageOnly(backwardCommand);

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                backwardCommand.getUsername(), backwardCommand.getCommand());
        Audio lastLoadedAudio = currentUser.getLastLoadedAudio();

        if (lastLoadedAudio == null) {
            commandOutput.setMessage("Please select a source before rewinding.");
            return commandOutput;
        }

        lastLoadedAudio.backward(currentUser, backwardCommand, commandOutput);

        return commandOutput;
    }

}
