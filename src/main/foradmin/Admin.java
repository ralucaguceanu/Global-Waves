package main.foradmin;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.MessageOnly;
import fileio.output.ResultsOnly;
import fileio.output.PodcastOutput;
import fileio.output.AlbumOutput;
import main.audiocollections.Library;
import main.forartist.Artist;
import main.forhost.Host;
import main.foruser.User;

public final class Admin {

    private Admin() {
    }

    /**
     * metoda pentru adaugarea unui utilizator nou, apeleaza addArtist, addHost si addNormalUser
     *
     * @param addUserCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput addUser(final CommandInput addUserCommand) {
        MessageOnly commandOutput = new MessageOnly(addUserCommand);

        if (UsersHistory.getUsersHistory().containsUser(addUserCommand.getUsername())) {
            commandOutput.setMessage("The username " + addUserCommand.getUsername()
                    + " is already taken.");
            return commandOutput;
        }

        if (addUserCommand.getType().equals("artist")) {
            addArtist(addUserCommand);
        } else if (addUserCommand.getType().equals("host")) {
            addHost(addUserCommand);
        } else {
            addNormalUser(addUserCommand);
        }

        commandOutput.setMessage("The username " + addUserCommand.getUsername()
                + " has been added successfully.");
        return commandOutput;
    }

    /**
     * metoda care adauga un host
     *
     * @param addUserCommand comanda actuala
     */
    private static void addHost(final CommandInput addUserCommand) {
        Host host = new Host(addUserCommand.getUsername(), addUserCommand.getAge(),
                addUserCommand.getCity(), addUserCommand.getType());
        Library.getLibrary().getHosts().add(host);
        UsersHistory.getUsersHistory().getAllUsers().put(addUserCommand.getUsername(), host);
        UsersHistory.getUsersHistory().getHosts().put(addUserCommand.getUsername(), host);
    }

    /**
     * metoda care adauga un artist
     *
     * @param addUserCommand comanda actuala
     */
    private static void addArtist(final CommandInput addUserCommand) {
        Artist artist = new Artist(addUserCommand.getUsername(), addUserCommand.getAge(),
                addUserCommand.getCity(), addUserCommand.getType());
        Library.getLibrary().getArtists().add(artist);
        UsersHistory.getUsersHistory().getAllUsers().put(addUserCommand.getUsername(), artist);
        UsersHistory.getUsersHistory().getArtists().put(addUserCommand.getUsername(), artist);
    }

    /**
     * metoda care adauga un user normal
     *
     * @param addUserCommand comanda actuala
     */
    private static void addNormalUser(final CommandInput addUserCommand) {
        User normalUser = new User(addUserCommand.getUsername(), addUserCommand.getAge(),
                addUserCommand.getCity(), addUserCommand.getType());
        Library.getLibrary().getNormalUsers().add(normalUser);
        UsersHistory.getUsersHistory().getAllUsers().put(addUserCommand.getUsername(), normalUser);
    }

    /**
     * metoda pentru afisarea albumele unui artist
     *
     * @param showAlbumsCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput showAlbums(final CommandInput showAlbumsCommand) {
        ResultsOnly commandOutput = new ResultsOnly(showAlbumsCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                showAlbumsCommand.getUsername(), showAlbumsCommand.getCommand());
        ((Artist) currentUser).getAlbums().forEach(album ->
                commandOutput.getResult().add(new AlbumOutput(album)));
        return commandOutput;
    }

    /**
     * metoda pentru afisarea podcasturilor unui host
     *
     * @param shoePodcastsCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput showPodcasts(final CommandInput shoePodcastsCommand) {
        ResultsOnly commandOutput = new ResultsOnly(shoePodcastsCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                shoePodcastsCommand.getUsername(), shoePodcastsCommand.getCommand());
        ((Host) currentUser).getPodcasts().forEach(podcast ->
                commandOutput.getResult().add(new PodcastOutput(podcast)));
        return commandOutput;
    }

    /**
     * metoda pentru stergerea unui user din baza de date; se verifica daca userul poate fi sters
     * apelend "canBeDeleted", iar in caz afirmativ se apeleaza apoi metoda "delete", pentru
     * eliminarea proriu-zisa
     *
     * @param deleteUserCommand comanda actuala
     * @return
     */
    public static CommandOutput deleteUser(final CommandInput deleteUserCommand) {
        MessageOnly commandOutput = new MessageOnly(deleteUserCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                deleteUserCommand.getUsername(), deleteUserCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + deleteUserCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.canBeDeleted(currentUser, deleteUserCommand)) {
            commandOutput.setMessage(deleteUserCommand.getUsername()
                    + " can't be deleted.");
            return commandOutput;
        }

        currentUser.delete();
        commandOutput.setMessage(deleteUserCommand.getUsername()
                + " was successfully deleted.");
        return commandOutput;
    }
}
