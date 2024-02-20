package main.foruser;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.MessageAndResults;
import fileio.output.MessageOnly;
import main.audiocollections.Song;
import main.audiocollections.Podcast;
import main.audiocollections.Album;
import main.audiocollections.Library;
import main.audiocollections.audiosproperties.Filter;
import main.foradmin.UsersHistory;

import java.util.ArrayList;
import java.util.List;

public class SearchBar {
    public static final int MAXIMUM_RESULT_SIZE = 5;

    protected SearchBar() {
    }

    /**
     * realizarea cautarii de catre useri
     * @param searchCommand comanda actuala
     * @return rezultatele in urma cautarii
     */
    public static CommandOutput search(final CommandInput searchCommand) {
        Filter filters = searchCommand.getFilters();
        MessageAndResults commandOutput = new MessageAndResults(searchCommand);
        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(searchCommand.getUsername(), searchCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + searchCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getIsOnline()) {
            commandOutput.setMessage(currentUser.getUsername() + " is offline.");
            return commandOutput;
        }

        switch (searchCommand.getType()) {
            case "song": {
                // folosim stream().filter() pentru a filtra rezultatele
                Library.getLibrary().getAllSongs().stream()
                        .filter(song -> (filters.getName() == null
                                || song.getName().startsWith(filters.getName()))
                            && (filters.getAlbum() == null
                                || song.getAlbum().equals(filters.getAlbum()))
                            && (filters.getTags() == null
                                || song.getTags().containsAll(filters.getTags()))
                            && (filters.getLyrics() == null
                                || song.getLyrics().toLowerCase()
                                .contains(filters.getLyrics().toLowerCase()))
                            && (filters.getGenre() == null
                                || song.getGenre().equalsIgnoreCase(filters.getGenre()))
                            && (filters.getReleaseYear() == null
                                || ((filters.getReleaseYear().contains("<"))
                            && Integer.parseInt(filters.getReleaseYear().substring(1))
                                    > song.getReleaseYear())
                                || ((filters.getReleaseYear().contains(">"))
                            && Integer.parseInt(filters.getReleaseYear().substring(1))
                                    < song.getReleaseYear()))
                            && (filters.getArtist() == null
                                || song.getArtist().equals(filters.getArtist()))
                        )
                        .forEach(s -> {
                            commandOutput.getResults().add(s.getName());
                            Song song = new Song(s);
                            currentUser.getLastSearchResults().add(song);
                            currentUser.getLastUserSearchResults().clear();
                        });

                break;
            }
            case "playlist": {
                Library.getLibrary().getPlaylists()
                        .stream()
                        .filter(playlist ->
                                (filters.getOwner() == null
                                        || playlist.getOwner().equals(filters.getOwner()))
                                    && (filters.getName() == null
                                        || playlist.getName().startsWith(filters.getName()))
                                    && (playlist.getVisibility()))
                        .forEach(p -> {
                            commandOutput.getResults().add(p.getName());
                            currentUser.getLastSearchResults().add(p);
                            currentUser.getLastUserSearchResults().clear();
                        });
                break;
            }
            case "podcast": {
                Library.getLibrary().getPodcasts()
                        .stream()
                        .filter(podcast ->
                                (filters.getName() == null
                                        || podcast.getName().startsWith(filters.getName()))
                                    && (filters.getOwner() == null
                                        || podcast.getOwner().equals(filters.getOwner())))
                        .forEach(p -> {
                            commandOutput.getResults().add(p.getName());
                            Podcast podcast = new Podcast();
                            podcast.deepCopy(p);
                            currentUser.getLastSearchResults().add(podcast);
                            currentUser.getLastUserSearchResults().clear();
                        });
                break;
            }
            case "album": {
                Library.getLibrary().getAlbums()
                        .stream()
                        .filter(album ->
                                (filters.getName() == null
                                    || album.getName().startsWith(filters.getName()))
                                && (filters.getOwner() == null
                                    || album.getOwner().startsWith(filters.getOwner()))
                                && (filters.getDescription() == null
                                    || album.getDescription().startsWith(filters.getAlbum())))
                        .forEach(a -> {
                            commandOutput.getResults().add(a.getName());
                            Album album = new Album(a.getName(), a.getReleaseYear(),
                                    a.getDescription(), a.getOwner());
                            album.deepCopy(a);
                            currentUser.getLastSearchResults().add(album);
                            currentUser.getLastUserSearchResults().clear();
                        });
                break;
            }
            case "artist": {
                Library.getLibrary().getArtists()
                        .stream()
                        .filter(artist -> (filters.getName() == null
                                || artist.getUsername().startsWith(filters.getName())))
                        .forEach(a -> {
                            commandOutput.getResults().add(a.getUsername());
                            currentUser.getLastUserSearchResults()
                                    .add(UsersHistory.getUsersHistory()
                                    .getArtists().get(a.getUsername()));
                            currentUser.getLastSearchResults().clear();
                        });
                break;
            }
            case "host": {
                Library.getLibrary().getHosts()
                        .stream()
                        .filter(host -> (filters.getName() == null
                                || host.getUsername().startsWith(filters.getName())))
                        .forEach(h -> {
                            commandOutput.getResults().add(h.getUsername());
                            currentUser.getLastUserSearchResults()
                                    .add(UsersHistory.getUsersHistory().getHosts()
                                    .get(h.getUsername()));
                            currentUser.getLastSearchResults().clear();
                        });
                break;
            }
            default: break;
        }
        if (commandOutput.getResults().size() > MAXIMUM_RESULT_SIZE) {
            List<String> subList = commandOutput.getResults()
                    .subList(0, Math.min(commandOutput.getResults().size(), MAXIMUM_RESULT_SIZE));
            ArrayList<String> firstFive = new ArrayList<>(subList);
            commandOutput.getResults().clear();
            commandOutput.getResults().addAll(firstFive);
        }
        if (currentUser.getLastLoadedAudio() != null) {
            currentUser.getLastLoadedAudio().updateStatus(currentUser,
                    searchCommand.getTimestamp());
        }
        currentUser.setLastSelectedAudio(null);
        currentUser.setLastLoadedAudio(null);
        commandOutput.setMessage("Search returned " + commandOutput.getResults().size()
                + " results");
        return commandOutput;
    }

    /**
     * selectarea fisierului audio dorit dupa cautare
     * @param selectCommand comanda actuala
     * @return rezultatul comenzii de selectare
     */
    public static CommandOutput select(final CommandInput selectCommand) {
        MessageOnly commandOutput = new MessageOnly(selectCommand);

        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(selectCommand.getUsername(), selectCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + selectCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (currentUser.getLastSearchResults() == null
                && currentUser.getLastUserSearchResults() == null) {
            commandOutput.setMessage("Please conduct a search before making a selection.");
            return commandOutput;
        }

        if (currentUser.getLastSearchResults() != null
                && ((currentUser.getLastUserSearchResults() == null)
                || currentUser.getLastUserSearchResults().isEmpty())) {
            if (currentUser.getLastSearchResults().size() < selectCommand.getItemNumber()) {
                commandOutput.setMessage("The selected ID is too high.");
                return commandOutput;
            }

            commandOutput.setMessage("Successfully selected " + currentUser.getLastSearchResults()
                    .get(selectCommand.getItemNumber() - 1).getName() + ".");
            currentUser.setLastSelectedAudio(currentUser.getLastSearchResults()
                    .get(selectCommand.getItemNumber() - 1));
            currentUser.getLastSearchResults().clear();
            return commandOutput;
        }

        if (currentUser.getLastUserSearchResults() != null) {
            if (currentUser.getLastUserSearchResults().size() < selectCommand.getItemNumber()) {
                commandOutput.setMessage("The selected ID is too high.");
                return commandOutput;
            }

            commandOutput.setMessage("Successfully selected "
                    + currentUser.getLastUserSearchResults()
                        .get(selectCommand.getItemNumber() - 1).getUsername() + "'s page.");
            currentUser.setLastSelectedUser(currentUser.getLastUserSearchResults()
                    .get(selectCommand.getItemNumber() - 1));

            if (currentUser.getLastSelectedUser().getType().equals("artist")) {
                currentUser.setCurrentPage("Artist");
            } else {
                currentUser.setCurrentPage("Host");
            }

            currentUser.getLastUserSearchResults().clear();
            return commandOutput;
        }

        return commandOutput;
    }
}
