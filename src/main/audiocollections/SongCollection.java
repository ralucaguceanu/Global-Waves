package main.audiocollections;

import fileio.input.CommandInput;

public interface SongCollection {

    /**
     * metoda default dezvoltata in clasele ce implementeaza interfata, scopul ei este de a gasi
     * duplicate
     *
     * @param commandInput comanda actuala
     * @return rezultatul verificarii, adevart sau fals
     */
    Boolean hasDuplicates(CommandInput commandInput);

}
