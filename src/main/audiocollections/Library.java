package main.audiocollections;

import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.forartist.Artist;
import main.forartist.Event;
import main.forhost.Host;
import main.foruser.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public final class Library {

    // instanta clasei
    private static Library library = null;

    // toate melodiile din LibraryInput si din albume
    private Set<Song> allSongs = new LinkedHashSet<>();
    private List<Album> albums = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();
    private List<Podcast> podcasts = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<User> normalUsers = new ArrayList<>();
    private List<Artist> artists = new ArrayList<>();
    private List<Host> hosts = new ArrayList<>();

    private Library() {
    }

    /**
     * instantierea clasei Singleton dupa modelul Lazy instantiation
     *
     * @return instanta clasei, care simuleaza o baza de date
     */
    public static Library getLibrary() {
        if (library == null) {
            library = new Library();
        }
        return library;
    }

    /**
     * metoda pentru adaugarea in baza de date a melodiilor primite ca input
     *
     * @param songs lista de melodii primite
     */

    public void addSongsFromLibraryInput(final List<SongInput> songs) {
        library.getAllSongs().clear();
        songs.forEach(songInput -> {
            Song song = new Song(songInput);
            Library.getLibrary().getAllSongs().add(song);
        });
    }

    /**
     * adaugarea melodiilor din albumele adaugate de catre artisti
     *
     * @param songs lista de melodii din cate un album
     */
    public void addSongsFromAlbums(final List<Song> songs) {
        library.getAllSongs().addAll(songs);
    }


    /**
     * metoda pentru adaugarea in baza de date a podcasturilor primite ca input
     *
     * @param podcasts lista de podcasturi primite
     */
    public void addPodcastFromLibraryInput(final List<PodcastInput> podcasts) {
        library.getPodcasts().clear();
        podcasts.forEach(podcastInput -> {
            Podcast podcast = new Podcast(podcastInput);
            library.getPodcasts().add(podcast);
        });
    }
}
