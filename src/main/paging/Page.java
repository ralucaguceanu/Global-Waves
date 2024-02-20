package main.paging;

import fileio.input.CommandInput;
import fileio.output.CommandOutput;
import fileio.output.CommandOutputInterface;

public interface Page {

    /**
     * metoda care afiseaza pagina curenta
     *
     * @param printCurrentPageCommand comanda actuala
     * @return rezultatul comenzii
     */
    CommandOutputInterface printCurrentPage(CommandInput printCurrentPageCommand);

    /**
     * metoda care muta un utilizator de pe o pagina pe alta
     *
     * @param changePageCommand comanda actuala
     * @return rezultatul comenzii
     */
    CommandOutput changePage(CommandInput changePageCommand);
}
