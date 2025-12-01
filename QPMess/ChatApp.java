
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import database.DatabaseConnection;
import Collections.Group;
import Collections.MessageList;
import Collections.User;
import Collections.UserContacts;
import Collections.UserGroupMessagesList;
import Collections.UserGroups;
import Collections.Message;
import SessionManager.Session;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import network.PeerEndpoint;
import network.PeerMessage;
import network.TcpPeerService;

public class ChatApp extends Application {

    private User loggedInUser;
    private TextArea messageArea;
    private TextField messageInput;
    private VBox chatBox;
    private ScrollPane chatScrollPane;
    ListView<User> contactsList;
    private final ObjectProperty<User> selectedUserProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Message> selectedMessageProperty = new SimpleObjectProperty<>();
    private TcpPeerService tcpPeerService;
    private final Map<Integer, PeerEndpoint> peerDirectory = new ConcurrentHashMap<>();
    private TextField peerHostField;
    private TextField peerPortField;
    private Label peerStatusLabel;
    private Label messageCount;
    private TextField searchField;
    private Label chatHeaderLabel;
    private HBox chatHeader;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Xây dựng ứng dụng Chat QPMess dựa trên giao thức TCP và mô hình Peer-to-Peer (P2P)");

        if(Session.isLoggedIn()) {
            
           loggedInUser = Session.getLoggedInUser();
            System.out.println(loggedInUser.getName());
            initializePeerService();
        } 




        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-bg");

        // Create a VBox for the side panel (contacts)
        BorderPane sidePanel = new BorderPane();
        
        sidePanel.setPrefWidth(350);
        sidePanel.getStyleClass().add("side-panel");
        
        // Search box for contacts
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm liên hệ...");
        searchField.getStyleClass().add("search-box");
        searchField.setPrefWidth(300);
        
        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterContacts(newVal);
        });

        // Create a ListView for displaying contacts
       
       // lets make a new vertical panel that will have some UI buttons such as settings, story, etc,
       // it should be on the left most side and vertical

        VBox leftPanel = new VBox();
        leftPanel.setPrefWidth(8);
        leftPanel.setPadding(new Insets(2));
        leftPanel.setStyle("-fx-background-color: #2C2F33");
        leftPanel.setSpacing(10);

        // add some ui buttons to the left panel
        Button settingsButton = new Button("");
        settingsButton.setStyle("-fx-background-color: #2C2F33; -fx-text-fill: white;");
        Image settingsImage = new Image(getClass().getResourceAsStream("Images\\settings2.png"));
        ImageView settingsImageView = new ImageView(settingsImage);
        settingsImageView.setFitWidth(24);
        settingsImageView.setFitHeight(24);
        settingsImageView.setPreserveRatio(true);
        settingsImageView.setOpacity(1);

        settingsButton.setGraphic(settingsImageView);

        settingsButton.setOnAction(e -> {
            PrivacySettingsUI privacySettingsGUI = new PrivacySettingsUI();
            privacySettingsGUI.start(new Stage());
        });
        


        Button storyButton = new Button();
        storyButton.setStyle("-fx-background-color: #2C2F33; -fx-text-fill: white;");
        Image storyImage = new Image(getClass().getResourceAsStream("Images\\story.png"));
        ImageView storyImageView = new ImageView(storyImage);
        storyImageView.setFitWidth(24);
        storyImageView.setFitHeight(24);
        storyImageView.setPreserveRatio(true);
        storyImageView.setOpacity(1);

        storyButton.setGraphic(storyImageView);


        Button subscriptionButton = new Button();
        subscriptionButton.setStyle("-fx-background-color: #2C2F33; -fx-text-fill: white;");
        Image subscriptionImage = new Image(getClass().getResourceAsStream("Images\\pro.png"));
        ImageView subscriptionImageView = new ImageView(subscriptionImage);
        subscriptionImageView.setFitWidth(24);
        subscriptionImageView.setFitHeight(24);
        subscriptionImageView.setPreserveRatio(true);
        subscriptionImageView.setOpacity(1);

        subscriptionButton.setGraphic(subscriptionImageView);

        Button linkedDevicesButton = new Button();
        linkedDevicesButton.setStyle("-fx-background-color: #2C2F33; -fx-text-fill: white;");
        Image linkedDevicesImage = new Image(getClass().getResourceAsStream("Images\\linkeddevices.png"));
        ImageView linkedDevicesImageView = new ImageView(linkedDevicesImage);
        linkedDevicesImageView.setFitWidth(24);
        linkedDevicesImageView.setFitHeight(24);
        linkedDevicesImageView.setPreserveRatio(true);
        linkedDevicesImageView.setOpacity(1);

        linkedDevicesButton.setGraphic(linkedDevicesImageView);

        // Button quản lý tài khoản
        Button accountButton = new Button();
        accountButton.setStyle("-fx-background-color: #2C2F33; -fx-text-fill: white;");
        accountButton.setTooltip(new Tooltip("Quản lý tài khoản"));
        Image accountImage = new Image(getClass().getResourceAsStream("Images\\settings.png"));
        ImageView accountImageView = new ImageView(accountImage);
        accountImageView.setFitWidth(24);
        accountImageView.setFitHeight(24);
        accountImageView.setPreserveRatio(true);
        accountImageView.setOpacity(1);

        accountButton.setGraphic(accountImageView);
        accountButton.setOnAction(e -> {
            AccountSettingsUI accountSettingsUI = new AccountSettingsUI();
            accountSettingsUI.start(new Stage());
        });


        Button reportedUsersButton = new Button();
        reportedUsersButton.setStyle("-fx-background-color: #2C2F33; -fx-text-fill: white;");
        Image reportedUsersImage = new Image(getClass().getResourceAsStream("Images\\rep.png"));
        ImageView reportedUsersImageView = new ImageView(reportedUsersImage);
        reportedUsersImageView.setFitWidth(24);
        reportedUsersImageView.setFitHeight(24);
        reportedUsersImageView.setPreserveRatio(true);
        reportedUsersImageView.setOpacity(1);

        reportedUsersButton.setGraphic(reportedUsersImageView);
        




        VBox.setVgrow(storyButton, Priority.NEVER);
        VBox.setVgrow(settingsImageView, Priority.ALWAYS);

        leftPanel.setAlignment(Pos.BOTTOM_CENTER);
        leftPanel.setSpacing(10);
        leftPanel.getChildren().addAll(accountButton, reportedUsersButton ,linkedDevicesButton ,subscriptionButton ,storyButton, settingsButton);

        contactsList = UserContacts.getContacts(loggedInUser);
        contactsList.setPrefHeight(500);
        contactsList.getStyleClass().add("contacts-list");
        
        // Improved contact list cell factory with better UI
        contactsList.setCellFactory(lv -> new ListCell<User>() {
            private final HBox hbox = new HBox(12);
            private final ImageView avatarView = new ImageView();
            private final VBox vbox = new VBox(4);
            private final Label nameLabel = new Label();
            private final Label statusLabel = new Label();
            private final Circle statusCircle = new Circle(5);
            
            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPadding(new Insets(8, 12, 8, 12));
                
                avatarView.setFitWidth(50);
                avatarView.setFitHeight(50);
                avatarView.setPreserveRatio(true);
                try {
                    avatarView.setImage(new Image(getClass().getResourceAsStream("Images/Contact1.jpeg")));
                } catch (Exception e) {
                    // Use default if image not found
                }
                
                nameLabel.getStyleClass().add("title-label");
                nameLabel.setStyle("-fx-font-size: 15px;");
                
                statusLabel.getStyleClass().add("muted-label");
                statusLabel.setStyle("-fx-font-size: 12px;");
                statusLabel.setText("Offline");
                
                statusCircle.setFill(Color.web("#8696a0"));
                statusCircle.setStroke(Color.web("#111b21"));
                statusCircle.setStrokeWidth(2);
                
                HBox statusBox = new HBox(6, statusCircle, statusLabel);
                statusBox.setAlignment(Pos.CENTER_LEFT);
                
                vbox.getChildren().addAll(nameLabel, statusBox);
                HBox.setHgrow(vbox, Priority.ALWAYS);
                
                hbox.getChildren().addAll(avatarView, vbox);
            }
            
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(user.getName());
                    try {
                        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                            avatarView.setImage(new Image(user.getProfilePicture()));
                        }
                    } catch (Exception e) {
                        // Keep default image
                    }
                    setGraphic(hbox);
                }
            }
        });
        
        final VBox sidePanelContent = new VBox(8);
        sidePanelContent.setPadding(new Insets(12));
        sidePanelContent.getChildren().addAll(searchField, contactsList);
        VBox.setVgrow(contactsList, Priority.ALWAYS);
        
        sidePanel.setLeft(leftPanel);
        sidePanel.setCenter(sidePanelContent);


        // Create a ScrollPane for the chat area with modern styling
        chatScrollPane = new ScrollPane();
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(true);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.getStyleClass().add("chat-scroll");
        
        // Create a VBox for the chat area
        chatBox = new VBox(12);
        chatBox.setPadding(new Insets(20));
        chatBox.getStyleClass().add("chat-bg");
        chatBox.setAlignment(Pos.TOP_LEFT);
        
        chatScrollPane.setContent(chatBox);

        // Create chat header
        chatHeader = new HBox(12);
        chatHeader.setPadding(new Insets(16, 20, 16, 20));
        chatHeader.getStyleClass().add("top-bar");
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        
        chatHeaderLabel = new Label("Chọn một cuộc trò chuyện");
        chatHeaderLabel.getStyleClass().add("title-label");
        chatHeaderLabel.setStyle("-fx-font-size: 18px;");
        Button addMemberButton = new Button("+");
        addMemberButton.getStyleClass().add("button-secondary");
        addMemberButton.setPrefWidth(32);
        addMemberButton.setPrefHeight(32);
        addMemberButton.setVisible(false);

        HBox.setHgrow(chatHeaderLabel, Priority.ALWAYS);
        chatHeader.getChildren().addAll(chatHeaderLabel, addMemberButton);
        
        // Create a HBox for the top bar (chat types)
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(12, 16, 12, 16));
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Create ToggleButtons for chat types with modern styling
        ToggleButton contactsButton = new ToggleButton("Liên hệ");
        ToggleButton groupsButton = new ToggleButton("Nhóm");
        ToggleButton communitiesButton = new ToggleButton("Cộng đồng");
        contactsButton.getStyleClass().add("toggle-button");
        groupsButton.getStyleClass().add("toggle-button");
        communitiesButton.getStyleClass().add("toggle-button");
        contactsButton.setSelected(true);

        Button newContactButton = new Button("+");
        newContactButton.getStyleClass().add("button");
        newContactButton.setPrefWidth(40);
        newContactButton.setPrefHeight(40);
        newContactButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");


        // User info với avatar
        HBox userInfoBox = new HBox(10);
        userInfoBox.setAlignment(Pos.CENTER_RIGHT);
        userInfoBox.getStyleClass().add("user-info-box");
        userInfoBox.setCursor(javafx.scene.Cursor.HAND);
        userInfoBox.setOnMouseClicked(e -> {
            AccountSettingsUI accountSettingsUI = new AccountSettingsUI();
            accountSettingsUI.start(new Stage());
        });
        
        ImageView userAvatar = new ImageView();
        userAvatar.setFitWidth(40);
        userAvatar.setFitHeight(40);
        userAvatar.setPreserveRatio(true);
        userAvatar.getStyleClass().add("avatar-round");
        try {
            if (loggedInUser.getProfilePicture() != null && !loggedInUser.getProfilePicture().isEmpty()) {
                userAvatar.setImage(new Image(loggedInUser.getProfilePicture()));
            } else {
                userAvatar.setImage(new Image(getClass().getResourceAsStream("Images/Contact1.jpeg")));
            }
        } catch (Exception e) {
            userAvatar.setImage(new Image(getClass().getResourceAsStream("Images/Contact1.jpeg")));
        }
        
        VBox userTextBox = new VBox(2);
        userTextBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(loggedInUser.getName());
        nameLabel.getStyleClass().add("title-label");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label("● Online");
        statusLabel.getStyleClass().add("muted-label");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #00a884;");
        
        userTextBox.getChildren().addAll(nameLabel, statusLabel);
        userInfoBox.getChildren().addAll(userAvatar, userTextBox);
        
        // Button đăng xuất
        Button logoutButton = new Button("Đăng xuất");
        logoutButton.getStyleClass().add("button-secondary");
        logoutButton.setPrefWidth(100);
        logoutButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận đăng xuất");
            confirmAlert.setHeaderText("Bạn có chắc muốn đăng xuất?");
            confirmAlert.setContentText("Bạn sẽ cần đăng nhập lại để sử dụng ứng dụng.");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logout(primaryStage);
            }
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        messageCount = new Label("Tin nhắn: " + contactsList.getItems().size());
        messageCount.getStyleClass().add("muted-label");



        // Add the ToggleButtons to the top bar
        topBar.getChildren().addAll(contactsButton, groupsButton, communitiesButton, newContactButton, messageCount, spacer, logoutButton, userInfoBox);
        topBar.setSpacing(10);

        peerHostField = new TextField("127.0.0.1");
        peerHostField.setPromptText("Địa chỉ IP");
        peerHostField.setPrefWidth(140);
        peerPortField = new TextField();
        peerPortField.setPromptText("Cổng");
        peerPortField.setPrefWidth(90);

        Button rememberPeerButton = new Button("Lưu Peer");
        rememberPeerButton.getStyleClass().add("button-secondary");
        rememberPeerButton.setOnAction(e -> rememberPeerForSelectedContact());

        peerStatusLabel = new Label();
        peerStatusLabel.getStyleClass().add("connection-status-connected");
        updatePeerStatusLabel();

        Label peerInfoLabel = new Label("TCP P2P:");
        peerInfoLabel.getStyleClass().add("peer-config-label");

        HBox peerConfigBar = new HBox(10);
        peerConfigBar.getStyleClass().add("peer-config-bar");
        peerConfigBar.setAlignment(Pos.CENTER_LEFT);
        peerConfigBar.getChildren().addAll(peerInfoLabel, peerHostField, peerPortField, rememberPeerButton, peerStatusLabel);

        contactsButton.setOnAction(event -> {
            if(contactsButton.isSelected()){
                groupsButton.setSelected(false);
                communitiesButton.setSelected(false);
                sidePanelContent.getChildren().setAll(searchField, contactsList);
                sidePanelContent.setPadding(new Insets(12));
                VBox.setVgrow(contactsList, Priority.ALWAYS);
                sidePanel.setCenter(sidePanelContent);
            }
        });

        contactsList.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(user.getName());
                    ImageView imageView = new ImageView(new Image(user.getProfilePicture()));
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    setGraphic(imageView);
                    setStyle("-fx-background-color: #f0f2f5; -fx-padding: 5;");
                }
                setStyle("-fx-text-fill: white;");
            }
        });

        contactsList.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                selectedUserProperty.set(newUser);
                populatePeerFields(newUser);
                refreshContactConversation(newUser);
                // Update chat header
                chatHeaderLabel.setText(newUser.getName());
            }
        });

        ObjectProperty<Group> selectedGroupProperty = new SimpleObjectProperty<>();
        ListView<Group> groupsList = UserGroups.getGroups(loggedInUser);
        System.out.println(groupsList.getItems().size());

        // actionListener for newContactButton: tạo liên hệ hoặc nhóm tùy tab đang chọn
        newContactButton.setOnAction(e -> {
            if (contactsButton.isSelected()) {
                NewContactDialog.display();
                // reload lại danh sách liên hệ sau khi thêm
                contactsList.setItems(UserContacts.getContacts(loggedInUser).getItems());
            } else if (groupsButton.isSelected()) {
                NewGroupDialog.display();
                // reload lại danh sách nhóm sau khi tạo
                ListView<Group> updatedGroups = UserGroups.getGroups(loggedInUser);
                groupsList.setItems(updatedGroups.getItems());
            }
        });

        groupsButton.setOnAction(event -> {
            if (groupsButton.isSelected()) {
                contactsButton.setSelected(false);
                communitiesButton.setSelected(false);
                
                groupsList.setCellFactory(lv -> new ListCell<Group>() {
                    private final HBox hbox = new HBox(12);
                    private final ImageView avatarView = new ImageView();
                    private final Label nameLabel = new Label();
                    
                    {
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setPadding(new Insets(8, 12, 8, 12));
                        
                        avatarView.setFitWidth(50);
                        avatarView.setFitHeight(50);
                        avatarView.setPreserveRatio(true);
                        
                        nameLabel.getStyleClass().add("title-label");
                        nameLabel.setStyle("-fx-font-size: 15px;");
                        
                        hbox.getChildren().addAll(avatarView, nameLabel);
                    }
                    
                    @Override
                    protected void updateItem(Group group, boolean empty) {
                        super.updateItem(group, empty);
                        if (empty || group == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            nameLabel.setText(group.getGroupName());
                            try {
                                if (group.getProfile_picture() != null && !group.getProfile_picture().isEmpty()) {
                                    avatarView.setImage(new Image(group.getProfile_picture()));
                                }
                            } catch (Exception e) {
                                // Keep default
                            }
                            setGraphic(hbox);
                        }
                    }
                });
                
                sidePanelContent.getChildren().setAll(searchField, groupsList);
                sidePanelContent.setPadding(new Insets(12));
                VBox.setVgrow(groupsList, Priority.ALWAYS);
                sidePanel.setCenter(sidePanelContent);
            }
        });

        groupsList.setOnMouseClicked(event -> {
            selectedUserProperty.set(null);
            // Handle group selection
            Group selectedGroup = groupsList.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                selectedGroupProperty.set(selectedGroup);
                chatHeaderLabel.setText(selectedGroup.getGroupName());
                addMemberButton.setVisible(true);
        
                List<Message> messages = UserGroupMessagesList.getGroupMessages(selectedGroupProperty.get());
                
                messageCount.setText("Tin nhắn: " + messages.size());
                messages.sort(Comparator.comparing(Message::getMessageId));

                chatBox.getChildren().clear();
                for (Message message : messages) {
                    addMessageLabel(message);
                }
                Platform.runLater(() -> {
                    chatScrollPane.setVvalue(1.0);
                });
            }
        });

        // Nút "+" trong header để thêm thành viên vào nhóm hiện tại
        addMemberButton.setOnAction(e -> {
            Group g = selectedGroupProperty.get();
            if (g == null) {
                showError("Chưa chọn nhóm", "Hãy chọn một nhóm trước khi thêm thành viên.");
                return;
            }
            AddGroupMembersDialog.display(g);
            // sau khi thêm, không cần reload ngay hội thoại vì chỉ thay đổi thành viên
        });
        
    
        
        // Create a VBox for the chat area
        chatBox.setPadding(new Insets(10));
        chatBox.setSpacing(30);
        chatBox.setStyle("-fx-background-color: #23272A");

        // Create a TextArea for displaying the messages
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setStyle("-fx-control-inner-background: #2C2F33; -fx-text-fill: white;");

        // Create a HBox for the bottom bar (message input and send button)
        HBox bottomBar = new HBox();
        bottomBar.setSpacing(0);
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(0, 0, 10, 0));

        
        // Create a TextField for entering messages
        messageInput = new TextField();
        messageInput.setPromptText("Nhập tin nhắn...");
        messageInput.getStyleClass().add("text-field");
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        
        // Button đính kèm file
        Button attachFileButton = new Button("📎");
        attachFileButton.getStyleClass().add("button-secondary");
        attachFileButton.setPrefWidth(50);
        attachFileButton.setTooltip(new Tooltip("Đính kèm file"));
        attachFileButton.setOnAction(e -> {
            if (selectedUserProperty.get() != null) {
                sendFileToContact(selectedUserProperty.get());
            } else if (selectedGroupProperty.get() != null) {
                sendFileToGroup(selectedGroupProperty.get());
            } else {
                showError("Chưa chọn", "Vui lòng chọn một liên hệ hoặc nhóm để gửi file.");
            }
        });
        
        // Send on Enter key
        messageInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (selectedUserProperty.get() != null) {
                    sendContactMessage();
                } else if (selectedGroupProperty.get() != null) {
                    sendGroupMessage(selectedGroupProperty);
                }
            }
        });

        // Create a Button for sending messages
        Button sendButton = new Button("Gửi");
        sendButton.getStyleClass().add("button");
        sendButton.setPrefWidth(80);

        sendButton.setOnAction(e -> {
            if (selectedUserProperty.get() != null) {
                sendContactMessage();
            } else if (selectedGroupProperty.get() != null) {
                sendGroupMessage(selectedGroupProperty);
            } else {
                showError("Chưa chọn", "Vui lòng chọn một liên hệ hoặc nhóm để gửi tin nhắn.");
            }
        });
        
        // Button gửi file
        Button sendFileButton = new Button("📎");
        sendFileButton.getStyleClass().add("button-secondary");
        sendFileButton.setPrefWidth(50);
        sendFileButton.setStyle("-fx-font-size: 18px;");
        sendFileButton.setTooltip(new Tooltip("Gửi file"));
        sendFileButton.setOnAction(e -> {
            if (selectedUserProperty.get() != null) {
                sendFile();
            } else if (selectedGroupProperty.get() != null) {
                sendFileToGroup(selectedGroupProperty);
            } else {
                showError("Chưa chọn", "Vui lòng chọn một liên hệ hoặc nhóm để gửi file.");
            }
        });
        
        // Button emote
        Button emoteButton = new Button("😊");
        emoteButton.getStyleClass().add("button-secondary");
        emoteButton.setPrefWidth(50);
        emoteButton.setStyle("-fx-font-size: 18px;");
        emoteButton.setTooltip(new Tooltip("Chọn emoji"));
        
        // Tạo popup chọn emoji
        final Stage emotePopup = new Stage();
        emotePopup.initModality(javafx.stage.Modality.NONE);
        emotePopup.setTitle("Chọn Emoji");
        
        VBox emoteContainer = new VBox(10);
        emoteContainer.setStyle("-fx-background-color: #1a1a1a;");
        emoteContainer.setPadding(new Insets(10));
        
        // Header
        Label headerLabel = new Label("Cảm xúc");
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        GridPane emoteGrid = new GridPane();
        emoteGrid.setPadding(new Insets(10));
        emoteGrid.setHgap(5);
        emoteGrid.setVgap(5);
        emoteGrid.setStyle("-fx-background-color: #2a2a2a;");
        
        // Danh sách emoji đầy đủ giống hình mẫu - 8 cột x 6 hàng = 48 emoji
        String[] emojis = {
            // Hàng 1
            "\uD83D\uDE42", "\uD83D\uDE10", "\uD83D\uDE0D", "\uD83D\uDE02", "\uD83D\uDE0E", "\uD83D\uDE2D", "\uD83E\uDD14", "\uD83E\uDD2B",
            // Hàng 2  
            "\uD83D\uDE0C", "\uD83D\uDE20", "\uD83E\uDD2F", "\uD83E\uDD2A", "\uD83D\uDE21", "\uD83D\uDE2C", "\uD83E\uDD73", "\uD83D\uDE36",
            // Hàng 3
            "\uD83D\uDE0A", "\uD83D\uDE33", "\uD83D\uDE0E", "\uD83D\uDE1C", "\uD83E\uDD13", "\uD83D\uDE12", "\uD83E\uDD10", "\uD83D\uDE2E",
            // Hàng 4
            "\uD83E\uDD28", "\uD83D\uDE24", "\uD83E\uDD11", "\uD83E\uDD2E", "\uD83E\uDD24", "\uD83D\uDE06", "\uD83D\uDE0E", "\uD83D\uDE21",
            // Hàng 5
            "\uD83E\uDD2D", "\uD83D\uDE1F", "\uD83D\uDE2B", "\uD83D\uDE35", "\uD83E\uDD22", "\uD83D\uDE0F", "\uD83D\uDE36", "\uD83D\uDE44",
            // Hàng 6
            "\uD83D\uDE24", "\uD83E\uDD27", "\uD83D\uDE2A", "\uD83D\uDE35\u200D\uD83D\uDCAB", "\uD83E\uDD10", "\uD83D\uDE31", "\uD83D\uDC80", "\uD83E\uDD21"
        };
        
        int row = 0, col = 0;
        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setStyle(
                "-fx-font-size: 30px; " +
                "-fx-min-width: 50px; " +
                "-fx-min-height: 50px; " +
                "-fx-max-width: 50px; " +
                "-fx-max-height: 50px; " +
                "-fx-background-color: transparent; " +
                "-fx-background-radius: 8px; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 5;"
            );
            
            // Hover effect - giống như hình mẫu
            emojiBtn.setOnMouseEntered(ev -> {
                emojiBtn.setStyle(
                    "-fx-font-size: 30px; " +
                    "-fx-min-width: 50px; " +
                    "-fx-min-height: 50px; " +
                    "-fx-max-width: 50px; " +
                    "-fx-max-height: 50px; " +
                    "-fx-background-color: #3a3a3a; " +  // Sáng lên khi hover
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 5;"
                );
            });
            
            emojiBtn.setOnMouseExited(ev -> {
                emojiBtn.setStyle(
                    "-fx-font-size: 30px; " +
                    "-fx-min-width: 50px; " +
                    "-fx-min-height: 50px; " +
                    "-fx-max-width: 50px; " +
                    "-fx-max-height: 50px; " +
                    "-fx-background-color: transparent; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 5;"
                );
            });
            
            emojiBtn.setOnAction(ev -> {
                messageInput.setText(messageInput.getText() + emoji);
                messageInput.requestFocus();
                messageInput.positionCaret(messageInput.getText().length());
                emotePopup.hide();
            });
            emoteGrid.add(emojiBtn, col, row);
            col++;
            if (col > 7) {
                col = 0;
                row++;
            }
        }
        
        emoteContainer.getChildren().addAll(headerLabel, emoteGrid);
        
        ScrollPane emoteScroll = new ScrollPane(emoteContainer);
        emoteScroll.setFitToWidth(true);
        emoteScroll.setStyle("-fx-background: #1a1a1a; -fx-background-color: #1a1a1a;");
        Scene emoteScene = new Scene(emoteScroll, 450, 400);
        emoteScene.getStylesheets().add(getClass().getResource("css/modern.css").toExternalForm());
        emotePopup.setScene(emoteScene);
        
        emoteButton.setOnAction(e -> {
            if (!emotePopup.isShowing()) {
                emotePopup.show();
                // Đặt vị trí popup gần button
                emotePopup.setX(primaryStage.getX() + 100);
                emotePopup.setY(primaryStage.getY() + primaryStage.getHeight() - 300);
            } else {
                emotePopup.hide();
            }
        });


        // Create bottom bar with modern styling
        bottomBar.setPadding(new Insets(12, 16, 12, 16));
        bottomBar.getStyleClass().add("top-bar");
        bottomBar.getChildren().addAll(sendFileButton, emoteButton, messageInput, sendButton);
        bottomBar.setSpacing(10);

        BorderPane wholeChat = new BorderPane();
        wholeChat.setTop(chatHeader);
        wholeChat.setCenter(chatScrollPane);
        wholeChat.setBottom(bottomBar);
   

        // Set the side panel, chat box, and top bar in the root layout
        root.setLeft(sidePanel);
        root.setCenter(wholeChat);
        VBox topContainer = new VBox(topBar, peerConfigBar);
        root.setTop(topContainer);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("css/modern.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

 
        
        User selectedUser = selectedUserProperty.get();
        if (selectedUser != null) {
            // Perform actions with the selected user
            System.out.println(selectedUser.toString());
        } else {
            System.out.println("No user selected.");
        }


        sidePanel.setCenter(contactsList);

    }


    private void sendContactMessage() {
        User recipient = selectedUserProperty.get();
        if (recipient == null) {
            showError("Peer not selected", "Hãy chọn một liên hệ để gửi tin nhắn.");
            return;
        }

        String messageContent = messageInput.getText().trim();
        if (messageContent.isEmpty()) {
            return;
        }

        if (tcpPeerService == null) {
            showError("P2P disabled", "Không thể gửi tin nhắn vì dịch vụ P2P chưa sẵn sàng.");
            return;
        }

        PeerEndpoint endpoint = peerDirectory.get(recipient.getUserId());
        if (endpoint == null) {
            endpoint = parseEndpointFromFields();
            if (endpoint == null) {
                return;
            }
            peerDirectory.put(recipient.getUserId(), endpoint);
        }

        try {
            tcpPeerService.send(endpoint.getHost(), endpoint.getPort(), recipient.getUserId(), messageContent);
        } catch (IOException e) {
            showError("Peer unreachable", "Không thể kết nối tới " + endpoint.getHost() + ":" + endpoint.getPort() + " (" + e.getMessage() + ")");
            return;
        }

        persistAndStoreMessage(loggedInUser.getUserId(), recipient.getUserId(), "user", messageContent);
        messageInput.clear();
        refreshContactConversation(recipient);
    }

    private void sendGroupMessage(ObjectProperty<Group> selectedGroupProperty) {
        // Get the content of the message from your input field
        String messageContent = messageInput.getText();
        
        Group group = selectedGroupProperty.get();
        
        persistAndStoreMessage(loggedInUser.getUserId(), group.getGroupId(), "group", messageContent);
        
        // Clear the input field after sending the message
        messageInput.clear();
        
        // Retrieve the updated list of messages
        List<Message> updatedMessages = UserGroupMessagesList.getGroupMessages(selectedGroupProperty.get());
        // Sort the updated messages
        updatedMessages.sort(Comparator.comparing(Message::getMessageId));
        
        // Clear the message area
        chatBox.getChildren().clear();
        
        // Iterate over the updated messages and display them
        for (Message message : updatedMessages) {
            addMessageLabel(message);
        }
        Platform.runLater(() -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    




    private void initializePeerService() {
        if (loggedInUser == null) {
            return;
        }
        tcpPeerService = new TcpPeerService(loggedInUser.getUserId(), this::handleIncomingPeerMessage);
        tcpPeerService.start();
    }

    private void handleIncomingPeerMessage(PeerMessage peerMessage) {
        Platform.runLater(() -> {
            // Với MongoDB Atlas dùng chung cho tất cả clients,
            // message CHỈ được lưu một lần ở phía người gửi.
            // Ở phía nhận chúng ta chỉ cần reload lại hội thoại từ database
            // để tránh tạo bản ghi trùng -> hiển thị gấp đôi tin nhắn.

            // Refresh conversation if the sender is currently selected
            if (selectedUserProperty.get() != null && selectedUserProperty.get().getUserId() == peerMessage.getSenderId()) {
                refreshContactConversation(selectedUserProperty.get());
            } else {
                // Show notification or update contact list
                System.out.println("Tin nhắn mới từ user " + peerMessage.getSenderId());
            }
        });
    }

    private void refreshContactConversation(User contact) {
        if (contact == null) {
            return;
        }
        List<Message> messages = MessageList.getContactMessages(contact);
        if (messages == null) {
            chatBox.getChildren().clear();
            return;
        }
        messages.sort(Comparator.comparing(Message::getMessageId));
        if (messageCount != null) {
            messageCount.setText("Tin nhắn: " + messages.size());
        }
        chatBox.getChildren().clear();
        for (Message message : messages) {
            addMessageLabel(message);
        }
        // Scroll to bottom after loading messages
        Platform.runLater(() -> {
            chatScrollPane.setVvalue(1.0);
        });
    }

    private void addMessageLabel(Message message) {
        boolean isSent = message.getSenderId() == loggedInUser.getUserId();
        boolean isFile = "file".equals(message.getMessageType());
        
        // Wrapper để tin nhắn gửi đi sát bên phải
        HBox messageWrapper = new HBox();
        messageWrapper.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        HBox.setHgrow(messageWrapper, Priority.ALWAYS);
        
        VBox messageContainer = new VBox(4);
        messageContainer.setMaxWidth(400);
        
        HBox messageBubble = new HBox(8);
        messageBubble.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        VBox messageContent = new VBox(4);
        messageContent.setMaxWidth(350);
        
        Label messageLabel = new Label(message.getContent());
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(10, 14, 10, 14));
        messageLabel.getStyleClass().add(isSent ? "chat-bubble-sent" : "chat-bubble-received");
        
        // If it's a file message, add download button
        if (isFile && message.getFileName() != null) {
            Button downloadBtn = new Button("💾 Tải xuống");
            downloadBtn.getStyleClass().add("button-secondary");
            downloadBtn.setStyle("-fx-font-size: 11px;");
            downloadBtn.setOnAction(e -> openFile(message.getFilePath()));
            
            Label fileSizeLabel = new Label(formatFileSize(message.getFileSize()));
            fileSizeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #8696a0;");
            
            VBox fileInfo = new VBox(4);
            fileInfo.getChildren().addAll(downloadBtn, fileSizeLabel);
            fileInfo.setPadding(new Insets(5, 0, 0, 0));
            messageContent.getChildren().add(fileInfo);
        }
        
        // Add timestamp
        String timeStr = formatTimestamp(message.getTimestamp());
        Label timeLabel = new Label(timeStr);
        timeLabel.getStyleClass().add("message-time");
        timeLabel.setPadding(new Insets(0, 8, 0, 8));
        
        messageContent.getChildren().addAll(messageLabel, timeLabel);
        messageContent.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        messageBubble.getChildren().add(messageContent);
        
        messageContainer.getChildren().add(messageBubble);
        messageContainer.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        messageLabel.setOnMouseClicked(mouseevent -> {
            selectedMessageProperty.set(message);
            displayMessageOptions(selectedMessageProperty.get());
        });
        
        messageWrapper.getChildren().add(messageContainer);
        chatBox.getChildren().add(messageWrapper);
        
        // Auto scroll to bottom
        Platform.runLater(() -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    
    private void openFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    java.awt.Desktop.getDesktop().open(file);
                } else {
                    showError("Lỗi", "File không tồn tại: " + filePath);
                }
            } catch (Exception e) {
                showError("Lỗi", "Không thể mở file: " + e.getMessage());
            }
        }
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
        return String.format("%.1f %sB", (double)size / (1L << (z*10)), " KMGTPE".charAt(z));
    }
    
    private String formatTimestamp(String timestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return dateTime.format(formatter);
        } catch (DateTimeParseException e) {
            return timestamp;
        }
    }
    
    
    private void filterContacts(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            contactsList.setItems(UserContacts.getContacts(loggedInUser).getItems());
        } else {
            ListView<User> allContacts = UserContacts.getContacts(loggedInUser);
            contactsList.getItems().clear();
            for (User contact : allContacts.getItems()) {
                if (contact.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    contactsList.getItems().add(contact);
                }
            }
        }
    }

    private void populatePeerFields(User user) {
        if (peerHostField == null || peerPortField == null) {
            return;
        }
        if (user == null) {
            peerHostField.clear();
            peerPortField.clear();
            return;
        }
        PeerEndpoint endpoint = peerDirectory.get(user.getUserId());
        if (endpoint == null) {
            peerHostField.setText("127.0.0.1");
            peerPortField.setText(String.valueOf(TcpPeerService.derivePortForUser(user.getUserId())));
        } else {
            peerHostField.setText(endpoint.getHost());
            peerPortField.setText(String.valueOf(endpoint.getPort()));
        }
    }

    private PeerEndpoint parseEndpointFromFields() {
        if (peerHostField == null || peerPortField == null) {
            showError("Peer config", "Peer fields are not ready yet.");
            return null;
        }
        String host = peerHostField.getText() == null ? "" : peerHostField.getText().trim();
        if (host.isEmpty()) {
            showError("Thiếu địa chỉ", "Vui lòng nhập địa chỉ IP hoặc hostname của peer.");
            return null;
        }
        String portValue = peerPortField.getText() == null ? "" : peerPortField.getText().trim();
        int port;
        User selected = selectedUserProperty.get();
        if (portValue.isEmpty()) {
            if (selected == null) {
                showError("Thiếu thông tin", "Không thể suy ra cổng khi chưa chọn liên hệ.");
                return null;
            }
            port = TcpPeerService.derivePortForUser(selected.getUserId());
        } else {
            try {
                port = Integer.parseInt(portValue);
            } catch (NumberFormatException e) {
                showError("Cổng không hợp lệ", "Giá trị cổng phải là số.");
                return null;
            }
        }
        return new PeerEndpoint(host, port);
    }

    private void rememberPeerForSelectedContact() {
        User selected = selectedUserProperty.get();
        if (selected == null) {
            showError("Chưa chọn liên hệ", "Hãy chọn liên hệ trước khi lưu thông tin peer.");
            return;
        }
        PeerEndpoint endpoint = parseEndpointFromFields();
        if (endpoint == null) {
            return;
        }
        peerDirectory.put(selected.getUserId(), endpoint);
        if (peerStatusLabel != null) {
            peerStatusLabel.setText("✓ Đã lưu peer cho " + selected.getName());
            peerStatusLabel.getStyleClass().removeAll("connection-status-disconnected", "connection-status-connecting");
            peerStatusLabel.getStyleClass().add("connection-status-connected");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Message persistAndStoreMessage(int senderId, int receiverId, String recipientType, String content) {
        int messageId = getNextMessageId();
        Message newMessage = new Message(messageId, senderId, receiverId, recipientType, content, LocalDateTime.now().toString());
        storeMessageInDatabase(newMessage);
        return newMessage;
    }

    private int getNextMessageId() {
        try {
            // Sử dụng DatabaseConnection để lấy database
            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
            MongoCollection<Document> messageCollection = database.getCollection("Message");
            FindIterable<Document> results = messageCollection.find().sort(Sorts.descending("message_id")).limit(1);
            Document latest = results.first();
            if (latest != null) {
                return latest.getInteger("message_id") + 1;
            }
            return 1;
        } catch (Exception e) {
            System.err.println("Error getting next message ID: " + e.getMessage());
            return 1;
        }
    }

    private void updatePeerStatusLabel() {
        if (peerStatusLabel == null) {
            return;
        }
        if (tcpPeerService != null) {
            peerStatusLabel.setText("✓ Đang lắng nghe cổng " + tcpPeerService.getListeningPort());
            peerStatusLabel.getStyleClass().removeAll("connection-status-disconnected", "connection-status-connecting");
            peerStatusLabel.getStyleClass().add("connection-status-connected");
        } else {
            peerStatusLabel.setText("✗ Dịch vụ P2P không hoạt động");
            peerStatusLabel.getStyleClass().removeAll("connection-status-connected", "connection-status-connecting");
            peerStatusLabel.getStyleClass().add("connection-status-disconnected");
        }
    }

    private void sendFileToContact(User recipient) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file để gửi");
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Copy file to uploads directory
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }
                
                String fileName = selectedFile.getName();
                File destFile = new File(uploadsDir, System.currentTimeMillis() + "_" + fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Create file message
                String fileMessage = "📎 File: " + fileName;
                Message message = persistFileMessage(
                    loggedInUser.getUserId(), 
                    recipient.getUserId(), 
                    "user", 
                    fileMessage,
                    fileName,
                    destFile.getAbsolutePath(),
                    selectedFile.length()
                );
                
                refreshContactConversation(recipient);
                
                showSuccess("Thành công", "Đã gửi file: " + fileName);
            } catch (Exception e) {
                showError("Lỗi", "Không thể gửi file: " + e.getMessage());
            }
        }
    }
    
    private void sendFileToGroup(Group group) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file để gửi");
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Copy file to uploads directory
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }
                
                String fileName = selectedFile.getName();
                File destFile = new File(uploadsDir, System.currentTimeMillis() + "_" + fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Create file message
                String fileMessage = "📎 File: " + fileName;
                Message message = persistFileMessage(
                    loggedInUser.getUserId(), 
                    group.getGroupId(), 
                    "group", 
                    fileMessage,
                    fileName,
                    destFile.getAbsolutePath(),
                    selectedFile.length()
                );
                
                // Refresh group messages
                List<Message> updatedMessages = UserGroupMessagesList.getGroupMessages(group);
                updatedMessages.sort(Comparator.comparing(Message::getMessageId));
                chatBox.getChildren().clear();
                for (Message msg : updatedMessages) {
                    addMessageLabel(msg);
                }
                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
                
                showSuccess("Thành công", "Đã gửi file: " + fileName);
            } catch (Exception e) {
                showError("Lỗi", "Không thể gửi file: " + e.getMessage());
            }
        }
    }
    
    private Message persistFileMessage(int senderId, int receiverId, String recipientType, 
                                       String content, String fileName, String filePath, long fileSize) {
        int messageId = getNextMessageId();
        Message newMessage = new Message(
            messageId, senderId, receiverId, recipientType, content, 
            LocalDateTime.now().toString(), "file", fileName, filePath, fileSize
        );
        storeFileMessageInDatabase(newMessage);
        return newMessage;
    }
    
    private void storeFileMessageInDatabase(Message message) {
        try {
            // Sử dụng DatabaseConnection để lấy database
            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
            MongoCollection<Document> messageCollection = database.getCollection("Message");
            
            Document messageDocument = new Document()
                .append("message_id", message.getMessageId())
                .append("sender_id", message.getSenderId())
                .append("recipient_id", message.getReceiverId())
                .append("recipient_type", message.getRecipientType())
                .append("timestamp", message.getTimestamp())
                .append("content", message.getContent())
                .append("message_type", message.getMessageType())
                .append("file_name", message.getFileName())
                .append("file_path", message.getFilePath())
                .append("file_size", message.getFileSize());
            
            messageCollection.insertOne(messageDocument);
        } catch (Exception e) {
            System.err.println("Error storing file message: " + e.getMessage());
        }
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void logout(Stage primaryStage) {
        // Clear session
        Session.setLoggedInUser(null);
        
        // Close P2P service
        if (tcpPeerService != null) {
            try {
                tcpPeerService.close();
            } catch (Exception e) {
                System.err.println("Error closing P2P service: " + e.getMessage());
            }
        }
        
        // Return to login screen
        Home loginScreen = new Home();
        loginScreen.start(primaryStage);
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (tcpPeerService != null) {
            tcpPeerService.close();
        }
    }

    private void storeMessageInDatabase(Message message) {
        // Replace this with your MongoDB logic to store the message in the Message collection
        // You can use a MongoDB driver or an ORM library like Spring Data MongoDB
        // Example code to store the message:
        // Sử dụng DatabaseConnection để lấy database
        MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
        MongoCollection<Document> messageCollection = database.getCollection("Message");
    
        // Create a new document for the message
        Document messageDocument = new Document()
            .append("message_id", message.getMessageId())
            .append("sender_id", message.getSenderId())
            .append("recipient_id", message.getReceiverId())
            .append("recipient_type", message.getRecipientType())
            .append("timestamp", message.getTimestamp())
            .append("content", message.getContent());
    
        // Insert the document into the Message collection
        messageCollection.insertOne(messageDocument);
    
        // Note: We don't close the DatabaseConnection as it's a singleton shared across the app
    }
    
    private void displayMessageOptions(Message selectedMessage) {
        // Get the selected message
        // Message selectedMessage = selectedMessageProperty.get();
        if (selectedMessage == null) {
            return; // No message selected, do nothing
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String css = getClass().getResource("css/modern.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);
        alert.setTitle("Message Options");
        alert.setHeaderText("Choose an option for the message:");
        alert.setContentText(selectedMessage.getContent());

    
        ButtonType editButton = new ButtonType("Edit");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    
        alert.getButtonTypes().setAll(editButton, deleteButton, cancelButton);
    
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == editButton) {
                // Handle the edit option
                editMessage(selectedMessage);
            } else if (result.get() == deleteButton) {
                // Handle the delete option
                deleteMessage(selectedMessage);
            }
        }
    }

    private void editMessage(Message message) {
        // Implement the logic to allow the user to edit the message
    
        TextInputDialog dialog = new TextInputDialog(message.getContent());
        dialog.setTitle("Edit Message");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the new message:");
        // input field?


        String css = getClass().getResource("css/modern.css").toExternalForm();
        dialog.getDialogPane().getStylesheets().add(css);

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        String updatedContent = result.get();
    
        try {
            // Sử dụng DatabaseConnection để lấy database
            MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
            MongoCollection<Document> messageCollection = database.getCollection("Message");
    
            for (Document doc: messageCollection.find()){
                if (doc.getInteger("message_id") == message.getMessageId()) {
                    messageCollection.updateOne(doc, new Document("$set", new Document("content", updatedContent)));
    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message Edited");
                    alert.setHeaderText(null);
                    alert.setContentText("Message has been edited");
                    alert.showAndWait();
    
                }
            }
        } catch (Exception e) {
            System.err.println("Error editing message: " + e.getMessage());
        }

    }
    
    private void deleteMessage(Message message) {
        
        // lets just set the conent to ""
        // message.setContent("");
        // Update the message in the database or data source
        // Sử dụng DatabaseConnection để lấy database
        MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
        MongoCollection<Document> messageCollection = database.getCollection("Message");

       // lets find that message in our database
        for (Document doc : messageCollection.find()) {
            if (doc.getInteger("message_id") == message.getMessageId()) {
                // delete the message
                messageCollection.deleteOne(doc);
            }
        }

    }


    



    
    // Send file to contact
    private void sendFile() {
        User recipient = selectedUserProperty.get();
        if (recipient == null) {
            showError("Peer not selected", "Hãy chọn một liên hệ để gửi file.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file để gửi");
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Create uploads directory if not exists
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }
                
                // Copy file to uploads directory
                String fileName = selectedFile.getName();
                File destFile = new File(uploadsDir, System.currentTimeMillis() + "_" + fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Create file message
                String fileMessage = "📎 File: " + fileName;
                long fileSize = selectedFile.length();
                
                // Store in database
                int messageId = getNextMessageId();
                Message fileMsg = new Message(
                    messageId,
                    loggedInUser.getUserId(),
                    recipient.getUserId(),
                    "user",
                    fileMessage,
                    LocalDateTime.now().toString(),
                    "file",
                    fileName,
                    destFile.getAbsolutePath(),
                    fileSize
                );
                storeFileMessageInDatabase(fileMsg);
                
                // Refresh conversation
                refreshContactConversation(recipient);
                
                // Show success
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setContentText("Đã gửi file: " + fileName);
                alert.show();
                
            } catch (Exception e) {
                showError("Lỗi", "Không thể gửi file: " + e.getMessage());
            }
        }
    }
    
    // Send file to group
    private void sendFileToGroup(ObjectProperty<Group> selectedGroupProperty) {
        Group group = selectedGroupProperty.get();
        if (group == null) {
            showError("Group not selected", "Hãy chọn một nhóm để gửi file.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file để gửi");
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Create uploads directory if not exists
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }
                
                // Copy file to uploads directory
                String fileName = selectedFile.getName();
                File destFile = new File(uploadsDir, System.currentTimeMillis() + "_" + fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Create file message
                String fileMessage = "📎 File: " + fileName;
                long fileSize = selectedFile.length();
                
                // Store in database
                int messageId = getNextMessageId();
                Message fileMsg = new Message(
                    messageId,
                    loggedInUser.getUserId(),
                    group.getGroupId(),
                    "group",
                    fileMessage,
                    LocalDateTime.now().toString(),
                    "file",
                    fileName,
                    destFile.getAbsolutePath(),
                    fileSize
                );
                storeFileMessageInDatabase(fileMsg);
                
                // Refresh group messages
                List<Message> updatedMessages = UserGroupMessagesList.getGroupMessages(group);
                updatedMessages.sort(Comparator.comparing(Message::getMessageId));
                chatBox.getChildren().clear();
                for (Message message : updatedMessages) {
                    addMessageLabel(message);
                }
                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
                
                // Show success
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setContentText("Đã gửi file: " + fileName);
                alert.show();
                
            } catch (Exception e) {
                showError("Lỗi", "Không thể gửi file: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}   