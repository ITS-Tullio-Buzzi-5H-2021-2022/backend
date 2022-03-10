package edu.tulliobuzzi.componenti;

import java.util.ArrayList;

public class Rotore extends ComponenteBase{
    private String identificatore;
    private int posizioneRotore;
    private int posizioneTacca;
    private int impostazioniAnello;
    private ArrayList<Integer> configurazioneInversa;

    public Rotore(String codifica, String identificatore, int posizioneRotore, int posizioneTacca, int impostazioniAnello) {
        super(codifica);
        configurazioneInversa = invertiConfigurazione(configurazione);
        this.identificatore = identificatore;
        this.posizioneRotore = posizioneRotore;
        this.posizioneTacca = posizioneTacca;
        this.impostazioniAnello = impostazioniAnello;
    }

    @Override
    public int getLettera(int indice) {
        return 0;
    }

    @Override
    ArrayList<Integer> convertiStringa(String codifica) {
        return null;
    }

    public String getIdentificatore() {
        return this.identificatore;
    }

    public int getPosizione() {
        return this.posizioneRotore;
    }

    private ArrayList<Integer> invertiConfigurazione(ArrayList<Integer> configurazione) {
        ArrayList<Integer> inverso = new ArrayList<Integer>();
        for (int i = 0; i < configurazione.size(); i++) {
            int forward = wiring[i];
            inverso[forward] = i;
        }
        return inverso;
    }

    private int cifrazione(int k, ArrayList<Integer> configurazione) {
        int shift = this.posizioneRotore - this.impostazioniAnello;
        //return (configurazione[(k + shift + 26) % 26] - shift + 26) % 26;
        return 0;
    }

    public int forward(int indice) {
        return cifrazione(c, this.configurazione);
    }

    public int backward(int c) {
        return cifrazione(c, this.rotorPosition, this.ringSetting, this.backwardWiring);
    }

    public boolean isAtNotch() {
        return this.notchPosition == this.rotorPosition;
    }

    public void turnover() {
        this.rotorPosition = (this.rotorPosition + 1) % 26;
    }

}
