package main.forhost;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Audio;
import main.audiocollections.Episode;
import main.audiocollections.Library;
import main.audiocollections.Podcast;
import main.foradmin.UsersHistory;
import main.foruser.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static main.audiocollections.Podcast.containsPodcast;

@Getter
@Setter
public final class Host extends User {

    private List<Podcast> podcasts = new ArrayList<>();
    private List<Announcement> announcements = new ArrayList<>();

    public Host(final String username, final Integer age, final String city, final String type) {
        super(username, age, city, type);
    }

    /**
     * metoda pentru adaugarea unui podcast de catre un host; se verifica daca hostul are deja un
     * podcast cu acelasi nume prin apelarea metodei "containsPodcast", apoi verificam daca
     * podcastul pe care artistul doreste sa il adauge contine un episod de cel putin 2 ori prin
     * apelarea metodei "hasDuplicates"
     *
     * @param addPodcastCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput addPodcast(final CommandInput addPodcastCommand) {
        MessageOnly commandOutput = new MessageOnly(addPodcastCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                addPodcastCommand.getUsername(), addPodcastCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + addPodcastCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("host")) {
            commandOutput.setMessage(addPodcastCommand.getUsername()
                    + " is not a host.");
            return commandOutput;
        }

        if (containsPodcast(currentUser.getUsername(), addPodcastCommand.getName())) {
            commandOutput.setMessage(addPodcastCommand.getUsername()
                    + " has another podcast with the same name.");
            return commandOutput;
        }

        Podcast podcast = new Podcast(addPodcastCommand.getName(), currentUser.getUsername(),
                (ArrayList<Episode>) addPodcastCommand.getEpisodes());

        if (podcast.hasDuplicates(addPodcastCommand)) {
            commandOutput.setMessage(addPodcastCommand.getUsername()
                    + " has the same episode in this podcast.");
            return commandOutput;
        }

        commandOutput.setMessage(addPodcastCommand.getUsername()
                + " has added new podcast successfully.");

        ((Host) currentUser).getPodcasts().add(podcast);
        Library.getLibrary().getPodcasts().add(podcast);

        return commandOutput;
    }

    /**
     * metoda care adauga un anunt in lista de anunturi a unui host, verificand intai daca hostul
     * are deja un anunt cu numele cerut prin apelarea metodei "containsAnnouncement"
     *
     * @param addAnnouncementCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput addAnnouncement(final CommandInput addAnnouncementCommand) {
        MessageOnly commandOutput = new MessageOnly(addAnnouncementCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                addAnnouncementCommand.getUsername(), addAnnouncementCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + addAnnouncementCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("host")) {
            commandOutput.setMessage(addAnnouncementCommand.getUsername()
                    + " is not a host.");
            return commandOutput;
        }

        if (containsAnnouncement(currentUser.getUsername(), addAnnouncementCommand.getName())) {
            commandOutput.setMessage(addAnnouncementCommand.getUsername()
                    + " has already added an announcement with this name.");
            return commandOutput;
        }

        Announcement announcement = new Announcement(addAnnouncementCommand.getName(),
                addAnnouncementCommand.getDescription());

        commandOutput.setMessage(addAnnouncementCommand.getUsername()
                + " has successfully added new announcement.");

        ((Host) currentUser).getAnnouncements().add(announcement);
        return commandOutput;
    }

    /**
     * metoda care verifica daca numele unui anunt se regaseste deja in lista de anunturi a
     * hostului curent
     *
     * @param hostUsername     numele hostului care doreste sa adauge anuntul
     * @param announcementName numele dorit pentru anunt
     * @return rezultatul verificarii, adevarat sau fals
     */
    public static Boolean containsAnnouncement(final String hostUsername,
                                               final String announcementName) {
        return UsersHistory.getUsersHistory().getHosts().get(hostUsername).getAnnouncements()
                .stream()
                .anyMatch(album -> album.getName().equals(announcementName));
    }

    /**
     * metoda care elimina un anunt din lista de anunturi a unui host, verificand intai daca anuntul
     * exista
     *
     * @param removeAnnouncementCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput removeAnnouncement(final CommandInput removeAnnouncementCommand) {
        MessageOnly commandOutput = new MessageOnly(removeAnnouncementCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                removeAnnouncementCommand.getUsername(), removeAnnouncementCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + removeAnnouncementCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("host")) {
            commandOutput.setMessage(removeAnnouncementCommand.getUsername()
                    + " is not a host.");
            return commandOutput;
        }

        if (!containsAnnouncement(currentUser.getUsername(), removeAnnouncementCommand.getName())) {
            commandOutput.setMessage(removeAnnouncementCommand.getUsername()
                    + " has no announcement with the given name.");
            return commandOutput;
        }

        for (Announcement announcement : UsersHistory.getUsersHistory().getHosts().
                get(currentUser.getUsername()).getAnnouncements()) {
            if (announcement.getName().equals(removeAnnouncementCommand.getName())) {
                UsersHistory.getUsersHistory().getHosts().get(currentUser.getUsername())
                        .getAnnouncements().remove(announcement);
                commandOutput.setMessage(currentUser.getUsername()
                        + " has successfully deleted the announcement.");
            }
        }

        return commandOutput;
    }

    /**
     * metoda care elimina un podcast atat din baza de date, verificand intai ca acesta sa nu fie
     * loaded de niciun user la momentul stergerii
     *
     * @param removePodcastCommand comanda actuala
     * @return rezultatul comenzii
     */
    public static CommandOutput removePodcast(final CommandInput removePodcastCommand) {
        MessageOnly commandOutput = new MessageOnly(removePodcastCommand);
        User currentUser = UsersHistory.getUsersHistory().getCurrentUser(
                removePodcastCommand.getUsername(), removePodcastCommand.getCommand());

        if (currentUser == null) {
            commandOutput.setMessage("The username " + removePodcastCommand.getUsername()
                    + " doesn't exist.");
            return commandOutput;
        }

        if (!currentUser.getType().equals("host")) {
            commandOutput.setMessage(removePodcastCommand.getUsername()
                    + " is not a host.");
            return commandOutput;
        }

        if (!containsPodcast(currentUser.getUsername(), removePodcastCommand.getName())) {
            commandOutput.setMessage(removePodcastCommand.getUsername()
                    + " doesn't have a podcast with the given name.");
            return commandOutput;
        }

        if (checkIfPodcastIsLoaded(currentUser, removePodcastCommand)) {
            commandOutput.setMessage(removePodcastCommand.getUsername()
                    + " can't delete this podcast.");
            return commandOutput;
        }

        Podcast currentPodcast = findPodcast(removePodcastCommand.getName());
        ((Host) currentUser).getPodcasts().remove(currentPodcast);
        Library.getLibrary().getPodcasts().remove(currentPodcast);
        commandOutput.setMessage(currentUser.getUsername()
                + " deleted the podcast successfully.");
        return commandOutput;
    }

    /**
     * metoda care verifica daca un podcast este loaded de catre vreun user la momentul curent de
     * timp
     * @param currentUser ownerul podcastul
     * @param commandInput comanda actuala
     * @return adevarat/fals
     */
    public static Boolean checkIfPodcastIsLoaded(final User currentUser,
                                                 final CommandInput commandInput) {

        List<Audio> allLastLoadedAudio = UsersHistory.getUsersHistory().getAllUsers().values()
                .stream()
                .filter(user -> (!user.getUsername().equals(currentUser.getUsername()))
                        && user.getLastLoadedAudio() != null)
                .map(user -> {
                    user.getLastLoadedAudio().updateStatus(user, commandInput.getTimestamp());
                    return user.getLastLoadedAudio();
                })
                .filter(Objects::nonNull)
                .toList();

        List<String> namesOfPodcasts = allLastLoadedAudio.stream().map(Audio::getName).toList();

        if (commandInput.getCommand().equals("removePodcast")) {
            return namesOfPodcasts.contains(commandInput.getName());
        }

        return allLastLoadedAudio.stream()
                .filter(lastLoadedAudio -> lastLoadedAudio.getType().equals("podcast"))
                .noneMatch(lastLoaded ->
                        ((Podcast) lastLoaded).getOwner().equals(commandInput.getUsername()));
    }

    /**
     * metoda care gaseste un podcast in baza de date
     *
     * @param podcastName numele podcastului de cautat
     * @return podcastul gasit
     */
    public static Podcast findPodcast(final String podcastName) {
        for (Podcast podcast : Library.getLibrary().getPodcasts()) {
            if (podcast.getName().equals(podcastName)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * metoda care verifica daca un host poate sa fie sters, adica daca niciun podcast de-ale lui
     * nu este loaded de vreun alt user si daca niciun user nu se afla pe pagina lui
     *
     * @param currentUser       hostul pe care vrem sa il stergem
     * @param deleteUserCommand comanda actuala
     * @return metoda intoarce adevarat daca niciun podcast nu este loaded la momentul stergerii si
     * daca niciun user nu se afla pe pagina hostului
     */
    @Override
    public Boolean canBeDeleted(final User currentUser, final CommandInput deleteUserCommand) {
        boolean isUserOnHostPage = UsersHistory.getUsersHistory().getAllUsers().values()
                .stream()
                .anyMatch(user -> user.getCurrentPage().equals("Host")
                        && user.getLastSelectedUser().equals(this));

        if (isUserOnHostPage) {
            return false;
        }

        return checkIfPodcastIsLoaded(currentUser, deleteUserCommand);
    }

    @Override
    public void delete() {
        super.delete();
    }
}
