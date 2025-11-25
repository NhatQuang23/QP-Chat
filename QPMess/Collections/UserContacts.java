package Collections;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import database.DatabaseConnection;
import SessionManager.Session;
import javafx.scene.control.ListView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserContacts {
    

    public static ListView<User> getContacts(User loggedInUser) {
        
        // Sử dụng DatabaseConnection để lấy database
        MongoDatabase database = DatabaseConnection.getInstance().getDatabase();

        ListView<User> contacts = new ListView<>();

        if (loggedInUser != null && database != null) {
            MongoCollection<Document> contactsCollection = database.getCollection("User_Contacts");
            FindIterable<Document> userContacts = contactsCollection.find(new Document("user_id", loggedInUser.getUserId()));

            for (Document document : userContacts) {
                int contactId = document.getInteger("contact_id");
                // Fetch the contact details from the database
                MongoCollection<Document> contactedCollection = database.getCollection("User");
                FindIterable<Document> contacted = contactedCollection.find(new Document("user_id", contactId));
                for (Document document1 : contacted) {
                    String name = document1.getString("name");
                    String profilePicture = document1.getString("profile_picture");
                    User contact = new User(contactId, name, profilePicture);
                    contacts.getItems().add(contact);
                }
            }
        }

        return contacts;
    }
    


    // public static void main(String[] args) {
        
    //     User loggedInUser = new User(1, "John Doe", "https://www.google.com");
    //     ListView<User> contactedUsers=  getContacts(loggedInUser);

    //     contactedUsers.getItems().forEach((user) -> {
    //         System.out.println(user.getName());
    //     });
    // }
}
