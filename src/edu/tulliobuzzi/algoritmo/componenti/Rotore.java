package edu.tulliobuzzi.algoritmo.componenti;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static edu.tulliobuzzi.algoritmo.Enigma.ALPHABET;

/**
 * Implementazione di un rotore
 */
public class Rotore implements Componente {

    private final String identificatore;
    private final int impostazioniAnello;
    private final Map<String, String> configurazione;
    private final Map<String, String> configurazioneInversa;
    private final List<Integer> posizioniTacca;
    private int posizioneRotore;

    /**
     * Costruttore di Rotore
     * @param identificatore     nome del rotore (I, II, III, ...)
     * @param configurazione     configurazione del rotore (lettera->codifica)
     * @param inversa            configurazione inversa del rotore (codifica->lettera)
     * @param posizioniTacca     lista delle posizioni delle tacche
     * @param posizioneRotore    posizione iniziale del rotore
     * @param impostazioniAnello posizione dell'anello
     */
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

    /**
     * Metodi getter
     */
    public String getIdentificatore() {
        return this.identificatore;
    }

    public int getPosizioneRotore() {
        return this.posizioneRotore;
    }

    /**
     * Codifica del carattere secondo la configurazione del rotore
     * @param carattere carattere da cifrare
     * @param configurazione configurazione utilizzata per la cifrazione
     * @return carattere cifrato
     */
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

    /**
     * Metodo per verificare il rotore si trova alla posizione di una delle sue tacche
     * @return "true" se il rotore si trova alla posizione di una tacca, altrimenti "false"
     */
    public boolean isAtTacca() {
        return this.posizioniTacca.contains(this.posizioneRotore);
    }

    /**
     * Ruota il rotore, facendo avanzare la posizione
     */
    public void ruota() {
        this.posizioneRotore = (this.posizioneRotore + 1) % ALPHABET.size();
    }

    /**
     * Ruota il rotore, facendo arretrare la posizione
     */
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
