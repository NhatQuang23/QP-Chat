package network;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * Represents a TCP payload exchanged between peers. The body is Base64 encoded
 * so we can safely stream it over a single-line protocol.
 */
public final class PeerMessage {

    private static final String SEPARATOR = "|";

    private final int senderId;
    private final int recipientId;
    private final String body;
    private final Instant timestamp;

    public PeerMessage(int senderId, int recipientId, String body, Instant timestamp) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.body = body;
        this.timestamp = timestamp;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public String getBody() {
        return body;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String toWireFormat() {
        String encodedBody = Base64.getEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8));
        return senderId + SEPARATOR + recipientId + SEPARATOR + timestamp.toString() + SEPARATOR + encodedBody;
    }

    public static PeerMessage fromWireFormat(String payload) {
        String[] parts = payload.split("\\|", 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid peer payload: " + payload);
        }
        int parsedSender = Integer.parseInt(parts[0]);
        int parsedRecipient = Integer.parseInt(parts[1]);
        Instant parsedTimestamp = Instant.parse(parts[2]);
        String decodedBody = new String(Base64.getDecoder().decode(parts[3]), StandardCharsets.UTF_8);
        return new PeerMessage(parsedSender, parsedRecipient, decodedBody, parsedTimestamp);
    }
}

