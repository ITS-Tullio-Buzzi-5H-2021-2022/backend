package edu.tulliobuzzi.componenti;

import java.util.ArrayList;

public class Rotore extends ComponenteAstratto{

    protected String identificatore;
    protected int posizioneRotore;
    protected int posizioneTacca;
    protected int impostazioniAnello;
    protected ArrayList<Integer> configurazioneInversa;

    public Rotore(String identificatore, String codifica, int posizioneRotore, int posizioneTacca, int impostazioniAnello) {
        super(codifica);
        configurazioneInversa = invertiConfigurazione(configurazione);
        this.identificatore = identificatore;
        this.posizioneRotore = posizioneRotore;
        this.posizioneTacca = posizioneTacca;
        this.impostazioniAnello = impostazioniAnello;
    }

    @Override
    public int avanza(int carattere) {
        return cifrazione(carattere, this.configurazione);
    }

    @Override
    public int arretra(int carattere) {
        return cifrazione(carattere, this.configurazioneInversa);
    }

    public String getIdentificatore() {
        return this.identificatore;
    }

    public int getPosizioneRotore() {
        return this.posizioneRotore;
    }

    private ArrayList<Integer> invertiConfigurazione(ArrayList<Integer> configurazione) {
        ArrayList<Integer> inverso = new ArrayList<Integer>();
        for(int i = 0; i < configurazione.size(); i++)
            inverso.add(0);

        for (int i = 0; i < configurazione.size(); i++) {
            int forward = this.configurazione.get(i);
            inverso.set(forward, i);
        }
        return inverso;
    }

    private int cifrazione(int carattere, ArrayList<Integer> configurazione) {
        int shift = this.posizioneRotore - this.impostazioniAnello;
        return (this.configurazione.get((carattere + shift + 26) % 26) - shift + 26) % 26;
    }

    public boolean isAtTacca() {
        return this.posizioneTacca == this.posizioneRotore;
    }

    public void ruota() {
        this.posizioneRotore = (this.posizioneRotore + 1) % 26;
    }

}
