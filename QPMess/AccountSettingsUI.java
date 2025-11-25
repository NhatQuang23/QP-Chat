import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.File;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import database.DatabaseConnection;
import Collections.User;
import Collections.Login;
import SessionManager.Session;

public class AccountSettingsUI extends Application {
    
    private User currentUser;
    private String currentPasswordFromDB;
    private TextField nameField;
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private TextField emailField;
    private TextField phoneField;
    private ImageView avatarView;
    private String selectedAvatarPath;
    
    @Override
    public void start(Stage primaryStage) {
        // Lấy thông tin user đang đăng nhập
        if (Session.isLoggedIn()) {
            currentUser = Session.getLoggedInUser();
            // Load password từ Login collection
            loadPasswordFromDatabase();
        } else {
            showError("Lỗi", "Bạn cần đăng nhập để xem thông tin tài khoản.");
            primaryStage.close();
            return;
        }
        
        primaryStage.setTitle("Quản lý thông tin tài khoản");
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-bg");
        root.setPadding(new Insets(30));
        
        // Header
        Label headerLabel = new Label("Thông tin tài khoản");
        headerLabel.getStyleClass().add("title-label");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        HBox header = new HBox(headerLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));
        
        // Avatar section
        VBox avatarSection = new VBox(10);
        avatarSection.setAlignment(Pos.CENTER);
        
        avatarView = new ImageView();
        avatarView.setFitWidth(120);
        avatarView.setFitHeight(120);
        avatarView.setPreserveRatio(true);
        avatarView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        
        // Load avatar hiện tại
        try {
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                avatarView.setImage(new Image(currentUser.getProfilePicture()));
                selectedAvatarPath = currentUser.getProfilePicture();
            } else {
                avatarView.setImage(new Image(getClass().getResourceAsStream("Images/Contact1.jpeg")));
            }
        } catch (Exception e) {
            avatarView.setImage(new Image(getClass().getResourceAsStream("Images/Contact1.jpeg")));
        }
        
        Button changeAvatarBtn = new Button("Thay đổi ảnh đại diện");
        changeAvatarBtn.getStyleClass().add("button-secondary");
        changeAvatarBtn.setOnAction(e -> chooseAvatar(primaryStage));
        
        avatarSection.getChildren().addAll(avatarView, changeAvatarBtn);
        
        // Form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(20, 50, 20, 50));
        
        // Tên người dùng
        Label nameLabel = new Label("Tên hiển thị:");
        nameLabel.getStyleClass().add("title-label");
        nameField = new TextField(currentUser.getName());
        nameField.getStyleClass().add("text-field");
        nameField.setPromptText("Nhập tên của bạn");
        
        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("title-label");
        emailField = new TextField(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        emailField.getStyleClass().add("text-field");
        emailField.setPromptText("Nhập email của bạn");
        
        // Số điện thoại
        Label phoneLabel = new Label("Số điện thoại:");
        phoneLabel.getStyleClass().add("title-label");
        phoneField = new TextField(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        phoneField.getStyleClass().add("text-field");
        phoneField.setPromptText("Nhập số điện thoại");
        
        // Mật khẩu hiện tại
        Label currentPasswordLabel = new Label("Mật khẩu hiện tại:");
        currentPasswordLabel.getStyleClass().add("title-label");
        currentPasswordField = new PasswordField();
        currentPasswordField.getStyleClass().add("text-field");
        currentPasswordField.setPromptText("Để trống nếu không đổi mật khẩu");
        
        // Mật khẩu mới
        Label newPasswordLabel = new Label("Mật khẩu mới:");
        newPasswordLabel.getStyleClass().add("title-label");
        newPasswordField = new PasswordField();
        newPasswordField.getStyleClass().add("text-field");
        newPasswordField.setPromptText("Nhập mật khẩu mới");
        
        // Xác nhận mật khẩu mới
        Label confirmPasswordLabel = new Label("Xác nhận mật khẩu:");
        confirmPasswordLabel.getStyleClass().add("title-label");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.getStyleClass().add("text-field");
        confirmPasswordField.setPromptText("Nhập lại mật khẩu mới");
        
        // Add to grid
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(phoneLabel, 0, 2);
        formGrid.add(phoneField, 1, 2);
        formGrid.add(new Separator(), 0, 3, 2, 1);
        formGrid.add(currentPasswordLabel, 0, 4);
        formGrid.add(currentPasswordField, 1, 4);
        formGrid.add(newPasswordLabel, 0, 5);
        formGrid.add(newPasswordField, 1, 5);
        formGrid.add(confirmPasswordLabel, 0, 6);
        formGrid.add(confirmPasswordField, 1, 6);
        
        // Make fields expand
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setMinWidth(250);
        formGrid.getColumnConstraints().addAll(col1, col2);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button saveButton = new Button("Lưu thay đổi");
        saveButton.getStyleClass().add("button");
        saveButton.setPrefWidth(150);
        saveButton.setOnAction(e -> saveChanges(primaryStage));
        
        Button cancelButton = new Button("Hủy");
        cancelButton.getStyleClass().add("button-secondary");
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        // Layout
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.getChildren().addAll(avatarSection, formGrid, buttonBox);
        
        root.setTop(header);
        root.setCenter(centerContent);
        
        Scene scene = new Scene(root, 700, 750);
        scene.getStylesheets().add(getClass().getResource("css/modern.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void chooseAvatar(Stage ownerStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(ownerStage);
        if (selectedFile != null) {
            try {
                selectedAvatarPath = selectedFile.toURI().toString();
                avatarView.setImage(new Image(selectedAvatarPath));
            } catch (Exception e) {
                showError("Lỗi", "Không thể tải ảnh: " + e.getMessage());
            }
        }
    }
    
    private void saveChanges(Stage stage) {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validate
        if (newName.isEmpty()) {
            showError("Lỗi", "Tên không được để trống!");
            return;
        }
        
        // Validate email format (optional)
        if (!newEmail.isEmpty() && !newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Lỗi", "Email không hợp lệ!");
            return;
        }
        
        // Nếu người dùng muốn đổi mật khẩu
        boolean changePassword = !currentPassword.isEmpty() || !newPassword.isEmpty();
        
        if (changePassword) {
            if (currentPassword.isEmpty()) {
                showError("Lỗi", "Vui lòng nhập mật khẩu hiện tại!");
                return;
            }
            
            if (newPassword.isEmpty()) {
                showError("Lỗi", "Vui lòng nhập mật khẩu mới!");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showError("Lỗi", "Mật khẩu xác nhận không khớp!");
                return;
            }
            
            if (newPassword.length() < 6) {
                showError("Lỗi", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                return;
            }
            
            // Verify current password
            if (currentPasswordFromDB != null && !currentPassword.equals(currentPasswordFromDB)) {
                showError("Lỗi", "Mật khẩu hiện tại không đúng!");
                return;
            }
        }
        
        // Update in database
        try {
            updateUserInDatabase(newName, newEmail, newPhone, changePassword ? newPassword : null, selectedAvatarPath);
            
            // Update session
            currentUser.setName(newName);
            currentUser.setEmail(newEmail);
            currentUser.setPhone(newPhone);
            if (changePassword) {
                currentUser.setPassword(newPassword);
                currentPasswordFromDB = newPassword;
            }
            if (selectedAvatarPath != null) {
                currentUser.setProfilePicture(selectedAvatarPath);
            }
            
            showSuccess("Thành công", "Thông tin tài khoản đã được cập nhật!");
            stage.close();
            
        } catch (Exception e) {
            showError("Lỗi", "Không thể cập nhật thông tin: " + e.getMessage());
        }
    }
    
    private void loadPasswordFromDatabase() {
        try {
            // Sử dụng DatabaseConnection để lấy database
            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
            MongoCollection<Document> loginCollection = database.getCollection("Login");
            
            Document query = new Document("user_id", currentUser.getUserId());
            Document loginDoc = loginCollection.find(query).first();
            
            if (loginDoc != null) {
                currentPasswordFromDB = loginDoc.getString("password");
            }
        } catch (Exception e) {
            showError("Lỗi", "Không thể tải mật khẩu: " + e.getMessage());
        }
    }
    
    private void updateUserInDatabase(String name, String email, String phone, String password, String avatarPath) {
        try {
            // Sử dụng DatabaseConnection để lấy database
            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
            MongoCollection<Document> userCollection = database.getCollection("User");
            
            Document query = new Document("user_id", currentUser.getUserId());
            Document updateDoc = new Document();
            
            updateDoc.append("name", name);
            if (!email.isEmpty()) {
                updateDoc.append("email", email);
            }
            if (!phone.isEmpty()) {
                updateDoc.append("phone", phone);
            }
            if (avatarPath != null) {
                updateDoc.append("profile_picture", avatarPath);
            }
            
            Document update = new Document("$set", updateDoc);
            userCollection.updateOne(query, update);
            
            // Update password in Login collection if changed
            if (password != null) {
                MongoCollection<Document> loginCollection = database.getCollection("Login");
                Document loginQuery = new Document("user_id", currentUser.getUserId());
                Document passwordUpdate = new Document("$set", new Document("password", password));
                loginCollection.updateOne(loginQuery, passwordUpdate);
            }
            
        } catch (Exception e) {
            showError("Lỗi", "Không thể cập nhật database: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

