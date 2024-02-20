package main.foruser;

import fileio.input.CommandInput;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.Audio;
import main.audiocollections.Library;
import main.audiocollections.Playlist;
import main.audiocollections.Song;
import main.foradmin.OngoingPodcast;
import main.foradmin.UsersHistory;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class User {

    private String username;
    private Integer age;
    private String city;
    private List<Song> likedSongs = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();
    private List<Playlist> followedPlaylists = new ArrayList<>();
    private List<Audio> lastSearchResults;
    private Audio lastSelectedAudio;
    protected Audio lastLoadedAudio;
    private List<OngoingPodcast> ongoingPodcasts = new ArrayList<>();
    private Boolean isOnline = Boolean.TRUE;
    private String type = "user";
    private String currentPage = "Home";
    private List<User> lastUserSearchResults;
    private User lastSelectedUser;

    public User() {
    }

    public User(final String username, final Integer age, final String city, final String type) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.type = type;
    }

    public User(final String username) {
        this.username = username;
    }

    public User(final UserInput userInput) {
        this.setUsername(userInput.getUsername());
        this.setAge(userInput.getAge());
        this.setCity(userInput.getCity());
    }

    /**
     * generarea id-ului playlisturilor in ordine crescatoare
     *
     * @return id-ul playlistului
     */
    public Integer generatePlaylistId() {
        if (playlists.isEmpty()) {
            return 1;
        } else {
            return playlists.size() + 1;
        }
    }

    /**
     * gasirea unui playlist dupa id
     *
     * @param id id-ul de cautat
     * @return playlistul cu id-ul cautat
     */
    public Playlist getPlaylistById(final Integer id) {
        List<Playlist> playlist = playlists.stream()
                .filter(p -> p.getPlaylistId().equals(id)).toList();
        return playlist.isEmpty() ? null : playlist.get(0);
    }

    /**
     * metoda care verifica daca un user poate sa fie sters, adica daca un playlist de-ale lui este
     * loaded de vreun alt user
     *
     * @param currentUser       userul pe care vrem sa il stergem
     * @param deleteUserCommand comanda actuala
     * @return metoda intoarce adevarat daca niciun playlist nu este loaded la momentul stergerii
     */
    public Boolean canBeDeleted(final User currentUser, final CommandInput deleteUserCommand) {
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

        List<String> namesOfSongsFromPlaylists = this.getPlaylists().stream()
                .flatMap(playlist -> playlist.getSongs().stream().map(Song::getName))
                .toList();

        boolean isSongFromPlaylistLoaded = allLastLoadedAudio.stream()
                .map(lastLoadedAudio -> lastLoadedAudio.getStats().getName())
                .anyMatch(namesOfSongsFromPlaylists::contains);

        return (!isSongFromPlaylistLoaded);
    }

    /**
     * stergrea propriu-zisa a userului si a elementelor care apartineau lui (playlisturile), atat
     * din baza de date, cat si din elementele cu care interactiona, spre exemplu melodiile la care
     * a dat like si playlisturile la care a dat follow
     */
    public void delete() {
        List<Song> songsToRemove = playlists.stream()
                .flatMap(album -> album.getSongs().stream())
                .toList();

        Library.getLibrary().getAllSongs().removeIf(songsToRemove::contains);

        // stergem follow-urile de la user
        Library.getLibrary().getPlaylists().stream()
                .filter(playlist -> UsersHistory.getUsersHistory().getAllUsers().values().stream()
                        .flatMap(user -> user.getFollowedPlaylists().stream())
                        .anyMatch(followedPlaylist -> followedPlaylist.equals(playlist)))
                .forEach(playlist ->
                        playlist.setFollowers(playlist.getFollowers() - 1)
                );

        // stergem playlisturile userului din library
        Library.getLibrary().getPlaylists().removeIf(playlists::contains);

        // stergem melodiile playlisturilor din lista "likedSong" a fiecarui user
        UsersHistory.getUsersHistory().getAllUsers().values().forEach(user ->
                user.getLikedSongs().removeIf(songsToRemove::contains));
        // stergem playlisturile din lista "followedPlaylist" a fiecarui user
        UsersHistory.getUsersHistory().getAllUsers().values().forEach(user ->
                user.getFollowedPlaylists().removeIf(playlists::contains));
        // stergem userul
        UsersHistory.getUsersHistory().getAllUsers().remove(this.getUsername());
    }

}
