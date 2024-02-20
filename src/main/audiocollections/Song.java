package main.audiocollections;

import fileio.input.CommandInput;
import fileio.input.SongInput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.foruser.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;

import static main.foruser.Player.REPEAT_ONCE;
import static main.foruser.Player.REPEAT_INFINITE;
import static main.foruser.Player.NO_REPEAT;
import static main.foruser.Player.decreaseLikes;
import static main.foruser.Player.increaseLikes;


@Getter
@Setter
public final class Song extends Audio {

    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private Integer likes = 0;

    public Song() {
    }

    public Song(final Song song) {
        super(song.getName(), song.getDuration());
        this.setStats(song.getStats());
        this.album = song.getAlbum();
        this.tags = song.getTags();
        this.lyrics = song.getLyrics();
        this.genre = song.getGenre();
        this.releaseYear = song.getReleaseYear();
        this.artist = song.getArtist();
        this.likes = song.getLikes();
        this.setType(song.getType());
    }

    public Song(final SongInput songInput) {
        super(songInput.getName(), songInput.getDuration());
        this.album = songInput.getAlbum();
        this.tags = songInput.getTags();
        this.lyrics = songInput.getLyrics();
        this.genre = songInput.getGenre();
        this.releaseYear = songInput.getReleaseYear();
        this.artist = songInput.getArtist();
        this.setType("song");
    }

    @Override
    public String toString() {
        return super.toString() + " - " + artist;
    }

    @Override
    public void updateStatus(final User currentUser, final Integer currentTimestamp) {
        if (!currentUser.getIsOnline()) {
            return;
        }
        Integer lastTimestamp = this.getTimestamp();
        this.setTimestamp(currentTimestamp);

        // calculam timpul ramas pentru repeat
        if (!this.getStats().getPaused() || !currentUser.getIsOnline()) {
            String repeatState = this.getStats().getRepeat();

            Integer remainedTime;

            if (this.getStats().getRemainedTime() == null) {
                remainedTime = 0;
            } else {
                remainedTime = this.getStats().getRemainedTime()
                        - (currentTimestamp - lastTimestamp);
            }

            if (repeatState.equals(REPEAT_ONCE) && remainedTime <= 0) {
                remainedTime += this.getDuration();
                this.getStats().setRepeat(NO_REPEAT);
            } else if (repeatState.equals(REPEAT_INFINITE)) {
                while (remainedTime <= 0) {
                    remainedTime += this.getDuration();
                }
            }
            if (remainedTime <= 0) {
                remainedTime = 0;
                this.getStats().setPaused(Boolean.TRUE);
                this.getStats().setName("");
                currentUser.setLastLoadedAudio(null);
            }
            this.getStats().setRemainedTime(remainedTime);
        }
    }

    @Override
    public boolean load(final User currentUser, final CommandInput commandInput) {
        this.setTimestamp(commandInput.getTimestamp());
        this.getStats().setName(this.getName());
        this.getStats().setRemainedTime(this.getDuration());
        currentUser.setLastLoadedAudio(this);
        currentUser.setLastSelectedAudio(null);
        return true;
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

            existingPlaylist.getSongs().add(this);
            commandOutput.setMessage("Successfully added to playlist.");
            currentUser.setLastSearchResults(null);
            currentUser.setLastSelectedAudio(null);
        } else {
            commandOutput.setMessage("The specified playlist does not exist.");
        }
    }

    @Override
    public void like(final User currentUser,
                     final CommandInput commandInput,
                     final MessageOnly commandOutput,
                     final LinkedHashSet<Song> allSongs) {
        for (Song s : currentUser.getLikedSongs()) {
            if (s.getName().equals(this.getName())) {
                currentUser.getLikedSongs().remove(s);
                decreaseLikes(allSongs, this.getName());
                commandOutput.setMessage("Unlike registered successfully.");
                return;
            }
        }
        currentUser.getLikedSongs().add(this);
        increaseLikes(allSongs, this.getName());
        commandOutput.setMessage("Like registered successfully.");
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
        if (this.getStats().getRepeat().equalsIgnoreCase(NO_REPEAT)) {
            this.getStats().setRepeat(REPEAT_ONCE);
        } else if (this.getStats().getRepeat().equalsIgnoreCase(REPEAT_ONCE)) {
            this.getStats().setRepeat(REPEAT_INFINITE);
        } else {
            Integer remainingTime = timestamp - this.getStats().getRemainedTime();
            while (remainingTime - this.getDuration()
                    > this.getTimestamp()) {
                remainingTime -= this.getDuration();
            }
            remainingTime = this.getDuration()
                    - (remainingTime - this.getTimestamp());
            this.getStats().setRemainedTime(remainingTime);
            this.getStats().setRepeat(NO_REPEAT);
            this.setTimestamp(timestamp);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        Song song = (Song) o;
        return Objects.equals(lyrics, song.lyrics);
    }

}
