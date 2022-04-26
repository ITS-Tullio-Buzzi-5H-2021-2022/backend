package edu.tulliobuzzi.algoritmo.componenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

public class Rotore implements Componente {

    private final String identificatore;
    private final int impostazioniAnello;
    private final Map<String, String> configurazione;
    private final Map<String, String> configurazioneInversa;
    private final int[] posizioniTacca;
    private int posizioneRotore;

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

        int inputPosition = Arrays.binarySearch(ALPHABET, carattere);
        int shiftedInputPosition = (inputPosition + shift + ALPHABET.length) % ALPHABET.length;
        String shiftedInput = ALPHABET[shiftedInputPosition];

        String shiftedOutput = configurazione.get(shiftedInput);
        int shiftedOutputPosition = Arrays.binarySearch(ALPHABET, shiftedOutput);
        int outputPosition = (shiftedOutputPosition - shift + ALPHABET.length) % ALPHABET.length;

        return ALPHABET[outputPosition];
    }

    public boolean isAtTacca() {
        for (int i : this.posizioniTacca) {
            if (this.posizioneRotore == i)
                return true;
        }
        return false;
    }

    public void ruota() {
        this.posizioneRotore = (this.posizioneRotore + 1) % ALPHABET.length;
    }

    public void ruotaIndietro() {
        this.posizioneRotore = (this.posizioneRotore - 1 + ALPHABET.length) % ALPHABET.length;
    }

}
