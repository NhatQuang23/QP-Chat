


import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import database.DatabaseConnection;
import Collections.User;
import SessionManager.Session;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Home extends Application {

    public User isAuthenticated = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("QPMess - Đăng nhập");

        // Main container
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.getStyleClass().add("main-bg");

        // Title
        Label titleLabel = new Label("QPMess");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setStyle("-fx-text-fill: #00a884;");

        Label subtitleLabel = new Label("Ứng dụng Chat P2P");
        subtitleLabel.getStyleClass().add("muted-label");
        subtitleLabel.setStyle("-fx-font-size: 14px;");

        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Login form container
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30, 40, 30, 40));
        formContainer.setMaxWidth(400);
        formContainer.setStyle("-fx-background-color: #202c33; -fx-background-radius: 12px;");

        // Username field
        Label usernameLabel = new Label("Tên đăng nhập:");
        usernameLabel.getStyleClass().add("label");
        usernameLabel.setStyle("-fx-font-size: 14px;");
        
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Nhập tên đăng nhập");
        usernameInput.getStyleClass().add("text-field");
        usernameInput.setPrefWidth(320);

        // Password field
        Label passwordLabel = new Label("Mật khẩu:");
        passwordLabel.getStyleClass().add("label");
        passwordLabel.setStyle("-fx-font-size: 14px;");
        
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Nhập mật khẩu");
        passwordInput.getStyleClass().add("text-field");
        passwordInput.setPrefWidth(320);

        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Đăng nhập");
        loginButton.getStyleClass().add("button");
        loginButton.setPrefWidth(150);
        loginButton.setPrefHeight(40);

        Button registerButton = new Button("Đăng ký");
        registerButton.getStyleClass().add("button-secondary");
        registerButton.setPrefWidth(150);
        registerButton.setPrefHeight(40);

        buttonBox.getChildren().addAll(registerButton, loginButton);

        // Add event handlers
        loginButton.setOnAction(e -> {
            String username = usernameInput.getText().trim();
            String password = passwordInput.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Thiếu thông tin", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
                return;
            }

            isAuthenticated = authenticateUser(username, password);
            if (isAuthenticated != null) {
                ChatApp chatApp = new ChatApp();
                chatApp.start(primaryStage);
            } else {
                showError("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng.");
            }
        });

        registerButton.setOnAction(e -> {
            UserRegistrationForm register = new UserRegistrationForm();
            register.start(primaryStage);
        });

        // Enter key to login
        usernameInput.setOnAction(e -> passwordInput.requestFocus());
        passwordInput.setOnAction(e -> loginButton.fire());

        // Add components to form
        formContainer.getChildren().addAll(
            usernameLabel, usernameInput,
            passwordLabel, passwordInput,
            buttonBox
        );

        // Add to main container
        mainContainer.getChildren().addAll(titleBox, formContainer);

        Scene scene = new Scene(mainContainer, 500, 600);
        scene.getStylesheets().add(getClass().getResource("css/modern.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        String css = getClass().getResource("css/modern.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);
        alert.showAndWait();
    }

    private User authenticateUser(String username, String password) {
        // Perform authentication logic using the User collection in MongoDB
        // Replace this code with your MongoDB authentication logic
        // Query the User collection for the provided username and password
        // Return true if a matching user document is found, false otherwise
        // You can use a MongoDB driver or an ORM library like Spring Data MongoDB

        // Sử dụng DatabaseConnection để lấy database
        MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
        MongoCollection<Document> userCollection = database.getCollection("Login");

        // Query the User collection for the provided username and password
        Document query = new Document("username", username).append("password", password);
        FindIterable<Document> result = userCollection.find(query);

        // Check if a matching user document is found
        Document userDocument = result.first();
        if (userDocument != null) {
            // Retrieve the user information from the document
            int userId = userDocument.getInteger("user_id");

            // our login table gives us the user_id, so we can use that to get the user's name
            MongoCollection<Document> userCollection2 = database.getCollection("User");
            Document query2 = new Document("user_id", userId);
            FindIterable<Document> result2 = userCollection2.find(query2);
            Document userDocument2 = result2.first();
            String name = userDocument2.getString("name");
            String profilePicture = userDocument2.getString("profile_picture");
            String bio = userDocument2.getString("bio");
            String preferredLanguage = userDocument2.getString("preferred_language");
            String createdAt = userDocument2.getString("created_at");

            // Create a User object
            User loggedInUser = new User(userId, name, profilePicture, bio, preferredLanguage, createdAt);
            // lets handle the session here
            Session.setLoggedInUser(loggedInUser);
           

            // Return the logged-in user object
            return loggedInUser;
        } else {
            // Authentication failed, return null
            return null;
        }

    }


    public static void main(String[] args) {
        launch(args);
    }

}
