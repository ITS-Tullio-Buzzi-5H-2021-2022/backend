package edu.tulliobuzzi.algoritmo.componenti;

import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

public enum FabbricaRotori {
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
    private final int[] tacche;

    FabbricaRotori(Map<String, String> configurazione, int... tacche) {
        this.configurazione = configurazione;
        this.configurazioneInversa = invertiConfigurazione(configurazione);
        this.tacche = tacche;
    }

    private static Map<String, String> convertiStringa(String codifica) {
        TreeMap<String, String> configurazione = new TreeMap<>();
        for (int i = 0; i < ALPHABET.length; i++) {
            configurazione.put(ALPHABET[i], Character.toString(codifica.charAt(i)));
        }
        return configurazione;
    }

    private static Map<String, String> invertiConfigurazione(Map<String, String> configurazione) {
        Map<String, String> configurazioneInversa = new TreeMap<>();
        for (Map.Entry<String, String> value : configurazione.entrySet()) {
            configurazioneInversa.put(value.getValue(), value.getKey());
        }
        return configurazioneInversa;
    }

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
