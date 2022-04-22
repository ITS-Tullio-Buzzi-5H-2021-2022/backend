package edu.tulliobuzzi.verticale;

public class Verticale {

    // "Main" della comunicazione verticale

    public Verticale() {
        // crea server con protocollo in base alla modalità Enc o Dec
        // se enc i dati vengono ricevuti dall'alto e vanno verso il basso (main)
        // se dec i dati vengono ricevuti e vanno verso l'alto (frontend)
    }

    // Trasmissione tipo:
    // Enc:
    // {"type": "rotorFill", "rotors": ["I", "II", "III"]}
    // TODO: risposta rotorFill?
    //
    // TODO: -> {"type": "charToEncode", "data": "A" }
    // TODO: -> {"type": "encodingResult", "char":"A", "rotors": [true, false, true] }
    // {"type": "backspacePressed", "data":"Backspace"} TODO: -> {"type": "backspacePressed"}
    // {"type": "enterPressed","data":"Enter"} / TODO: -> {"type": "backspacePressed"}
    // Dec:
    // TODO: invio iniziale delle informazioni sui rotori?
    //
    // {"type": "encodedText", "data": "ILSASSOFRASSO" }
    // {"type": "textToDecode", "data": "ILSASSOFRASSO", "rotors": ["I", "II", "III"]}
    // {"type": "decodedText", "data": "SASSISTAPAZZO" }

}
