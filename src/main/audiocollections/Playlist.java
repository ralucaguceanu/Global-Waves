package main.audiocollections;

import fileio.input.CommandInput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.audiosproperties.Stats;
import main.foruser.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static main.foruser.Player.decreaseLikes;
import static main.foruser.Player.increaseLikes;
import static main.foruser.Player.REPEAT_ALL;
import static main.foruser.Player.REPEAT_CURRENT_SONG;
import static main.foruser.Player.NO_REPEAT;

@Getter
@Setter
public final class Playlist extends Audio {

    private String owner;
    private Integer playlistId;
    private Integer timeCreated;
    private List<Song> songs = new ArrayList<>();
    private Boolean visibility = Boolean.TRUE;
    private Integer followers = 0;

    public Playlist() {

    }

    public Playlist(final String name, final String owner, final Integer playlistId,
                    final Integer timeCreated, final List<Song> songs, final Boolean visibility) {
        super(name);
        this.owner = owner;
        this.playlistId = playlistId;
        this.timeCreated = timeCreated;
        this.songs = songs;
        this.visibility = visibility;
        this.setType("this");
    }


    @Override
    public String toString() {
        return super.toString() + " - " + owner;
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
                                        // s-a terminat playlistul
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
                                - (currentTimestamp - lastTimestamp));

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
        this.getStats().setPaused(Boolean.TRUE);
        Integer remainedTime = this.getStats().getRemainedTime()
                - (commandInput.getTimestamp() - this.getTimestamp());
        this.getStats().setRemainedTime(remainedTime);
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
        int playedFromTotal = commandInput.getTimestamp() - this.getTimestamp();
        int partialSum = 0;
        for (Song song : this.getSongs()) {
            partialSum += song.getDuration();
            if (playedFromTotal <= partialSum) {
                for (Song s : currentUser.getLikedSongs()) {
                    if (s.getName().equals(song.getName())) {
                        currentUser.getLikedSongs().remove(s);
                        decreaseLikes(allSongs, song.getName());
                        commandOutput.setMessage("Unlike registered successfully.");
                        return;
                    }
                }
                currentUser.getLikedSongs().add(song);
                increaseLikes(allSongs, song.getName());
                commandOutput.setMessage("Like registered successfully.");
                return;
            }
        }
    }

    @Override
    public void follow(final User currentUser,
                       final CommandInput commandInput,
                       final MessageOnly commandOutput) {
        if (this.getOwner().equals(commandInput.getUsername())) {
            commandOutput.setMessage("You cannot follow or unfollow your own playlist.");
            return;
        }

        for (Playlist p : currentUser.getFollowedPlaylists()) {
            if (p.getName().equals(currentUser.getLastSelectedAudio().getName())) {
                currentUser.getFollowedPlaylists().remove(p);
                commandOutput.setMessage("Playlist unfollowed successfully.");
                this.setFollowers(this.getFollowers() - 1);
                return;
            }
        }
        this.setFollowers(this.getFollowers() + 1);
        currentUser.getFollowedPlaylists().add(this);
        commandOutput.setMessage("Playlist followed successfully.");
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
        Integer lastRemainedTime = this.getStats().getRemainedTime();
        Integer lastTimestamp = this.getTimestamp();
        for (int i = 0; i < this.getSongs().size(); i++) {
            if (this.getSongs().get(i).getName().equals(this.getStats().getName())) {
                int timestampDif = commandInput.getTimestamp() - lastTimestamp;
                int remainingTime = lastRemainedTime - timestampDif;

                for (int j = i; j < this.getSongs().size(); j++) {
                    if (remainingTime < 0) {
                        if (j + 1 == this.getSongs().size()) {
                            if (this.getStats().getRepeat().equals(REPEAT_ALL)) {
                                j = -1;
                            } else {
                                commandOutput.setMessage(
                                        "Please load a source before skipping to the next track.");
                                return;
                            }
                        }
                        remainingTime += this.getSongs().get(j + 1).getDuration();
                    } else {
                        // daca nu exista urmatoarea melodie in playlist
                        if (j + 1 == this.getSongs().size()) {
                            Song currentSong = this.getSongs().get(j);
                            this.getStats().setName(currentSong.getName());
                            this.getStats().setRemainedTime(currentSong.getDuration());
                        } else {
                            Song nextSong = this.getSongs().get(j + 1);
                            this.getStats().setName(nextSong.getName());
                            this.getStats().setRemainedTime(nextSong.getDuration());
                        }
                        commandOutput.setMessage("Skipped to next track successfully. "
                                + "The current track is " + this.getStats().getName() + ".");
                        this.setTimestamp(commandInput.getTimestamp());
                        return;
                    }
                }
            }
        }
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
                                    "Please load a source before returning to the previous track.");
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
                                + "The current track is "
                                + this.getStats().getName() + ".");
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
}
