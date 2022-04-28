package edu.tulliobuzzi;

import edu.tulliobuzzi.algoritmo.Enigma;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRiflettori;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRotori;
import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;
import edu.tulliobuzzi.orizzontale.Orizzontale;
import edu.tulliobuzzi.orizzontale.OrizzontaleCodifica;
import edu.tulliobuzzi.orizzontale.OrizzontaleDecodifica;
import edu.tulliobuzzi.verticale.Verticale;
import edu.tulliobuzzi.verticale.VerticaleCodifica;
import edu.tulliobuzzi.verticale.VerticaleDecodifica;

import java.io.IOException;

public class Main {

    // Trasmissione tipo:
    // Enc:
    // {"type": "charToEncode", "data": "A" }
    // {"type": "encodingResult", "data":"B", "rotors": [true, false, true] }
    // {"type": "backspacePressed"}
    // {"type": "enterPressed"}
    //
    // Dec:
    // {"type": "encodedText", "data": "ILSASSOFRASSO" }
    // {"type": "textToDecode", "data": "ILSASSOFRASSO", "rotors": [{"I"}, {"II"}, {"III"}]}
    // {"type": "decodedText", "data": "SASSISTAPAZZO" }

    public static Orizzontale ORIZZONTALE;
    public static Verticale VERTICALE;

    public static void main(String[] args) throws IOException {
        try {
            switch (args[0]) {
                case "encode" -> {
                    ORIZZONTALE = new OrizzontaleCodifica();
                    VERTICALE = new VerticaleCodifica();
                }
                case "decode" -> {
                    ORIZZONTALE = new OrizzontaleDecodifica();
                    VERTICALE = new VerticaleDecodifica();
                }
                default -> {
                    System.err.println("Invalid parameter.");
                    System.exit(-1);
                }
            }

            VERTICALE.run();
        } finally {
            ORIZZONTALE.close();
            VERTICALE.close();
        }
    }

    public static Enigma configurazioneStandard() {
        return new Enigma(
                FabbricaRiflettori.B.build(),
                new Rotore[]{
                        FabbricaRotori.I.build(0, 0),
                        FabbricaRotori.II.build(0, 0),
                        FabbricaRotori.III.build(0, 0)
                },
                new PannelloControllo("")
        );
    }
}
