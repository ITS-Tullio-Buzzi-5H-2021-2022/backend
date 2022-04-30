package edu.tulliobuzzi;

import com.google.gson.Gson;
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
import java.util.List;

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

    public static final Gson GSON = new Gson();
    public static Orizzontale ORIZZONTALE;
    public static Verticale VERTICALE;

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) Configuration.LOCAL_HOST = args[1];
        if (args.length >= 3) Configuration.PUBLIC_HOST = args[2];
        if (args.length >= 4) Configuration.HORIZON_HOST = args[3];

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
            if (ORIZZONTALE != null) ORIZZONTALE.close();
            if (VERTICALE != null) VERTICALE.close();
        }
    }

    public static Enigma configurazioneStandard() {
        return new Enigma(
                FabbricaRiflettori.B.build(),
                List.of(
                        FabbricaRotori.I.build(0, 0),
                        FabbricaRotori.I.build(0, 0),
                        FabbricaRotori.I.build(0, 0)
                ),
                new PannelloControllo("")
        );
    }
}
