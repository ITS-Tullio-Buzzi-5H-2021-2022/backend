package edu.tulliobuzzi;

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
    // {"type": "rotorFill", "rotors": ["I", "II", "III"]}
    // risposta rotorFill?
    //
    // {"type": "charToEncode", "data": "A" }
    // {"type": "encodingResult", "data":"B", "rotors": [true, false, true] }
    // {"type": "backspacePressed"}
    // {"type": "enterPressed"}
    //
    // Dec:
    // invio iniziale delle informazioni sui rotori?
    //
    // {"type": "encodedText", "data": "ILSASSOFRASSO" }
    // {"type": "textToDecode", "data": "ILSASSOFRASSO", "rotors": ["I", "II", "III"]}
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
}
