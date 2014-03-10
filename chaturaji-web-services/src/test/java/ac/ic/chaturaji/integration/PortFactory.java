package ac.ic.chaturaji.integration;

import java.io.IOException;
import java.net.ServerSocket;

public class PortFactory {
    public static int findFreePort() {
        int port;
        try {
            ServerSocket server = new ServerSocket(0);
            port = server.getLocalPort();
            server.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Exception while trying to find a free port", ioe);
        }
        return port;
    }
}