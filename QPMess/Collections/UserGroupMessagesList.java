package Collections;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.scene.control.ListView;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserGroupMessagesList {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static List<Message> getGroupMessages(Group selectedGroup) {
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("ChatApp");

        List<Message> messages = new ArrayList<>();

        if (selectedGroup != null && database != null) {
            MongoCollection<Document> messagesCollection = database.getCollection("Message");
            FindIterable<Document> groupMessages = messagesCollection.find(
                    new Document("recipient_id", selectedGroup.getGroupId())
                            .append("recipient_type", "group"));

            for (Document document : groupMessages) {
                int messageId = document.getInteger("message_id");
                int senderId = document.getInteger("sender_id");
                int recipientId = document.getInteger("recipient_id");
                String content = document.getString("content");
                String recipientType = document.getString("recipient_type");
                String timestamp = document.getString("timestamp");

                Message message = new Message(messageId, senderId, recipientId, recipientType, content, timestamp);
                
                // Load file info if exists
                String messageType = document.getString("message_type");
                if (messageType != null) {
                    message.setMessageType(messageType);
                    message.setFileName(document.getString("file_name"));
                    message.setFilePath(document.getString("file_path"));
                    Long fileSize = document.getLong("file_size");
                    if (fileSize != null) message.setFileSize(fileSize);
                }
                
                messages.add(message);
            }
        }

        return messages;
    }
}
