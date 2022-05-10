package edu.tulliobuzzi.algoritmo.componenti;

import edu.tulliobuzzi.algoritmo.Enigma;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implementazione del Pannello di Controllo
 */
public class PannelloControllo implements Componente {
    private final Map<String, String> configurazione;

    /**
     * Costruttore del Pannello di Controllo
     * @param configurazione configurazione iniziale sotto forma di mappa
     */
    public PannelloControllo(Map<String, String> configurazione) {
        this.configurazione = configurazione;
    }

    /**
     * Costruttore del Pannello di Controllo
     * @param configurazione configurazione iniziale sotto forma di stringa (vedi convertiStringa)
     */
    public PannelloControllo(String configurazione) {
        this.configurazione = convertiStringa(configurazione);
    }

    /**
     * La configurazione viene inizializzata associando ogni lettera con se stessa
     * @return la mappa che associa ogni lettera con la sua traduzione
     */
    private static Map<String, String> getConfigurazioneInizializzata() {
        TreeMap<String, String> configurazione = new TreeMap<>();
        for (String character : Enigma.ALPHABET) {
            configurazione.put(character, character);
        }
        return configurazione;
    }

    /**
     * Parsing della stringa in input rappresentante la configurazione
     * @param codifica nella forma "AB CD" = A->B, C->D
     * @return configurazione aggiornata
     */
    public static Map<String, String> convertiStringa(String codifica) {
        codifica = codifica.toUpperCase();
        Map<String, String> configurazione = PannelloControllo.getConfigurazioneInizializzata();

        if (codifica == null || codifica.equals("")) {
            return configurazione;
        }

        String[] coppie = codifica.split("[^a-zA-Z]");
        Set<String> caratteriCollegati = new HashSet<>();

        //validazione configurazione
        for (String coppia : coppie) {
            if (coppia.length() != 2)
                return configurazione;

            // Codifica da ASCII a indice
            String c1 = Character.toString(coppia.charAt(0));
            String c2 = Character.toString(coppia.charAt(1));

            if (caratteriCollegati.contains(c1) || caratteriCollegati.contains(c2)) {
                return configurazione;
            }

            caratteriCollegati.add(c1);
            caratteriCollegati.add(c2);

            configurazione.put(c1, c2);
            configurazione.put(c2, c1);
        }

        return configurazione;
    }

    @Override
    public String avanza(String carattere) {
        return this.configurazione.get(carattere);
    }

    @Override
    public String toString() {
        return "PannelloControllo{" +
                "configurazione=" + configurazione +
                '}';
    }
}
