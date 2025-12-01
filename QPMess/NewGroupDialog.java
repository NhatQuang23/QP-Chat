import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import Collections.User;
import Collections.UserContacts;
import SessionManager.Session;
import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewGroupDialog {

    public static void display() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Tạo nhóm mới — QPMess");

        // Labels & fields
        Label nameLabel = new Label("Tên nhóm:");
        TextField nameField = new TextField();
        nameField.setPromptText("Nhập tên nhóm");

        Label descLabel = new Label("Mô tả:");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Mô tả ngắn về nhóm (không bắt buộc)");
        descArea.setPrefRowCount(3);

        // Danh sách liên hệ để chọn thành viên nhóm
        Label membersLabel = new Label("Thành viên:");
        ListView<User> contactsView = UserContacts.getContacts(Session.getLoggedInUser());
        contactsView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        contactsView.setPrefHeight(160);
        contactsView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(8);
                    CheckBox cb = new CheckBox(item.getName());
                    cb.selectedProperty().addListener((obs, was, isNow) -> {
                        if (isNow) {
                            if (!contactsView.getSelectionModel().getSelectedItems().contains(item)) {
                                contactsView.getSelectionModel().select(item);
                            }
                        } else {
                            contactsView.getSelectionModel().clearSelection(contactsView.getItems().indexOf(item));
                        }
                    });
                    row.getChildren().add(cb);
                    setGraphic(row);
                }
            }
        });

        Button createButton = new Button("Tạo nhóm");
        createButton.setOnAction(e -> {
            String groupName = nameField.getText() == null ? "" : nameField.getText().trim();
            String description = descArea.getText() == null ? "" : descArea.getText().trim();

            if (groupName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Thiếu tên nhóm");
                alert.setContentText("Vui lòng nhập tên nhóm.");
                alert.showAndWait();
                return;
            }

            try {
                MongoDatabase database = DatabaseConnection.getInstance().getDatabase();
                if (database == null || Session.getLoggedInUser() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi hệ thống");
                    alert.setHeaderText("Không thể tạo nhóm");
                    alert.setContentText("Không kết nối được cơ sở dữ liệu hoặc người dùng chưa đăng nhập.");
                    alert.showAndWait();
                    return;
                }

                MongoCollection<Document> groupsCollection = database.getCollection("Groups");
                MongoCollection<Document> groupMembersCollection = database.getCollection("Group_Members");

                // Lấy group_id tiếp theo
                int groupId = 1;
                com.mongodb.client.FindIterable<Document> results = groupsCollection
                        .find()
                        .sort(Sorts.descending("group_id"))
                        .limit(1);
                Document latest = results.first();
                if (latest != null && latest.get("group_id") != null) {
                    groupId = latest.getInteger("group_id") + 1;
                }

                // Tạo document nhóm
                Document newGroup = new Document("group_id", groupId)
                        .append("group_name", groupName)
                        .append("description", description)
                        .append("admin", Session.getLoggedInUser().getUserId())
                        .append("profile_picture", "");
                groupsCollection.insertOne(newGroup);

                // Thêm người tạo vào bảng Group_Members
                Document memberDoc = new Document("group_id", groupId)
                        .append("user_id", Session.getLoggedInUser().getUserId());
                groupMembersCollection.insertOne(memberDoc);

                // Thêm các thành viên được chọn từ danh sách liên hệ
                ObservableList<User> selectedUsers = contactsView.getSelectionModel().getSelectedItems();
                for (User u : selectedUsers) {
                    if (u.getUserId() == Session.getLoggedInUser().getUserId()) {
                        continue; // đã thêm ở trên
                    }
                    Document member = new Document("group_id", groupId)
                            .append("user_id", u.getUserId());
                    groupMembersCollection.insertOne(member);
                }

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Thành công");
                success.setHeaderText("Nhóm đã được tạo");
                success.setContentText("Nhóm \"" + groupName + "\" đã được tạo thành công.");
                success.showAndWait();

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể tạo nhóm");
                alert.setContentText("Chi tiết: " + ex.getMessage());
                alert.showAndWait();
            }

            dialogStage.close();
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(12));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(membersLabel, 0, 2);
        grid.add(contactsView, 1, 2);
        grid.add(createButton, 1, 3);

        grid.setAlignment(Pos.CENTER);

        Scene scene = new Scene(grid);
        try {
            scene.getStylesheets().add(NewGroupDialog.class.getResource("css/modern.css").toExternalForm());
        } catch (Exception ignore) {
            // nếu không tìm thấy css thì vẫn chạy bình thường
        }
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
}


