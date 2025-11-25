package network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Thin wrapper over a TCP socket for sending a message to a remote peer.
 */
public final class PeerClient {

    private static final int CONNECT_TIMEOUT_MS = (int) Duration.ofSeconds(3).toMillis();

    private PeerClient() {
    }

    public static void send(String host, int port, PeerMessage message) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write(message.toWireFormat());
                writer.newLine();
                writer.flush();
            }
        }
    }
}

