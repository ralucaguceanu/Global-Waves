package main.paging;

import fileio.input.CommandInput;
import main.foruser.User;
import main.foradmin.UsersHistory;

public class PageFactory {

    protected PageFactory() {
    }

    /**
     * metoda care instantiaza obiecte conform Factory Method Pattern, in functie de nevoie, fara sa
     * avem nevoie de tipul obiectului la runtime
     * @param commandInput comanda actuala
     * @return rezultatul comenzii
     */
    public static Page getCurrentPage(final CommandInput commandInput) {
        User currentUser = UsersHistory.getUsersHistory()
                .getCurrentUser(commandInput.getUsername(),
                        commandInput.getCommand());
        return switch (currentUser.getCurrentPage()) {
            case "Home" -> new HomePage();
            case "LikedContent" -> new LikedContentPage();
            case "Artist" -> new ArtistPage();
            case "Host" -> new HostPage();
            default -> null;
        };
    }
}
