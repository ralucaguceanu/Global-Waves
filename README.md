# Global Waves

Implemenatarea unui player muzical folosind conceptele de moștenire, polimorfism
upcasting, downcasting, clase abstracte, interfețe, deep copy, design patternul
Singleton și Factory Method. Logica se bazează pe manipularea timpului curent, la
care s-a dat o anumită comandă, și a timpului comenzii anterioare, reținut pentru
fiecare utilizator.

### Structura fișierelor

* src/fileio
  * input
    * CommandInput - stochează detaliile despre comenzile primite
    * EpisodeInput, LibraryInput, PodcastInput, SongInput, UserInput - conțin
      datele despre utilizatorii playerului muzical și despre fișierele audio
      din acesta
  * output - tipurile de afișări în funcție de rezultat
    * AlbumOutput - particularizarea afișării pentru caracteristicile
      albumelor
    * CommandOutput - este extinsă de celelalte tipuri de rezultat
    * CommandOutputInterface - pentru outputurile care nu se încadrau în
      formatul celor anterioare, pentru păstrarea logicii
    * MessageAndResults - folosită la căutare, pentru afișarea mesajului și a
      listei de rezultate găsite
    * MessageOnly - afișarea mesajului rezultat
    * NoMessageAndStats - folosită la afișarea statusului unui fișier audio
    * NoUserAndResult - pentru comenzile care nu sunt date de un user
    * PageOutput - particularizarea afișării pentru caracteristicile
      paginilor
    * PlaylistOutput - particularizarea afișării pentru caracteristicile
      playlisturilor
    * PodcastOutput - particularizarea afișării pentru caracteristicile
      podcasturilor
    * ResultsOnly - doar afișarea rezultatelor, pentru statistici
* src/main/
  * audiocollections
    * audiosproperties
      * Filter - conține toate tipurile de filtre după care sunt căutate
        fișierele audio
      * Stats - este conținută de clasa Audio și reține starea în care se află
        un fișier audio
    * Album - extinde clasa Audio și este în relație de agregare cu clasa Song
    * Audio - este extinsă și particularizată de tipurile de fișiere audio
    * Episode - este în relație de compunere cu clasa Podcast
    * Library - clasa Singleton ce simulează o bază de date cu informații despre
      fișiere audio și utilizatori
    * Playlist - este în relație de agregare cu clasa Song
    * Podcast - conține episoade
    * Song - este conținută de Playlist și Album, dar are utilități și în afara
      acestor clase
    * SongCollection - interfață implementată de Playlist și Album pentru
      operații comune
  * foradmin
    * Admin - clasa unde se găsesc toate comenzile date de un admin
    * CommandRunner - centralizeză și execută toate comenzile care se pot da
    * NormalUserCommand - conține comenzile date doar de utilizatorii normali
    * OngoingPodcast - în relație de agregare cu Podcast; conține o listă
      cu toate podcasturile începute de utilizatori și timpul care s-a derulat
      din podcastul curent
    * Statistics - conține metode pentru realizarea unor statistici cu privire
      la fișierele urmărite și apreciate de utilizatori
    * UsersHistory - clasă Singleton, întrucât ajută la stocarea activității
      tuturor utilizatorilor; pe tot parcursul proiectului avem nevoie de
      detalii despre utilizatorul care a dat comanda pentru a întoarce un
      rezultat potrivit. De aceea, această clasă conține un HashMap cu ajutorul
      căruia putem găsi ușor datele utilizatorului curent după numele acestuia
  * forartist
    * Artist - extinde clasa User, conținând particularizări și comenzi
      specifice unui artist
    * Event - în relație de agregare cu clasa Artist, având metode pentru
      comenzile referitoare la evenimente
    * Merch - în relație de agregare cu clasa Artist, având metode pentru
      comenzile referitoare la merchandising
  * forhost
    * Host - extinde clasa User, conținând particularizări și comenzi
      specifice unui host
    * Announcement - în relație de agregare cu clasa Artist, având metode pentru
      comenzile referitoare la anunțuri
  * foruser
    * Player - conține metodele prin care utilizatorul interacționează cu
      playerul muzical. Un utilizator poate încărca un fișier, poate să îl
      pună pe pauză, să revină la el, să afișeze starea în care se află, să
      creeze un playlist, să adauge melodii în el, să aprecieze o melodie, să
      urmărească un playlist, să asculte un fișier audio pe repeat, să se
      întoarcă sau să avanseze cu 90 de secunde într-un episod din podcast
      sau să ajungă la fișierele audio vecine
    * SearchBar - clasa în care se face căutarea și selectarea fișierelor
      audio în funcție de filtre
    * User - stochează atât informații personale despre utilizator, cât și
        detalii despre activitatea acestuia, cum ar fi rezultatele de la ultima
        căutare realizată cu succes, ultimul fișier selectat, ultimul fișier
        încărcat și podcasturile începute; se află în relație de agregare cu
        clasele Audio, Song, Playlist, Album și OngoingPodcast
  * paging
    * ArtistPage - implementeaza interfața Page, având particularizări pentru un
      artist
    * HomePage - implementează interfața Page, având particularizări pentru o
      pagină de recomandări în funcție de preferințele utilizatorului
    * HostPage - implementează interfața Page, având particularizări pentru un
      host
    * LikedContentPage - implementează interfața Page, conține preferințele
      utilizatorului
    * Page - interfața folosită pentru implementarea Factory Method Pattern
    * PageFactory - instanțiază obiecte de tip Page conform Factory Method
      Pattern
  * Main - pentru gestionarea inputului și a outputului; executarea proiectului
  * Test - folosită la testare
