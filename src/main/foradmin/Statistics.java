package main.foradmin;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.NoUserAndResult;
import fileio.output.ResultsOnly;
import main.audiocollections.Audio;
import main.audiocollections.Song;
import main.audiocollections.Playlist;
import main.audiocollections.Library;
import main.audiocollections.Album;
import main.foruser.SearchBar;
import main.foruser.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Statistics {

    protected Statistics() {
    }

    /**
     * selectam doar melodiile apreciate de userul curent
     *
     * @param showPreferredSongsCommand comanda primita
     * @return melodiile la care userul a dat like
     */
    public static CommandOutput showPreferredSongs(final CommandInput showPreferredSongsCommand) {
        ResultsOnly commandOutput = new ResultsOnly();
        commandOutput.setCommand(showPreferredSongsCommand.getCommand());
        commandOutput.setUser(showPreferredSongsCommand.getUsername());
        commandOutput.setTimestamp(showPreferredSongsCommand.getTimestamp());

        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                showPreferredSongsCommand.getUsername(), showPreferredSongsCommand.getCommand());

        currentUser.getLikedSongs().forEach(s -> commandOutput.getResult().add(s.getName()));

        return commandOutput;
    }

    /**
     * sortarea melodiilor dupa numarul de like-uri si extragerea primelor 5
     *
     * @param getTop5SongsCommand comanda primita
     * @return cele mai apreciate 5 melodii
     */
    public static NoUserAndResult getTop5Songs(final CommandInput getTop5SongsCommand) {
        NoUserAndResult commandOutput = new NoUserAndResult(getTop5SongsCommand);

        List<Song> sortedSongsList = Library.getLibrary().getAllSongs().stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .toList();
        sortedSongsList.subList(0, SearchBar.MAXIMUM_RESULT_SIZE).forEach(s ->
                commandOutput.getResult().add(s.getName()));
        return commandOutput;
    }

    /**
     * sortarea playlisturilor dupa numarul de followersi
     *
     * @param getTop5PlaylistsCommand comanda primita
     * @return cele mai urmarite playlisturi
     */
    public static CommandOutput getTop5Playlists(final CommandInput getTop5PlaylistsCommand) {
        NoUserAndResult commandOutput = new NoUserAndResult(getTop5PlaylistsCommand);

        List<Playlist> allPlaylists = new ArrayList<>(Library.getLibrary().getPlaylists());

        List<Playlist> sortedPlaylists = allPlaylists.stream()
                .sorted(Comparator.comparingInt(Playlist::getFollowers).reversed()
                        .thenComparing(Playlist::getTimeCreated))
                .toList();

        sortedPlaylists.subList(0, Math.min(allPlaylists.size(), SearchBar.MAXIMUM_RESULT_SIZE))
                .forEach(p -> commandOutput.getResult().add(p.getName()));

        return commandOutput;
    }

    /**
     * metoda pentru afisarea tuturor userilor care sunt online
     *
     * @param getOnlineUsersCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput getOnlineUsers(final CommandInput getOnlineUsersCommand) {
        NoUserAndResult commandOutput = new NoUserAndResult(getOnlineUsersCommand);

        List<String> onlineUsers = new ArrayList<>();
        UsersHistory.getUsersHistory().getAllUsers().values().forEach(user -> {
            if (user.getType().equals("user") && user.getIsOnline()) {
                onlineUsers.add(user.getUsername());
            }
        });

        Collections.sort(onlineUsers);

        commandOutput.getResult().addAll(onlineUsers);
        return commandOutput;
    }

    /**
     * metoda pentru afisarea username-ului pentru toti userii din baza de date
     *
     * @param getAllUsersCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput getAllUsers(final CommandInput getAllUsersCommand) {
        NoUserAndResult commandOutput = new NoUserAndResult(getAllUsersCommand);

        List<String> usernames = new ArrayList<>();

        usernames.addAll(Library.getLibrary().getNormalUsers()
                .stream()
                .map(User::getUsername)
                .toList());

        usernames.addAll(Library.getLibrary().getArtists()
                .stream()
                .map(User::getUsername)
                .toList());

        usernames.addAll(Library.getLibrary().getHosts()
                .stream()
                .map(User::getUsername)
                .toList());

        commandOutput.getResult().addAll(usernames);
        return commandOutput;
    }

    /**
     * metoda pentru afisarea celor mai apreciate 5 albume din baza de date, dupa numarul total
     * de like-uri de pe toate melodiile, in caz de egalitate departajarea facandu-se dupa
     * ordinea lexicografica
     *
     * @param getTop5AlbumsCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput getTop5Albums(final CommandInput getTop5AlbumsCommand) {
        NoUserAndResult commandOutput = new NoUserAndResult(getTop5AlbumsCommand);

        List<Album> allAlbums = Library.getLibrary().getAlbums().stream()
                .peek(album ->
                        album.setTotalNumberOfLikes(
                                album.computeLikesFromAllSongs(album.getSongs())))
                .sorted(Comparator.comparing(
                                Album::getTotalNumberOfLikes, Comparator.reverseOrder())
                        .thenComparing(Album::getName))
                .toList();

        // afisarea a maxim 5 rezultate
        allAlbums = allAlbums.stream()
                .limit(SearchBar.MAXIMUM_RESULT_SIZE)
                .collect(Collectors.toList());

        // adaugarea numelor in lista de rezultate
        commandOutput.getResult().addAll(allAlbums.stream().map(Audio::getName).toList());

        return commandOutput;
    }

}
