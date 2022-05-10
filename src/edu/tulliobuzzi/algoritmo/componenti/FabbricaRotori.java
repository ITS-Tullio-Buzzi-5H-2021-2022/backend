package edu.tulliobuzzi.algoritmo.componenti;

import com.google.gson.JsonObject;
import edu.tulliobuzzi.algoritmo.Enigma;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

/**
 * Implementazione del design pattern Factory per la creazione di Rotori
 */
public enum FabbricaRotori {
    /**
     * Lista dei rotori standard di Enigma
     */
    I(convertiStringa("EKMFLGDQVZNTOWYHXUSPAIBRCJ"), 16),
    II(convertiStringa("AJDKSIRUXBLHWTMCQGZNPYFVOE"), 4),
    III(convertiStringa("BDFHJLCPRTXVZNYEIWGAKMUSQO"), 21),
    IV(convertiStringa("ESOVPZJAYQUIRHXLNFTGKDCMWB"), 9),
    V(convertiStringa("VZBRGITYUPSDNHLXAWMJQOFECK"), 25),
    VI(convertiStringa("JPGVOUMFYQBENHZRDKASXLICTW"), 12, 25),
    VII(convertiStringa("NZJHGRCXMYSWBOUFAIVLPEKQDT"), 12, 25),
    VIII(convertiStringa("FKQHTLXOCBJSPDZRAMEWNIUYGV"), 12, 25),
    Default(convertiStringa("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), 0);

    private final Map<String, String> configurazione;
    private final Map<String, String> configurazioneInversa;
    private final List<Integer> tacche;

    FabbricaRotori(Map<String, String> configurazione, Integer... tacche) {
        this.configurazione = configurazione;
        this.configurazioneInversa = invertiConfigurazione(configurazione);
        this.tacche = Arrays.stream(tacche).toList();
    }

    /**
     * Parsing del JSON rappresentante il rotore
     * @param rotor rappresentazione JSON del rotore
     * @return Rotore creato
     */
    public static Rotore fromJsonObject(JsonObject rotor) {
        return FabbricaRotori.valueOf(rotor.get("name").getAsString())
                .build(
                        Enigma.ALPHABET.indexOf(rotor.get("pos").getAsString()),
                        Enigma.ALPHABET.indexOf(rotor.get("ring").getAsString())
                );
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
     * Creazione della configurazione inversa che trasforma ogni coppia (chiave, valore)
     * nella coppia (valore, chiave)
     * @param configurazione da invertire
     * @return configurazione invertita
     */
    private static Map<String, String> invertiConfigurazione(Map<String, String> configurazione) {
        Map<String, String> configurazioneInversa = new TreeMap<>();
        for (Map.Entry<String, String> value : configurazione.entrySet()) {
            configurazioneInversa.put(value.getValue(), value.getKey());
        }
        return configurazioneInversa;
    }

    /**
     * Creazione dell'oggetto rotore
     * @param posizioneRotore posizione iniziale del rotore
     * @param posizioneAnello posizione dell'anello
     * @return Rotore creato
     */
    public Rotore build(int posizioneRotore, int posizioneAnello) {
        return new Rotore(
                this.name(),
                this.configurazione,
                this.configurazioneInversa,
                this.tacche,
                posizioneRotore,
                posizioneAnello
        );
    }
}
