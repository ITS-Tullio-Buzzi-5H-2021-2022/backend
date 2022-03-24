package edu.tulliobuzzi.componenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static edu.tulliobuzzi.Enigma.ALPHABET;

public class Rotore implements Componente {

    private String identificatore;
    private int posizioneRotore;
    private int impostazioniAnello;
    private int[] posizioniTacca;
    private Map<String, String> configurazione;
    private Map<String, String> configurazioneInversa;

    public Rotore(String identificatore, Map<String, String> configurazione, Map<String, String> inversa,
                  int[] posizioniTacca, int posizioneRotore, int impostazioniAnello) {
        this.configurazione = configurazione;
        this.configurazioneInversa = inversa;
        this.identificatore = identificatore;
        this.posizioneRotore = posizioneRotore;
        this.impostazioniAnello = impostazioniAnello;
        this.posizioniTacca = posizioniTacca;
    }

    @Override
    public String avanza(String carattere) {
        return cifrazione(carattere, this.configurazione);
    }

    @Override
    public String arretra(String carattere) {
        return cifrazione(carattere, this.configurazioneInversa);
    }

    public String getIdentificatore() {
        return this.identificatore;
    }

    public int getPosizioneRotore() {
        return this.posizioneRotore;
    }


    private String cifrazione(String carattere, Map<String, String> configurazione) {
        int shift = this.posizioneRotore - this.impostazioniAnello;
        String shiftedCharacter = ALPHABET[
                ((Arrays.binarySearch(ALPHABET, carattere) + shift + ALPHABET.length) % ALPHABET.length - shift + 26) % 26];
        return configurazione.get(shiftedCharacter);
    }

    public boolean isAtTacca() {
        for (int i : this.posizioniTacca) {
            if (this.posizioneRotore == i)
                return true;
        }
        return false;
    }

    public void ruota() {
        this.posizioneRotore = (this.posizioneRotore + 1) % 26;
    }

}
