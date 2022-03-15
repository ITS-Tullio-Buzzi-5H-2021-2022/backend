package edu.tulliobuzzi.componenti;

public class FabbricaRiflettori {
    public static Riflettore crea(String nome) {
        switch (nome) {
            case "B":
                return new Riflettore("YRUHQSLDPXNGOKMIEBFZCWVJAT");
            case "C":
                return new Riflettore("FVPJIAOYEDRZXWGCTKUQSBNMHL");
            default:
                return new Riflettore("ZYXWVUTSRQPONMLKJIHGFEDCBA");
        }
    }
}
