package main.foradmin;

import fileio.input.CommandInput;
import fileio.input.LibraryInput;
import fileio.output.CommandOutputInterface;
import main.audiocollections.Library;
import main.audiocollections.Song;
import main.forartist.Artist;
import main.forhost.Host;
import main.foruser.Player;
import main.foruser.SearchBar;
import main.paging.PageFactory;

import java.util.LinkedHashSet;

public class CommandRunner {

    protected CommandRunner() {
    }

    /**
     * executia comenzilor primite in functie de nume
     * @param command comanda actuala
     * @param library baza de date
     * @return rezultatul comenzii de input, afisat la output
     */
    public static CommandOutputInterface execute(final CommandInput command,
                                                 final LibraryInput library) {
        return switch (command.getCommand()) {
            case "search" -> SearchBar.search(command);
            case "select" -> SearchBar.select(command);
            case "load" -> Player.load(command);
            case "playPause" -> Player.playPause(command);
            case "status" -> Player.status(command);
            case "createPlaylist" -> Player.createPlaylist(command);
            case "addRemoveInPlaylist" -> Player.addRemoveInPlaylist(command);
            case "like" -> Player.like(command,
                    (LinkedHashSet<Song>) Library.getLibrary().getAllSongs());
            case "showPreferredSongs" -> Statistics.showPreferredSongs(command);
            case "showPlaylists" -> Player.showPlaylists(command);
            case "switchVisibility" -> Player.switchVisibility(command);
            case "follow" -> Player.follow(command);
            case "getTop5Songs" -> Statistics.getTop5Songs(command);
            case "getTop5Playlists" -> Statistics.getTop5Playlists(command);
            case "repeat" -> Player.repeat(command);
            case "next" -> Player.next(command);
            case "prev" -> Player.prev(command);
            case "forward" -> Player.forward(command);
            case "backward" -> Player.backward(command);
            case "switchConnectionStatus" -> NormalUserCommand.switchConnectionStatus(command);
            case "getOnlineUsers" -> Statistics.getOnlineUsers(command);
            case "addUser" -> Admin.addUser(command);
            case "addAlbum" -> Artist.addAlbum(command);
            case "showAlbums" -> Admin.showAlbums(command);
            case "printCurrentPage" -> PageFactory.getCurrentPage(command)
                    .printCurrentPage(command);
            case "addEvent" -> Artist.addEvent(command);
            case "addMerch" -> Artist.addMerch(command);
            case "getAllUsers" -> Statistics.getAllUsers(command);
            case "addPodcast" -> Host.addPodcast(command);
            case "addAnnouncement" -> Host.addAnnouncement(command);
            case "showPodcasts" -> Admin.showPodcasts(command);
            case "removeAnnouncement" -> Host.removeAnnouncement(command);
            case "removeAlbum" -> Artist.removeAlbum(command);
            case "changePage" -> PageFactory.getCurrentPage(command)
                    .changePage(command);
            case "removePodcast" -> Host.removePodcast(command);
            case "getTop5Albums" -> Statistics.getTop5Albums(command);
            case "deleteUser" -> Admin.deleteUser(command);
            default -> null;
        };

    }
}
