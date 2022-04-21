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

    TreeMap<String, String> convertiStringa(String codifica) {
        TreeMap<String, String> configurazione = new TreeMap<>();
        for (int i = 0; i < ALPHABET.length; i++) {
            configurazione.put(
                    ALPHABET[i],
                    Character.toString(codifica.charAt(i)));
        }
        return configurazione;
    }
}
