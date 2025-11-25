package network;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * High-level facade that manages the local peer server and exposes helpers for
 * sending messages to other peers.
 */
public final class TcpPeerService implements Closeable {

    private static final int BASE_PORT = 6000;

    private final int localUserId;
    private final Consumer<PeerMessage> inboundHandler;
    private PeerServer peerServer;
    private int listeningPort;

    public TcpPeerService(int localUserId, Consumer<PeerMessage> inboundHandler) {
        this.localUserId = localUserId;
        this.inboundHandler = Objects.requireNonNull(inboundHandler, "inboundHandler");
    }

    public void start() {
        listeningPort = derivePortForUser(localUserId);
        peerServer = new PeerServer(listeningPort, inboundHandler);
        peerServer.start();
    }

    public void send(String host, int port, int recipientId, String body) throws IOException {
        PeerMessage message = new PeerMessage(localUserId, recipientId, body, Instant.now());
        PeerClient.send(host, port, message);
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public static int derivePortForUser(int userId) {
        return BASE_PORT + userId;
    }

    @Override
    public void close() throws IOException {
        if (peerServer != null) {
            peerServer.close();
        }
    }
}

