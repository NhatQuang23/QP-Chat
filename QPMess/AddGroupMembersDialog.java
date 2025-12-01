import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import Collections.Group;
import Collections.User;
import Collections.UserContacts;
import SessionManager.Session;
import database.DatabaseConnection;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddGroupMembersDialog {

    public static void display(Group group) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Thêm thành viên vào nhóm " + group.getGroupName());

        Label info = new Label("Chọn liên hệ để thêm vào nhóm:");
        ListView<User> contactsView = UserContacts.getContacts(Session.getLoggedInUser());
        contactsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        contactsView.setPrefHeight(220);
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
                        int idx = contactsView.getItems().indexOf(item);
                        if (isNow) {
                            contactsView.getSelectionModel().select(idx);
                        } else {
                            contactsView.getSelectionModel().clearSelection(idx);
                        }
                    });
                    row.getChildren().add(cb);
                    setGraphic(row);
                }
            }
        });

        Button addBtn = new Button("Thêm vào nhóm");
        addBtn.setOnAction(e -> {
            ObservableList<User> selected = contactsView.getSelectionModel().getSelectedItems();
            if (selected.isEmpty()) {
                stage.close();
                return;
            }
            try {
                MongoDatabase db = DatabaseConnection.getInstance().getDatabase();
                MongoCollection<Document> groupMembers = db.getCollection("Group_Members");
                for (User u : selected) {
                    Document member = new Document("group_id", group.getGroupId())
                            .append("user_id", u.getUserId());
                    groupMembers.insertOne(member);
                }
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Thành công");
                ok.setHeaderText(null);
                ok.setContentText("Đã thêm " + selected.size() + " thành viên vào nhóm.");
                ok.showAndWait();
            } catch (Exception ex) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Lỗi");
                err.setHeaderText("Không thể thêm thành viên");
                err.setContentText(ex.getMessage());
                err.showAndWait();
            }
            stage.close();
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(12));
        grid.add(info, 0, 0, 2, 1);
        grid.add(contactsView, 0, 1, 2, 1);
        grid.add(addBtn, 1, 2);
        grid.setAlignment(Pos.CENTER);

        Scene scene = new Scene(grid, 420, 350);
        try {
            scene.getStylesheets().add(AddGroupMembersDialog.class.getResource("css/modern.css").toExternalForm());
        } catch (Exception ignore) {
        }

        stage.setScene(scene);
        stage.showAndWait();
    }
}


