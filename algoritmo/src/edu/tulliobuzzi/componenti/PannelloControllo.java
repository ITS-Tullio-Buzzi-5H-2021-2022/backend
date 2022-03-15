package edu.tulliobuzzi.componenti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PannelloControllo extends ComponenteAstratto{

    public PannelloControllo(String configurazione) {
        super(configurazione);
    }

    @Override
    public int avanza(int carattere) {
        return this.configurazione.get(carattere);
    }

    private ArrayList<Integer> getConfigurazioneInizializzata() {
        ArrayList<Integer> configurazione = new ArrayList<>(Collections.nCopies(26, null));
        for (int i = 0; i < 26; i++) {
            configurazione.set(i,i);
        }
        return configurazione;
    }

    ArrayList<Integer> convertiStringa(String codifica) {
        codifica = codifica.toUpperCase();
        ArrayList<Integer> configurazione = getConfigurazioneInizializzata();

        if (codifica == null || codifica.equals("")) {
            return configurazione;
        }

        String[] coppie = codifica.split("[^a-zA-Z]");
        Set<Integer> caratteriCollegati = new HashSet<>();

        //validazione configurazione
        for (String coppia : coppie) {
            if (coppia.length() != 2)
                return configurazione;

            // Codifica da ASCII a indice
            int c1 = coppia.charAt(0) - 65;
            int c2 = coppia.charAt(1) - 65;

            if (caratteriCollegati.contains(c1) || caratteriCollegati.contains(c2)) {
                return configurazione;
            }

            caratteriCollegati.add(c1);
            caratteriCollegati.add(c2);

            configurazione.set(c1,c2);
            configurazione.set(c2,c1);
        }

        return configurazione;
    }
}
