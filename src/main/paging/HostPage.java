package main.paging;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.CommandOutputInterface;
import fileio.output.MessageOnly;
import fileio.output.PageOutput;
import main.foradmin.UsersHistory;
import main.forhost.Host;
import main.foruser.User;

public class HostPage implements Page {

    /**
     * metoda care afiseaza pagina unui host daca utilizatorul curent se afla pe aceasta
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

        Host currentPageHost = UsersHistory.getUsersHistory()
                .getHosts().get(currentUser.getLastSelectedUser().getUsername());

        if (currentPageHost != null) {
            commandOutput.setMessage("Podcasts:\n\t" + currentPageHost.getPodcasts()
                    + "\n\nAnnouncements:\n\t" + currentPageHost.getAnnouncements());
        }

        return commandOutput;
    }

    /**
     * metoda care muta un utilizator de pe pagina unui host la o alta pagina, specificata de
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
