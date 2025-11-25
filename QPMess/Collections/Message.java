package Collections;
import java.time.LocalDateTime;

public class Message {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String recipientType;
    private String content;
    private String timestamp;
    private String messageType;  // "text", "file", "image"
    private String fileName;
    private String filePath;
    private long fileSize;

    public Message(int messageId, int senderId, int receiverId, String recipientType ,String content, String timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.recipientType = recipientType;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = "text"; // default
    }
    
    // Constructor for file messages
    public Message(int messageId, int senderId, int receiverId, String recipientType, String content, 
                   String timestamp, String messageType, String fileName, String filePath, long fileSize) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.recipientType = recipientType;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
}
