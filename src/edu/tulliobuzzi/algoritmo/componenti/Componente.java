package edu.tulliobuzzi.algoritmo.componenti;

/**
 * Interfaccia comune tra le diverse componenti (Rotore, Riflettore, Pannello di controllo)
 */
public interface Componente {
    /**
     * Codifica il carattere dato in input secondo la configurazione del Componente
     * @param carattere carattere da codificare
     * @return carattere codificato
     */
    String avanza(String carattere);

    /**
     * Decodifica il carattere dato in input secondo le regole del Componente
     * @param carattere carattere codificato
     * @return carattere decodificato
     */
    default String arretra(String carattere) {
        throw new UnsupportedOperationException();
    }
}
