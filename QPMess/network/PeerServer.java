package network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * Lightweight TCP server that accepts inbound peer messages and dispatches
 * them to the provided handler.
 */
public final class PeerServer implements Closeable {

    private static final int SO_TIMEOUT_MS = (int) Duration.ofSeconds(30).toMillis();

    private final int port;
    private final Consumer<PeerMessage> messageHandler;
    private final ExecutorService executor;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public PeerServer(int port, Consumer<PeerMessage> messageHandler) {
        this.port = port;
        this.messageHandler = Objects.requireNonNull(messageHandler, "messageHandler");
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("peer-server-" + port);
            thread.setDaemon(true);
            return thread;
        };
        this.executor = Executors.newCachedThreadPool(threadFactory);
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        executor.submit(this::acceptLoop);
    }

    private void acceptLoop() {
        try (ServerSocket server = new ServerSocket(port)) {
            this.serverSocket = server;
            server.setSoTimeout(SO_TIMEOUT_MS);
            while (running) {
                try {
                    Socket socket = server.accept();
                    executor.submit(() -> handleConnection(socket));
                } catch (IOException acceptException) {
                    if (!running) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                throw new RuntimeException("Unable to start peer server on port " + port, e);
            }
        }
    }

    private void handleConnection(Socket socket) {
        try (Socket clientSocket = socket;
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {
            String payload = reader.readLine();
            if (payload != null && !payload.isBlank()) {
                PeerMessage message = PeerMessage.fromWireFormat(payload.trim());
                messageHandler.accept(message);
            }
        } catch (IOException | IllegalArgumentException ignored) {
            // Ignore bad payloads or transient IO exceptions to keep the server alive.
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
        executor.shutdownNow();
    }
}

