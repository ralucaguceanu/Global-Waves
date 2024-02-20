package main.paging;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.CommandOutputInterface;
import fileio.output.MessageOnly;
import fileio.output.PageOutput;
import main.foradmin.UsersHistory;
import main.foruser.User;


public class LikedContentPage implements Page {

    /**
     * metoda care afiseaza pagina LikedContent a unui utilizator, mai exact melodiile si
     * playlisturile apreciate de acesta
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

        String message = "Liked songs:\n\t" + currentUser.getLikedSongs()
                + "\n\nFollowed playlists:\n\t" + currentUser.getFollowedPlaylists();
        commandOutput.setMessage(message);

        return commandOutput;
    }

    /**
     * metoda care muta un utilizator de pe pagina de LikedContent pe pagina Home
     *
     * @param changePageCommand comanda actuala
     * @return rezultatul comenzii
     */
    @Override
    public CommandOutput changePage(final CommandInput changePageCommand) {
        MessageOnly commandOutput = new MessageOnly(changePageCommand);
        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(changePageCommand.getUsername(), changePageCommand.getCommand());
        if (!changePageCommand.getNextPage().equals("Home")) {
            commandOutput.setMessage(currentUser.getUsername()
                    + " is trying to access a non-existent page.");
            return commandOutput;
        }
        currentUser.setCurrentPage("Home");
        commandOutput.setMessage(currentUser.getUsername()
                + " accessed Home successfully.");
        return commandOutput;
    }
}
