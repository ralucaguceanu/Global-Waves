package main.paging;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.CommandOutputInterface;
import fileio.output.MessageOnly;
import fileio.output.PageOutput;
import main.foradmin.UsersHistory;
import main.forartist.Artist;
import main.foruser.User;

public class ArtistPage implements Page {

    /**
     * metoda care afiseaza pagina unui artist daca utilizatorul curent se afla pe aceasta
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

        Artist currentPageArtist = UsersHistory.getUsersHistory()
                .getArtists().get(currentUser.getLastSelectedUser().getUsername());

        if (currentPageArtist != null) {
            commandOutput.setMessage("Albums:\n\t" + currentPageArtist.getAlbums()
                    + "\n\nMerch:\n\t" + currentPageArtist.getMerch()
                    + "\n\nEvents:\n\t" + currentPageArtist.getEvents());
        }

        return commandOutput;
    }

    /**
     * metoda care muta un utilizator de pe pagina unui artist la o alta pagina, specificata de
     * utilizator
     *
     * @param changePageCommand comanda actuala
     * @return rezultatul comenzii
     */
    @Override
    public CommandOutput changePage(final CommandInput changePageCommand) {
        MessageOnly commandOutput = new MessageOnly(changePageCommand);
        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(changePageCommand.getUsername(), changePageCommand.getCommand());

        switch (changePageCommand.getNextPage()) {
            case "LikedContent" -> {
                currentUser.setCurrentPage("LikedContent");
                commandOutput.setMessage(currentUser.getUsername()
                        + " accessed LikedContent successfully.");
                return commandOutput;
            }
            case "Home" -> {
                currentUser.setCurrentPage("Home");
                commandOutput.setMessage(currentUser.getUsername()
                        + " accessed Home successfully.");
                return commandOutput;
            }
            default -> {
                commandOutput.setMessage(currentUser.getUsername()
                        + " is trying to access a non-existent page.");
                return commandOutput;
            }
        }
    }
}
