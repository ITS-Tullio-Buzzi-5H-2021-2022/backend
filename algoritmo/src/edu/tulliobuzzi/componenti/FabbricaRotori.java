package edu.tulliobuzzi.componenti;

public class FabbricaRotori {
    public static Rotore crea(String nome, int posizioneRotore, int posizioneAnello) {
        switch (nome) {
            case "I":
                return new Rotore("I","EKMFLGDQVZNTOWYHXUSPAIBRCJ", posizioneRotore, 16, posizioneAnello);
            case "II":
                return new Rotore("II","AJDKSIRUXBLHWTMCQGZNPYFVOE", posizioneRotore, 4, posizioneAnello);
            case "III":
                return new Rotore("III","BDFHJLCPRTXVZNYEIWGAKMUSQO", posizioneRotore, 21, posizioneAnello);
            case "IV":
                return new Rotore("IV","ESOVPZJAYQUIRHXLNFTGKDCMWB", posizioneRotore, 9, posizioneAnello);
            case "V":
                return new Rotore("V","VZBRGITYUPSDNHLXAWMJQOFECK", posizioneRotore, 25, posizioneAnello);
            case "VI":
                return new RotoreAvanzato("VI","JPGVOUMFYQBENHZRDKASXLICTW", posizioneRotore, posizioneAnello);
            case "VII":
                return new RotoreAvanzato("VII","NZJHGRCXMYSWBOUFAIVLPEKQDT", posizioneRotore, posizioneAnello);
            case "VIII":
                return new RotoreAvanzato("VIII","FKQHTLXOCBJSPDZRAMEWNIUYGV", posizioneRotore, posizioneAnello);
            default:
                return new Rotore("Identità","ABCDEFGHIJKLMNOPQRSTUVWXYZ", posizioneRotore, 0, posizioneAnello);
        }
    }
}
