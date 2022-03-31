package edu.tulliobuzzi.componenti;

import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.Enigma.ALPHABET;

public enum FabbricaRiflettori {
    B(convertiStringa("YRUHQSLDPXNGOKMIEBFZCWVJAT")),
    C(convertiStringa("FVPJIAOYEDRZXWGCTKUQSBNMHL")),
    Default(convertiStringa("ZYXWVUTSRQPONMLKJIHGFEDCBA"));

    private final Map<String, String> configurazione;

    FabbricaRiflettori(Map<String, String> configurazione) {
        this.configurazione = configurazione;
    }

    public Riflettore build() {
        return new Riflettore(this.configurazione);
    }

    private static Map<String, String> convertiStringa(String codifica) {
        TreeMap<String, String> configurazione = new TreeMap<>();
        for (int i = 0; i < ALPHABET.length; i++) {
            configurazione.put(ALPHABET[i], Character.toString(codifica.charAt(i)));
        }
        return configurazione;
    }
}
