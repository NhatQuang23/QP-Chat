package network;

/**
 * Simple value object holding the host and port for a peer.
 */
public final class PeerEndpoint {
    private final String host;
    private final int port;

    public PeerEndpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

