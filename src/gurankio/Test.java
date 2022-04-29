package gurankio;

import gurankio.sockets.Server;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        new Server("localhost", 8080, Echo::new).run();
    }

}
