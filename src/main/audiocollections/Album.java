package main.audiocollections;

import fileio.input.CommandInput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.audiosproperties.Stats;
import main.foradmin.UsersHistory;
import main.foruser.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static main.foruser.Player.REPEAT_ALL;
import static main.foruser.Player.REPEAT_CURRENT_SONG;
import static main.foruser.Player.NO_REPEAT;

@Getter
@Setter
public final class Album extends Audio implements SongCollection {

    private Integer releaseYear;
    private String description;
    private List<Song> songs = new ArrayList<>();
    private String owner;
    private Integer totalNumberOfLikes;

    public Album(final String name, final Integer releaseYear, final String description,
                 final String owner) {
        super(name);
        this.releaseYear = releaseYear;
        this.description = description;
        this.owner = owner;
        this.setType("album");
    }

    @Override
    public void deepCopy(final Audio album) {
        this.setOwner(((Album) album).getOwner());
        this.setSongs(new ArrayList<>(((Album) album).getSongs()));
    }

    @Override
    public void updateStatus(final User currentUser, final Integer currentTimestamp) {
        if (!this.getStats().getPaused()) {
            Integer lastRemainedTime = this.getStats().getRemainedTime();
            Integer lastTimestamp = this.getTimestamp();
            this.setTimestamp(currentTimestamp);

            if (this.getStats().getRepeat().equals(REPEAT_ALL)) {
                for (int i = 0; i < this.getSongs().size(); i++) {
                    if (this.getSongs().get(i).getName()
                            .equals(this.getStats().getName())) {
                        int timestampDif = currentTimestamp - lastTimestamp;
                        int remainingTime = lastRemainedTime - timestampDif;

                        for (int j = i; j < this.getSongs().size(); j++) {
                            if (remainingTime <= 0) {
                                if (j + 1 == this.getSongs().size()) {
                                    if (this.getStats().getRepeat().equals(REPEAT_ALL)) {
                                        j = -1;
                                    } else {
                                        // s-a terminat albumul
                                        Stats stats = new Stats();
                                        stats.setName("");
                                        stats.setRemainedTime(0);
                                        stats.setPaused(Boolean.TRUE);
                                        this.setStats(stats);
                                        currentUser.setLastLoadedAudio(null);
                                        return;
                                    }
                                }
                                Song nextSong = this.getSongs().get(j + 1);
                                remainingTime += nextSong.getDuration();
                            } else {
                                this.getStats().setRemainedTime(remainingTime);
                                Song currentSong = this.getSongs().get(j);
                                this.getStats().setName(currentSong.getName());
                                return;
                            }
                        }
                    }
                }
            } else if (this.getStats().getRepeat().equals(REPEAT_CURRENT_SONG)) {
                for (Song song : (this).getSongs()) {
                    if (song.getName().equals(this.getStats().getName())) {
                        Integer remainedTime = (this.getStats().getRemainedTime()
                                - (currentTimestamp - this.getTimestamp()));

                        while (remainedTime <= 0) {
                            remainedTime += song.getDuration();
                        }
                        this.getStats().setRemainedTime(remainedTime);
                        return;
                    }
                }
            } else {
                // repeat state: No Repeat
                for (int i = 0; i < this.getSongs().size(); i++) {
                    if (this.getSongs().get(i).getName()
                            .equals(this.getStats().getName())) {
                        int timestampDif = currentTimestamp - lastTimestamp;
                        int remainingTime = lastRemainedTime - timestampDif;

                        for (int j = i; j < this.getSongs().size(); j++) {
                            if (remainingTime < 0) {
                                if (j + 1 == this.getSongs().size()) {
                                    if (this.getStats().getRepeat().equals(REPEAT_ALL)) {
                                        j = 0;
                                    } else {
                                        Stats stats = new Stats();
                                        stats.setName("");
                                        stats.setRemainedTime(0);
                                        stats.setPaused(Boolean.TRUE);
                                        this.setStats(stats);
                                        currentUser.setLastLoadedAudio(null);
                                        return;
                                    }
                                }
                                remainingTime += this.getSongs().get(j + 1).getDuration();
                            } else {
                                this.getStats().setRemainedTime(remainingTime);
                                Song currentSong = this.getSongs().get(j);
                                this.getStats().setName(currentSong.getName());
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean load(final User currentUser, final CommandInput commandInput) {
        if (this.getSongs().isEmpty()) {
            return false;
        } else {
            Song firstSongFromPlaylist = this.getSongs().get(0);
            firstSongFromPlaylist.setTimestamp(commandInput.getTimestamp());
            firstSongFromPlaylist.getStats().setName(this.getName());
            firstSongFromPlaylist.getStats().setRemainedTime(this.getDuration());
            currentUser.setLastLoadedAudio(this);
            currentUser.getLastLoadedAudio().setTimestamp(commandInput.getTimestamp());
            currentUser.getLastLoadedAudio()
                    .getStats().setName(firstSongFromPlaylist.getName());
            currentUser.getLastLoadedAudio()
                    .getStats().setRemainedTime(firstSongFromPlaylist.getDuration());
            // am facut load, deci nu mai avem nimic selectat
            currentUser.setLastSelectedAudio(null);
            return true;
        }
    }

    @Override
    public void playPause(final User currentUser, final CommandInput commandInput) {

    }

    @Override
    public void addRemoveInPlaylist(final User currentUser,
                                    final CommandInput commandInput,
                                    final MessageOnly commandOutput) {
        Playlist existingPlaylist = currentUser.getPlaylistById(
                commandInput.getPlaylistId());

        if (existingPlaylist != null) {
            for (Song s : existingPlaylist.getSongs()) {
                if (s.getName().equals(this.getName())) {
                    existingPlaylist.getSongs().remove(s);
                    commandOutput.setMessage("Successfully removed from playlist.");
                    return;
                }
            }
        }

        this.updateStatus(currentUser, commandInput.getTimestamp());
        Song currentSong = Library.getLibrary().getAllSongs().stream().filter(song ->
                song.getName().equals(this.getStats().getName())).findFirst().get();

        existingPlaylist.getSongs().add(currentSong);
        commandOutput.setMessage("Successfully added to playlist.");
        currentUser.setLastSearchResults(null);
        currentUser.setLastSelectedAudio(null);
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
        Integer lastRemainedTime = this.getStats().getRemainedTime();
        Integer lastTimestamp = this.getTimestamp();
        for (int i = 0; i < this.getSongs().size(); i++) {
            if (this.getSongs().get(i).getName().equals(this.getStats().getName())) {
                int timestampDif = commandInput.getTimestamp() - lastTimestamp;
                int remainingTime = lastRemainedTime - timestampDif;

                for (int j = i; j < this.getSongs().size(); j++) {
                    if (remainingTime < 0) {
                        if (j + 1 == this.getSongs().size()) {
                            commandOutput.setMessage(
                                    "Please load a source before "
                                            + "returning to the previous track.");
                            return;
                        }
                        remainingTime += this.getSongs().get(j + 1).getDuration();
                    } else {
                        boolean twoPrevsInARow = commandInput.getTimestamp()
                                .equals(this.getTimestamp());
                        Song currentSong = this.getSongs().get(j);
                        if (twoPrevsInARow) {
                            if (j == 0) {
                                this.getStats().setName(currentSong.getName());
                                this.getStats().setRemainedTime(currentSong.getDuration());
                            } else {
                                Song previousSong = this.getSongs().get(j - 1);
                                this.getStats().setName(previousSong.getName());
                                this.getStats().setRemainedTime(previousSong.getDuration());
                            }
                        } else {
                            this.getStats().setRemainedTime(currentSong.getDuration());
                        }
                        commandOutput.setMessage("Returned to previous track successfully. "
                                + "The current track is " + this.getStats().getName() + ".");
                        this.setTimestamp(commandInput.getTimestamp());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void changeRepeatStatus(final Integer timestamp) {
        if (this.getStats().getRepeat().equalsIgnoreCase(NO_REPEAT)) {
            this.getStats().setRepeat(REPEAT_ALL);
        } else if (this.getStats().getRepeat().equalsIgnoreCase(REPEAT_ALL)) {
            this.getStats().setRepeat(REPEAT_CURRENT_SONG);
        } else {
            this.getStats().setRepeat(NO_REPEAT);
        }
    }

    /**
     * metoda care verifica daca un artist are deja un album cu numele dat
     *
     * @param artistUsername artistul curent
     * @param albumName      numele dat
     * @return rezultatul verificarii, adevarat daca gasim albumul, fals in caz contrar
     */
    public static Boolean containsAlbum(final String artistUsername, final String albumName) {
        return UsersHistory.getUsersHistory().getArtists().get(artistUsername).getAlbums()
                .stream()
                .anyMatch(album -> album.getName().equals(albumName));
    }

    /**
     * metoda care verifica daca avem cel putin 2 melodii cu acelasi nume
     *
     * @param addAlbumCommand comanda actuala
     * @return rezultatul verificarii, adevarat daca gasim o melodie cu aceasta proprietate, fals
     * in caz contrar
     */
    @Override
    public Boolean hasDuplicates(final CommandInput addAlbumCommand) {
        Set<String> uniqueSongNames = addAlbumCommand.getSongs().stream()
                .map(Song::getName)
                .collect(Collectors.toSet());

        if (addAlbumCommand.getSongs().size() != uniqueSongNames.size()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * metoda care verifica egalitatea dintre doua fisiere album dupa nume si dupa owner
     *
     * @param o albumul cu care comparam
     * @return rezultatul verificarii, adevarat in caz de egalitate, fals in caz contrar
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        Album album = (Album) o;
        return Objects.equals(owner, album.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }
}
