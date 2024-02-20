package main.foradmin;

import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Library;
import main.forartist.Artist;
import main.forhost.Host;
import main.foruser.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public final class UsersHistory {

    private HashMap<String, User> allUsers = new HashMap<>();
    private HashMap<String, Artist> artists = new HashMap<>();
    private HashMap<String, Host> hosts = new HashMap<>();

    // instanta clasei
    private static UsersHistory usersHistory = null;

    private UsersHistory() {
    }

    /**
     * instantierea clasei Singleton dupa modelul Lazy instantiation
     *
     * @return instanta clasei
     */
    public static UsersHistory getUsersHistory() {
        if (usersHistory == null) {
            usersHistory = new UsersHistory();
        }
        return usersHistory;
    }

    /**
     * extragerea din baza de date a userului curent
     *
     * @param username numele userului
     * @param command  comanda actuala
     * @return userul curent
     */
    public User getCurrentUser(final String username, final String command) {
        User user;
        if (containsUser(username)) {
            user = usersHistory.getAllUsers().get(username);
        } else {
            return null;
        }
        if (command.equals("search")) {
            user.setLastSearchResults(new ArrayList<>());
            user.setLastUserSearchResults(new ArrayList<>());
        }
        return user;
    }

    /**
     * verificam daca userul se afla in baza noastra de date
     *
     * @param username numele userului
     * @return afirmativ sau negativ
     */
    public boolean containsUser(final String username) {
        return UsersHistory.getUsersHistory().getAllUsers().containsKey(username);
    }

    /**
     * verificam daca numele unui playlist exista in baza de date
     *
     * @param name numele playlistului
     * @return afirmativ sau negativ
     */
    public boolean checkIfPlaylistExists(final String name) {
        for (User u : allUsers.values()) {
            List<String> playlistNames = u.getPlaylists().stream().map(p -> p.getName()).toList();
            if (playlistNames.contains(name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * metoda care adauga in baza de date userii primiti ca input
     *
     * @param userInputs userii primiti
     */
    public void populateUserDatabase(final List<UserInput> userInputs) {
        UsersHistory.getUsersHistory().getAllUsers().clear();
        Library.getLibrary().getNormalUsers().clear();
        userInputs.forEach(userInput -> {
            allUsers.put(userInput.getUsername(), new User(userInput));
            Library.getLibrary().getNormalUsers()
                    .add(UsersHistory.getUsersHistory().getAllUsers().get(userInput.getUsername()));
        });

    }

}
