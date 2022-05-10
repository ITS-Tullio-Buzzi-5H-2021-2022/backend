package edu.tulliobuzzi.algoritmo.componenti;

import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

/**
 * Implementazione del design pattern Factory per la creazione di Riflettori
 */
public enum FabbricaRiflettori {
    /**
     * Lista dei riflettori standard di Enigma
     */
    B(convertiStringa("YRUHQSLDPXNGOKMIEBFZCWVJAT")),
    C(convertiStringa("FVPJIAOYEDRZXWGCTKUQSBNMHL")),
    Default(convertiStringa("ZYXWVUTSRQPONMLKJIHGFEDCBA"));

    private final Map<String, String> configurazione;

    FabbricaRiflettori(Map<String, String> configurazione) {
        this.configurazione = configurazione;
    }

    /**
     * Conversione della stringa in input rappresentante la configurazione
     * @param codifica nella forma "HDFG" = A->H B->D C->F D->G
     * @return la configurazione
     */
    private static Map<String, String> convertiStringa(String codifica) {
        TreeMap<String, String> configurazione = new TreeMap<>();
        for (int i = 0; i < ALPHABET.size(); i++) {
            configurazione.put(ALPHABET.get(i), Character.toString(codifica.charAt(i)));
        }
        return configurazione;
    }

    /**
     * Effettiva creazione del riflettore
     * @return Riflettore creato
     */
    public Riflettore build() {
        return new Riflettore(this.configurazione);
    }
}
