package edu.tulliobuzzi.algoritmo.componenti;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

public class Rotore implements Componente {

    private final String identificatore;
    private final int impostazioniAnello;
    private final Map<String, String> configurazione;
    private final Map<String, String> configurazioneInversa;
    private final List<Integer> posizioniTacca;
    private int posizioneRotore;

    public Rotore(String identificatore, Map<String, String> configurazione, Map<String, String> inversa,
                  List<Integer> posizioniTacca, int posizioneRotore, int impostazioniAnello) {
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
        final int shift = this.posizioneRotore - this.impostazioniAnello;
        final int size = ALPHABET.size();
        return ((Function<String, Integer>) (ALPHABET::indexOf))
                .andThen(c -> (c + shift + size) % size)
                .andThen(ALPHABET::get)
                .andThen(configurazione::get)
                .andThen(ALPHABET::indexOf)
                .andThen(c -> (c - shift + size) % size)
                .andThen(ALPHABET::get)
                .apply(carattere);
    }

    public boolean isAtTacca() {
        return this.posizioniTacca.contains(this.posizioneRotore);
    }

    public void ruota() {
        this.posizioneRotore = (this.posizioneRotore + 1) % ALPHABET.size();
    }

    public void ruotaIndietro() {
        this.posizioneRotore = (this.posizioneRotore - 1 + ALPHABET.size()) % ALPHABET.size();
    }

    @Override
    public String toString() {
        return "Rotore{" +
                "identificatore='" + identificatore + '\'' +
                ", impostazioniAnello=" + impostazioniAnello +
                ", configurazione=" + configurazione +
                ", configurazioneInversa=" + configurazioneInversa +
                ", posizioniTacca=" + posizioniTacca +
                ", posizioneRotore=" + posizioneRotore +
                '}';
    }
}
