package main.audiocollections;

import fileio.input.CommandInput;
import fileio.output.MessageOnly;
import lombok.Getter;
import lombok.Setter;
import main.audiocollections.audiosproperties.Stats;
import main.foruser.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class Audio {

    private String name;
    private Integer timestamp;
    private Integer duration;
    private Stats stats = new Stats();
    private String type;

    public Audio() {
    }

    public Audio(final String name) {
        this.name = name;
    }

    public Audio(final String name, final Integer duration) {
        this.name = name;
        this.duration = duration;
    }

    /**
     * realizam deep copies pentru a nu modifica in library
     *
     * @param audio obiectul de copiat
     */
    public void deepCopy(final Audio audio) {
    }

    /**
     * calculam numarul de like-uri din lista de melodii primita
     * a unui playlist sau a unui album
     *
     * @param songs lista de melodii
     * @return numarul total de like-uri
     */
    public int computeLikesFromAllSongs(final List<Song> songs) {
        return songs.stream()
                .mapToInt(Song::getLikes)
                .sum();
    }

    /**
     * metoda folosita pentru afisarea numelui unui fisier audio, spre exepmlu cand vrem sa
     * vedem continutul paginii de Home a unui utilizator
     *
     * @return numele fisierului audio
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * metoda implementata de subclase, folosita la actualizarea statusului ultimului fisier
     * incarcat al unui user, la momemntul de timp curent
     *
     * @param currentUser      userul curent, pentru care facem actualizarea
     * @param currentTimestamp momentul de timp curent
     */
    public abstract void updateStatus(User currentUser, Integer currentTimestamp);

    /**
     * metoda implementata de subclase, folosita la incarcarea unui fisier de catre utilizatorul
     * curent la momentul de timp
     *
     * @param currentUser  utilizatortul curent, care da load
     * @param commandInput momentul de timp curent
     * @return true daca loadul s-a realizat cu succes, false altfel
     */
    public abstract boolean load(User currentUser, CommandInput commandInput);

    /**
     * metoda implementata de subclase, folosita la a pune pauza sau a reveni la un fisier audio
     *
     * @param currentUser  userul care da comanda
     * @param commandInput momentul de timp curent
     */
    public abstract void playPause(User currentUser, CommandInput commandInput);

    /**
     * metoda implementata de subclase, folosita la adaugarea sau stergerea unei melodii
     * dintr-un playlist
     *
     * @param currentUser   userul care da comanda
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     */
    public abstract void addRemoveInPlaylist(User currentUser,
                                             CommandInput commandInput,
                                             MessageOnly commandOutput);

    /**
     * metoda implementata de subclase, folosita la contorizarea numarului de like-uri de pe melodii
     *
     * @param currentUser   userul care da like/unlike
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     * @param allSongs      lista de melodii
     */
    public abstract void like(User currentUser,
                              CommandInput commandInput,
                              MessageOnly commandOutput,
                              LinkedHashSet<Song> allSongs);

    /**
     * metoda implementata de subclase, folosita la contorizarea numarului de followers de pe
     * playlisturi
     *
     * @param currentUser   userul care da follow/unfollow
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     */
    public abstract void follow(User currentUser,
                                CommandInput commandInput,
                                MessageOnly commandOutput);

    /**
     * metoda care intoarce userul curent cu 90 de secunde in fisierul curent
     *
     * @param currentUser   userul curent
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     */
    public abstract void backward(User currentUser,
                                  CommandInput commandInput,
                                  MessageOnly commandOutput);

    /**
     * metoda care avanseaza userul curent cu 90 de secunde in fisierul curent
     *
     * @param currentUser   userul curent
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     */
    public abstract void forward(User currentUser,
                                 CommandInput commandInput,
                                 MessageOnly commandOutput);

    /**
     * metoda care duce userul curent la urmatorul fisier audio
     *
     * @param currentUser   userul curent
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     */
    public abstract void next(User currentUser,
                              CommandInput commandInput,
                              MessageOnly commandOutput);

    /**
     * metoda care duce userul curent la fisierul audio anterior
     *
     * @param currentUser   userul curent
     * @param commandInput  comanda actuala
     * @param commandOutput rezultatul comenzii
     */
    public abstract void prev(User currentUser,
                              CommandInput commandInput,
                              MessageOnly commandOutput);

    /**
     * metoda folosita la schimbarea starii de repeat
     *
     * @param timestamp momentul curent de timp
     */
    public abstract void changeRepeatStatus(Integer timestamp);

    /**
     * metoda folosita pentru compararea a doua fisiere audio, dupa nume
     *
     * @param o fisierul cu care comparam
     * @return valoarea de egalitate, adevarata sau falsa
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        Audio audio = (Audio) o;
        return Objects.equals(name, audio.name);
    }

}
