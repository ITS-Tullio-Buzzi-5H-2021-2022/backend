package edu.tulliobuzzi.algoritmo.componenti;

import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

public class Riflettore implements Componente {

    private final Map<String, String> configurazione;

    public Riflettore(Map<String, String> configurazione) {
        this.configurazione = configurazione;
    }

    @Override
    public String avanza(String carattere) {
        return this.configurazione.get(carattere);
    }

}
