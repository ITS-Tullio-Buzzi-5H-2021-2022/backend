package edu.tulliobuzzi.componenti;

import edu.tulliobuzzi.Enigma;

import java.util.*;

public class PannelloControllo implements Componente{
    private Map<String, String> configurazione;
    public PannelloControllo(Map<String, String> configurazione) {
        this.configurazione = configurazione;
    }
    public PannelloControllo(String configurazione) {
        this.configurazione = convertiStringa(configurazione);
    }

    @Override
    public String avanza(String carattere) {
        return this.configurazione.get(carattere);
    }

    private static Map<String, String> getConfigurazioneInizializzata() {
        TreeMap<String, String> configurazione = new TreeMap<>();
        for(String character : Enigma.ALPHABET) {
            configurazione.put(character, character);
        }
        return configurazione;
    }

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

            configurazione.put(c1,c2);
            configurazione.put(c2,c1);
        }

        return configurazione;
    }
}
