package main.forartist;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.CommandOutputInterface;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Album;
import main.audiocollections.Audio;
import main.audiocollections.Library;
import main.audiocollections.Song;
import main.foradmin.UsersHistory;
import main.foruser.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static main.audiocollections.Album.containsAlbum;

@Getter
@Setter
public final class Artist extends User {

    private List<Album> albums = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Merch> merch = new ArrayList<>();

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final int MAXIMUM_DAY_OF_THE_MONTH = 31;
    private static final int MAXIMUM_MONTH_OF_THE_YEAR = 12;
    private static final int MAXIMUM_YEAR_ALLOWED = 2023;
    private static final int MINIMUM_YEAR_ALLOWED = 1900;
    private static final int FEBRUARY_MONTH = 2;
    private static final int MAXIMUM_DAY_OF_FEBRUARY = 28;

    public Artist(final String username, final Integer age, final String city, final String type) {
        super(username, age, city, type);
    }

    /**
     * metoda pentru adaugarea unui album de catre un artist; se verifica daca artistul are deja un
     * album cu acelasi nume prin apelarea metodei "containsAlbum", apoi verificam daca albumul pe
     * care artistul doreste sa il adauge contine o melodie de cel putin 2 ori prin apelarea metodei
     * "hasDuplicates"
     *
     * @param addAlbumCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput addAlbum(final CommandInput addAlbumCommand) {
        MessageOnly commandOutput = new MessageOnly(addAlbumCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                addAlbumCommand.getUsername(), addAlbumCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + addAlbumCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("artist")) {
            commandOutput.setMessage(addAlbumCommand.getUsername()
                    + " is not an artist.");
            return commandOutput;
        }

        if (containsAlbum(addAlbumCommand.getUsername(), addAlbumCommand.getName())) {
            commandOutput.setMessage(addAlbumCommand.getUsername()
                    + " has another album with the same name.");
            return commandOutput;
        }


        Album album = new Album(addAlbumCommand.getName(), addAlbumCommand.getReleaseYear(),
                addAlbumCommand.getDescription(), addAlbumCommand.getUsername());

        if (album.hasDuplicates(addAlbumCommand)) {
            commandOutput.setMessage(addAlbumCommand.getUsername()
                    + " has the same song at least twice in this album.");
            return commandOutput;
        }

        commandOutput.setMessage(addAlbumCommand.getUsername()
                + " has added new album successfully.");
        album.getSongs().addAll(addAlbumCommand.getSongs());

        ((Artist) currentUser).getAlbums().add(album);
        Library.getLibrary().getAlbums().add(album);
        Library.getLibrary().addSongsFromAlbums(album.getSongs());

        return commandOutput;
    }

    /**
     * metoda care adauga un eveniment in baza de date si in lista de evenimente a unui artist,
     * verificand intai daca artistul are deja un eveniment cu numele cerut prin apelarea metodei
     * "containsEvent", iar mai apoi verifica daca data dorita este valida cu ajutorul metodei
     * "validateDate"
     *
     * @param addEventCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutputInterface addEvent(final CommandInput addEventCommand) {
        MessageOnly commandOutput = new MessageOnly(addEventCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                addEventCommand.getUsername(), addEventCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + addEventCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("artist")) {
            commandOutput.setMessage(addEventCommand.getUsername()
                    + " is not an artist.");
            return commandOutput;
        }

        if (containsEvent(addEventCommand.getUsername(), addEventCommand.getName())) {
            commandOutput.setMessage(addEventCommand.getUsername()
                    + " has another event with the same name.");
            return commandOutput;
        }

        if (!validateDate(addEventCommand.getDate())) {
            commandOutput.setMessage("Event for " + addEventCommand.getUsername()
                    + " does not have a valid date");
            return commandOutput;
        }

        commandOutput.setMessage(addEventCommand.getUsername()
                + " has added new event successfully.");

        Event event = new Event(addEventCommand.getName(), addEventCommand.getDate(),
                addEventCommand.getDescription());
        ((Artist) currentUser).getEvents().add(event);
        Library.getLibrary().getEvents().add(event);

        return commandOutput;
    }

    /**
     * metoda care verifica daca numele unui eveniment se regaseste deja in lista de evenimente a
     * utilizatorului curent
     *
     * @param artistUsername numele artistului care doreste sa adauge evenimentul
     * @param eventName      numele dorit pentru eveniment
     * @return rezultatul verificarii, adevarat sau fals
     */
    public static Boolean containsEvent(final String artistUsername, final String eventName) {
        return UsersHistory.getUsersHistory().getArtists().get(artistUsername).getEvents()
                .stream()
                .anyMatch(event -> event.getName().equals(eventName));
    }

    /**
     * metoda care verifica daca data dorita de catre artist este valida
     *
     * @param date data ceruta
     * @return rezultatul verificarii, adevarat sau fals
     */
    public static Boolean validateDate(final String date) {

        try {
            LocalDate parsedDate = LocalDate.parse(date, dateFormat);
            int day = parsedDate.getDayOfMonth();
            int month = parsedDate.getMonthValue();
            int year = parsedDate.getYear();

            if (day > MAXIMUM_DAY_OF_THE_MONTH || month > MAXIMUM_MONTH_OF_THE_YEAR
                    || year < MINIMUM_YEAR_ALLOWED || year > MAXIMUM_YEAR_ALLOWED) {
                return Boolean.FALSE;
            } else if (month == FEBRUARY_MONTH && day > MAXIMUM_DAY_OF_FEBRUARY) {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * metoda care adauga un merch in lista de merch-uri a unui artist, verificand intai daca
     * artistul are deja un merch cu numele cerut prin apelarea metodei "containsMerch", iar mai
     * apoi verifica daca pretul dorit este valid cu ajutorul metodei "validateMerchPrice"
     *
     * @param addMerchCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput addMerch(final CommandInput addMerchCommand) {
        MessageOnly commandOutput = new MessageOnly(addMerchCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                addMerchCommand.getUsername(), addMerchCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + addMerchCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("artist")) {
            commandOutput.setMessage(addMerchCommand.getUsername()
                    + " is not an artist.");
            return commandOutput;
        }

        if (containsMerch(currentUser.getUsername(), addMerchCommand.getName())) {
            commandOutput.setMessage(currentUser.getUsername()
                    + " has merchandise with the same name.");
            return commandOutput;
        }

        if (!validateMerchPrice(addMerchCommand.getPrice())) {
            commandOutput.setMessage("Price for merchandise can not be negative.");
            return commandOutput;
        }

        commandOutput.setMessage(currentUser.getUsername()
                + " has added new merchandise successfully.");

        Merch merch = new Merch(addMerchCommand.getName(), addMerchCommand.getPrice(),
                addMerchCommand.getDescription());
        ((Artist) currentUser).getMerch().add(merch);
        return commandOutput;
    }

    /**
     * metoda care verifica daca numele unui merch se regaseste deja in lista de merch-uri a
     * utilizatorului curent
     *
     * @param artistUsername numele artistului care doreste sa adauge merch-ul
     * @param merchName      numele dorit pentru merch
     * @return rezultatul verificarii, adevarat sau fals
     */
    public static Boolean containsMerch(final String artistUsername, final String merchName) {
        return UsersHistory.getUsersHistory().getArtists().get(artistUsername).getMerch()
                .stream()
                .anyMatch(merch -> merch.getName().equals(merchName));
    }

    /**
     * metoda care verifica daca pretul dorita de catre artist este valid
     *
     * @param price pretul cerut
     * @return rezultatul verificarii, adevarat sau fals
     */
    public static Boolean validateMerchPrice(final double price) {
        return price > 0;
    }

    /**
     * metoda care elimina un album din baza de date si din lista de albume a artistului
     *
     * @param removeAlbumCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput removeAlbum(final CommandInput removeAlbumCommand) {
        MessageOnly commandOutput = new MessageOnly(removeAlbumCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                removeAlbumCommand.getUsername(), removeAlbumCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + removeAlbumCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("artist")) {
            commandOutput.setMessage(removeAlbumCommand.getUsername()
                    + " is not an artist.");
            return commandOutput;
        }

        if (!containsAlbum(removeAlbumCommand.getUsername(), removeAlbumCommand.getName())) {
            commandOutput.setMessage(removeAlbumCommand.getUsername()
                    + " doesn't have an album with the given name.");
            return commandOutput;
        }

        Album currentAlbum = findAlbum(removeAlbumCommand.getName());

        if (checkIfAlbumIsLoaded(currentUser, removeAlbumCommand.getName())
                || checkIfPlaylistContainsAlbumSong(currentAlbum.getSongs())) {
            commandOutput.setMessage(removeAlbumCommand.getUsername()
                    + " can't delete this album.");
            return commandOutput;
        }

        ((Artist) currentUser).getAlbums().remove(currentAlbum);
        Library.getLibrary().getAlbums().remove(currentAlbum);
        commandOutput.setMessage(currentUser.getUsername()
                + " deleted the album successfully.");
        return commandOutput;
    }

    /**
     * metoda care verifica daca vreun album de-ale artistului este loaded la momentul curent de
     * timp
     *
     * @param currentUser
     * @param albumName
     * @return
     */
    public static Boolean checkIfAlbumIsLoaded(final User currentUser, final String albumName) {
        if (currentUser.getLastLoadedAudio() != null
                && currentUser.getLastLoadedAudio().getName().equals(albumName)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    /**
     * metoda care verifica daca vreun playlist contine vreo melodie dintr-un anumit album
     *
     * @param albumSongs lista de melodii din album
     * @return adevarat daca playlistul contine cel putin o melodie, fals altfel
     */
    public static Boolean checkIfPlaylistContainsAlbumSong(final List<Song> albumSongs) {
        List<Song> allSongsFromPlaylists = new ArrayList<>();
        Library.getLibrary().getPlaylists().forEach(playlist ->
                allSongsFromPlaylists.addAll(playlist.getSongs()));
        return allSongsFromPlaylists.stream().anyMatch(albumSongs::contains);
    }

    /**
     * metoda care cauta un album in baza de date
     *
     * @param albumName numele albumului de cautat
     * @return albumul cautat
     */
    public static Album findAlbum(final String albumName) {
        for (Album album : Library.getLibrary().getAlbums()) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }

    /**
     * metoda care verifica daca un artist poate sa fie sters, adica daca un album sau o melodie
     * dintr-un album de-ale lui este loaded de vreun alt user si daca niciun user nu se afla pe
     * pagina lui
     *
     * @param currentUser       artistul pe care vrem sa il stergem
     * @param deleteUserCommand comanda actuala
     * @return metoda intoarce adevarat daca niciun album si nicio melodie din vreun album nu sunt
     * loaded la momentul stergerii si daca niciun user nu se afla pe pagina artistului
     */
    @Override
    public Boolean canBeDeleted(final User currentUser, final CommandInput deleteUserCommand) {
        boolean isUserOnArtistPage = UsersHistory.getUsersHistory().getAllUsers().values()
                .stream()
                .anyMatch(user -> user.getCurrentPage().equals("Artist")
                        && user.getLastSelectedUser().equals(this));

        if (isUserOnArtistPage) {
            return false;
        }

        List<Audio> allLastLoadedAudio = UsersHistory.getUsersHistory().getAllUsers().values()
                .stream()
                .filter(user -> (!user.getUsername().equals(this.getUsername()))
                        && user.getLastLoadedAudio() != null)
                .map(user -> {
                    user.getLastLoadedAudio().updateStatus(user, deleteUserCommand.getTimestamp());
                    return user.getLastLoadedAudio();
                })
                .filter(Objects::nonNull)
                .toList();

        List<String> namesOfSongsFromAlbums = this.getAlbums().stream()
                .flatMap(album -> album.getSongs().stream().map(Song::getName))
                .toList();


        boolean isSongFromAlbumLoaded = allLastLoadedAudio.stream()
                .map(lastLoadedAudio -> lastLoadedAudio.getStats().getName())
                .anyMatch(namesOfSongsFromAlbums::contains);


        return (!isSongFromAlbumLoaded);
    }

    /**
     * stergrea propriu-zisa a artistului din baza de date, atat pe el, cat si elementele care ii
     * apartineau lui, spre exemplu merch-urile sau evenimentele
     */
    @Override
    public void delete() {
        List<Song> songsToRemove = albums.stream()
                .flatMap(album -> album.getSongs().stream())
                .toList();

        Library.getLibrary().getAllSongs().removeIf(songsToRemove::contains);
        UsersHistory.getUsersHistory().getAllUsers().values().forEach(user ->
                user.getLikedSongs().removeIf(songsToRemove::contains));
        Library.getLibrary().getAlbums().removeIf(this.getAlbums()::contains);
        UsersHistory.getUsersHistory().getAllUsers().remove(this.getUsername());
        UsersHistory.getUsersHistory().getArtists().remove(this.getUsername());
    }
}

