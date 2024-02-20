package main.audiocollections;

import fileio.input.CommandInput;
import fileio.input.PodcastInput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.foradmin.OngoingPodcast;
import main.foradmin.UsersHistory;
import main.foruser.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static main.foruser.Player.SKIP_TIME;
import static main.foruser.Player.NO_REPEAT;
import static main.foruser.Player.REPEAT_ONCE;
import static main.foruser.Player.REPEAT_INFINITE;

@Getter
@Setter
public final class Podcast extends Audio implements SongCollection {

    private String owner;
    private ArrayList<Episode> episodes = new ArrayList<>();
    private ArrayList<Integer> partialSumsArray = new ArrayList<>();

    public Podcast() {
        this.setType("podcast");
    }

    public Podcast(final PodcastInput podcastInput) {
        super(podcastInput.getName());
        this.owner = podcastInput.getOwner();
        podcastInput.getEpisodes().forEach(episodeInput -> {
            Episode episode = new Episode(episodeInput.getName(), episodeInput.getDuration(),
                    episodeInput.getDescription());
            this.getEpisodes().add(episode);
        });
        this.setType("podcast");
    }

    public Podcast(final String name, final String owner, final ArrayList<Episode> episodes) {
        super(name);
        this.owner = owner;
        this.episodes = episodes;
    }

    @Override
    public void deepCopy(final Audio podcast) {
        this.setName(podcast.getName());
        this.setOwner(((Podcast) podcast).getOwner());
        this.setEpisodes(new ArrayList<>(
                ((Podcast) podcast).getEpisodes().stream().map(episodeInput -> {
                    Episode episode = new Episode();
                    episode.deepCopy(episodeInput);
                    return episode;
                }).collect(Collectors.toList())));
        createPartialSumsArray();
    }

    @Override
    public Integer getDuration() {
        Integer totalDuration = 0;
        for (Episode episode : this.getEpisodes()) {
            totalDuration += episode.getDuration();
        }
        return totalDuration;
    }

    /**
     * avem nevoie de un vector care sa adune progresiv durata fiecarui episod din podcast
     * pentru a gasi episodul curent la un anumit timestamp, in functie de timestampul de load
     *
     * @return vectorul de sume partiale
     */
    public ArrayList<Integer> createPartialSumsArray() {
        Integer partialSum = 0;
        for (Episode episode : episodes) {
            partialSum += episode.getDuration();
            partialSumsArray.add(partialSum);
        }
        return partialSumsArray;
    }

    /**
     * gasim episodul curent cu ajutorul vectorului de sume partiale
     *
     * @param playedFromTotal cat timp a trecut din tot podcastul
     * @return episodul curent
     */
    public Episode findCurrentEpisode(final Integer playedFromTotal) {
        for (int index = 0; index < partialSumsArray.size(); index++) {
            if (playedFromTotal <= partialSumsArray.get(index)) {
                return episodes.get(index);
            }
        }
        return null;
    }

    /**
     * gasim indexul episodului curent cu ajutorul vectorului de sume partiale
     *
     * @param playedFromTotal cat timp a trecut din tot podcastul
     * @return indexul episodului curent
     */
    public int findCurrentEpisodeIndex(final Integer playedFromTotal) {
        for (int index = 0; index < partialSumsArray.size(); index++) {
            if (playedFromTotal <= partialSumsArray.get(index)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * calculam timpul ramas pentru episodul curent
     *
     * @param playedFromTotal cat timp a trecut din tot podcastul
     * @return timpul ramas
     */
    public Integer computeRemainedTimeForEpisode(final Integer playedFromTotal) {
        for (int index = 0; index < partialSumsArray.size(); index++) {
            if (playedFromTotal <= partialSumsArray.get(index)) {
                return partialSumsArray.get(index) - playedFromTotal;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return super.toString() + ":\n\t" + episodes + "\n";
    }

    @Override
    public void updateStatus(final User currentUser, final Integer currentTimestamp) {
        if (!this.getStats().getPaused()) {
            for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
                if (ongoingPodcast.getPodcast().getName().equals(this.getName())) {
                    Integer playedFromTotal = ongoingPodcast.getPlayedFromTotal()
                            + (currentTimestamp
                            - currentUser.getLastLoadedAudio().getTimestamp());
                    if (findCurrentEpisode(playedFromTotal) == null) {
                        this.getStats().setName("");
                    } else {
                        this.getStats().setName(ongoingPodcast.getPodcast()
                                .findCurrentEpisode(playedFromTotal).getName());
                    }

                    this.getStats().setRemainedTime(ongoingPodcast.getPodcast()
                            .computeRemainedTimeForEpisode(playedFromTotal));
                    ongoingPodcast.setPlayedFromTotal(playedFromTotal);
                    this.setTimestamp(currentTimestamp);
                }
            }
        }
    }

    @Override
    public boolean load(final User currentUser, final CommandInput commandInput) {
        this.setTimestamp(commandInput.getTimestamp());
        this.getStats().setName(this.getName());
        this.getStats().setRemainedTime(this.getDuration());
        currentUser.setLastLoadedAudio(this);
        OngoingPodcast currentPodcast = new OngoingPodcast(this, 0);
        // cautam episodul curent in functie de timpul total care s-a scurs din podcast
        for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
            if (ongoingPodcast.getPodcast().getName().equals(this.getName())) {
                updatePodcastStats(commandInput, ongoingPodcast, this);
                currentUser.setLastLoadedAudio(this);
                currentUser.setLastSelectedAudio(null);
                return true;
            }
        }
        currentUser.getOngoingPodcasts().add(currentPodcast);
        currentUser.setLastSelectedAudio(null);
        return true;
    }

    @Override
    public void playPause(final User currentUser, final CommandInput commandInput) {
        this.getStats().setPaused(Boolean.TRUE);
        for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
            if (ongoingPodcast.getPodcast().getName().equals(this.getName())) {
                updatePodcastStats(commandInput, ongoingPodcast, this);
            }
        }
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
        commandOutput.setMessage("Loaded source is not a song.");
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
        for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
            if (ongoingPodcast.getPodcast().getName().equals(this.getName())) {
                Integer playedFromLastLoaded = ongoingPodcast.getPlayedFromTotal()
                        + (commandInput.getTimestamp()
                        - currentUser.getLastLoadedAudio().getTimestamp()) - SKIP_TIME;
                Episode episode = ongoingPodcast.getPodcast()
                        .findCurrentEpisode(ongoingPodcast.getPlayedFromTotal());
                this.getStats().setName(episode.getName());
                this.getStats().setRemainedTime(
                        this.computeRemainedTimeForEpisode(playedFromLastLoaded));
                ongoingPodcast.setPlayedFromTotal(playedFromLastLoaded);
                this.setTimestamp(commandInput.getTimestamp());
            }
        }

        commandOutput.setMessage("Rewound successfully.");
    }

    @Override
    public void forward(final User currentUser,
                        final CommandInput commandInput,
                        final MessageOnly commandOutput) {
        for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
            if (ongoingPodcast.getPodcast().getName().equals(this.getName())) {
                Integer playedFromLastLoaded = ongoingPodcast.getPlayedFromTotal()
                        + (commandInput.getTimestamp()
                        - currentUser.getLastLoadedAudio().getTimestamp()) + SKIP_TIME;
                Episode episode = ongoingPodcast.getPodcast()
                        .findCurrentEpisode(ongoingPodcast.getPlayedFromTotal());
                this.getStats().setName(episode.getName());
                this.getStats().setRemainedTime(
                        this.computeRemainedTimeForEpisode(playedFromLastLoaded));
                ongoingPodcast.setPlayedFromTotal(playedFromLastLoaded);
                this.setTimestamp(commandInput.getTimestamp());
            }
        }

        commandOutput.setMessage("Skipped forward successfully.");
    }

    @Override
    public void next(final User currentUser,
                     final CommandInput commandInput,
                     final MessageOnly commandOutput) {
        for (OngoingPodcast ongoingPodcast : currentUser.getOngoingPodcasts()) {
            if (ongoingPodcast.getPodcast().getName().equals(this.getName())) {
                int currentEpisodeIndex = ongoingPodcast.getPodcast()
                        .findCurrentEpisodeIndex(ongoingPodcast.getPlayedFromTotal());
                Episode nextEpisode = ongoingPodcast.getPodcast()
                        .getEpisodes().get(currentEpisodeIndex + 1);
                this.getStats().setName(nextEpisode.getName());
                this.getStats().setRemainedTime(nextEpisode.getDuration());
                ongoingPodcast.setPlayedFromTotal(ongoingPodcast.getPodcast()
                        .getPartialSumsArray().get(currentEpisodeIndex));
                commandOutput.setMessage("Skipped to next track successfully. "
                        + "The current track is " + this.getStats().getName() + ".");
                this.setTimestamp(commandInput.getTimestamp());
            }
        }
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

    /**
     * metoda care verifica daca un host are deja un podcast cu numele dat
     *
     * @param hostUsername hostul curent
     * @param podcastName  numele dat
     * @return rezultatul verificarii, adevarat daca gasim podcastul, fals in caz contrar
     */
    public static Boolean containsPodcast(final String hostUsername, final String podcastName) {
        return UsersHistory.getUsersHistory().getHosts().get(hostUsername).getPodcasts()
                .stream()
                .anyMatch(album -> album.getName().equals(podcastName));
    }

    /**
     * metoda care verifica daca avem cel putin 2 episoade cu acelasi nume
     *
     * @param addPodcastCommand comanda actuala
     * @return rezultatul verificarii, adevarat daca gasim un episod cu aceasta proprietate, fals
     * in caz contrar
     */
    @Override
    public Boolean hasDuplicates(final CommandInput addPodcastCommand) {
        Set<String> uniqueEpisodeNames = addPodcastCommand.getEpisodes().stream()
                .map(Episode::getName)
                .collect(Collectors.toSet());

        if (addPodcastCommand.getEpisodes().size() != uniqueEpisodeNames.size()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * metoda folosita pentru actualizarea statusurilor pe podcast
     *
     * @param switchConnectionStatusCommand comanda actuala
     * @param ongoingPodcast                podcastul care a mai fost loaded
     * @param lastLoadedAudio               ultimul fisier incarcat
     */
    public static void updatePodcastStats(final CommandInput switchConnectionStatusCommand,
                                          final OngoingPodcast ongoingPodcast,
                                          final Audio lastLoadedAudio) {
        Integer playedFromLastLoaded = ongoingPodcast.getPlayedFromTotal()
                + (switchConnectionStatusCommand.getTimestamp()
                - lastLoadedAudio.getTimestamp());
        lastLoadedAudio.getStats().setName(ongoingPodcast.getPodcast()
                .findCurrentEpisode(playedFromLastLoaded).getName());
        lastLoadedAudio.getStats().setRemainedTime(ongoingPodcast.getPodcast()
                .computeRemainedTimeForEpisode(playedFromLastLoaded));
    }
}
