package edu.tulliobuzzi.algoritmo.componenti;

import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

/**
 * Implementazione di un Riflettore
 */
public class Riflettore implements Componente {

    private final Map<String, String> configurazione; // configurazione del riflettore (lettera->codifica)

    /**
     * Costruttore del Riflettore
     * @param configurazione configurazione iniziale sotto forma di mappa
     */
    public Riflettore(Map<String, String> configurazione) {
        this.configurazione = configurazione;
    }

    @Override
    public String avanza(String carattere) {
        return this.configurazione.get(carattere);
    }

    @Override
    public String toString() {
        return "Riflettore{" +
                "configurazione=" + configurazione +
                '}';
    }
}
