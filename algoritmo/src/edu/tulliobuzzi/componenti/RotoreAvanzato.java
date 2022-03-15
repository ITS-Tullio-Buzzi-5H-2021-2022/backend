package edu.tulliobuzzi.componenti;

public class RotoreAvanzato extends Rotore{

    public RotoreAvanzato(String identificatore, String codifica, int posizioneRotore, int impostazioniAnello) {
        super(identificatore, codifica, posizioneRotore, 12, impostazioniAnello);
    }

    @Override
    public boolean isAtTacca() {
        if(this.posizioneRotore == 12 || this.posizioneRotore == 25)
            return true;
        return false;
    }
}
