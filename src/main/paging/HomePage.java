package main.paging;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.CommandOutputInterface;
import fileio.output.MessageOnly;
import fileio.output.PageOutput;
import main.audiocollections.Audio;
import main.audiocollections.Playlist;
import main.audiocollections.Song;
import main.foradmin.UsersHistory;
import main.foruser.SearchBar;
import main.foruser.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HomePage implements Page {

    /**
     * metoda care afiseaza pagina de Home a unui utilizator, mai exact melodiile si playlisturile
     * recomandate, in functie de aprecierile utilizatorului
     *
     * @param printCurrentPageCommand comanda actuala
     * @return rezultatul comenzii
     */
    @Override
    public CommandOutputInterface printCurrentPage(final CommandInput printCurrentPageCommand) {

        PageOutput commandOutput = new PageOutput(printCurrentPageCommand);
        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(printCurrentPageCommand.getUsername(),
                        printCurrentPageCommand.getCommand());

        if (!currentUser.getIsOnline()) {
            commandOutput.setMessage(currentUser.getUsername() + " is offline.");
            return commandOutput;
        }

        // melodiile cele mai apreciate de userul curent
        List<Song> mostLikedSongsByUser = new ArrayList<>(currentUser.getLikedSongs());
        mostLikedSongsByUser.sort(Comparator.comparingInt(Song::getLikes).reversed());
        mostLikedSongsByUser.subList(0, Math.min(mostLikedSongsByUser.size(),
                SearchBar.MAXIMUM_RESULT_SIZE));

        List<Playlist> mostLikedPlaylistsFollowedByUser = new ArrayList<>(
                currentUser.getFollowedPlaylists());
        mostLikedPlaylistsFollowedByUser.sort(Comparator.comparingInt(playlist ->
                playlist.computeLikesFromAllSongs(playlist.getSongs())));
        mostLikedPlaylistsFollowedByUser.subList(0,
                Math.min(mostLikedPlaylistsFollowedByUser.size(), SearchBar.MAXIMUM_RESULT_SIZE));

        List<String> songNames;
        songNames = mostLikedSongsByUser.stream().map(Audio::getName).toList();
        List<String> playlistNames;
        playlistNames = mostLikedPlaylistsFollowedByUser.stream().map(Audio::getName).toList();
        commandOutput.setMessage("Liked songs:\n\t" + songNames
                + "\n\nFollowed playlists:\n\t" + playlistNames);

        return commandOutput;
    }

    /**
     * metoda care muta un utilizator de pe pagina de Home pe pagina LikedContent
     *
     * @param changePageCommand comanda actuala
     * @return rezultatul comenzii
     */
    @Override
    public CommandOutput changePage(final CommandInput changePageCommand) {
        MessageOnly commandOutput = new MessageOnly(changePageCommand);
        User currentUser = UsersHistory.getUsersHistory().
                getCurrentUser(changePageCommand.getUsername(), changePageCommand.getCommand());
        if (!changePageCommand.getNextPage().equals("LikedContent")) {
            commandOutput.setMessage(currentUser.getUsername()
                    + " is trying to access a non-existent page.");
            return commandOutput;
        }
        currentUser.setCurrentPage("LikedContent");
        commandOutput.setMessage(currentUser.getUsername()
                + " accessed LikedContent successfully.");
        return commandOutput;

    }

}
