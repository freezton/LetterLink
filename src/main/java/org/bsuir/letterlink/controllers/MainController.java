package org.bsuir.letterlink.controllers;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.bsuir.letterlink.classes.MessageHandler;
import org.bsuir.letterlink.entities.MessageEntity;
import org.bsuir.letterlink.tempclasses.DataClass;
import org.bsuir.letterlink.classes.FolderHandler;
import org.bsuir.letterlink.classes.MessageListCell;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    final double FOLDER_BUTTON_HEIGHT = 65.45;


    private HashMap<Folder, ObservableList<MessageEntity>> messages;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane folderScrollPane;
    @FXML
    private VBox foldersVBox;
    @FXML
    private TableView<MessageEntity> messageTableView;
    @FXML
    private TableColumn<MessageEntity, String> senderColumn;
    @FXML
    private TableColumn<MessageEntity, String> subjectColumn;
    @FXML
    private TableColumn<MessageEntity, String> dateColumn;

    void addFolderButton(Folder folder) {
        Button button = new Button(folder.getName());
        button.setPrefHeight(FOLDER_BUTTON_HEIGHT);
        button.setPrefWidth(foldersVBox.getPrefWidth());
        button.setOnAction(actionEvent -> {
            messageTableView.setItems(messages.get(folder));
        });
        foldersVBox.getChildren().add(button);
    }

    private void initMessagesTableView() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }
    @FXML
    void onButtonClick(ActionEvent event) {
        Button button = new Button("New Butt on");
        button.setPrefHeight(FOLDER_BUTTON_HEIGHT);
        button.setPrefWidth(foldersVBox.getPrefWidth());
        foldersVBox.getChildren().add(button);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        folderScrollPane.prefHeightProperty().bind(anchorPane.heightProperty());
        foldersVBox.prefHeightProperty().bind(folderScrollPane.heightProperty().subtract(2));
        folderScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setPannable(true);
        initMessagesTableView();
//        messagesListView.setCellFactory(param -> new MessageListCell());

        messages = new HashMap<>();

        FolderHandler folderHandler = new FolderHandler(DataClass.imapHost, DataClass.imapPort, DataClass.address, DataClass.password);
        try {
            folderHandler.openStore();
            for (Folder folder: folderHandler.getFolders()) {
                folder.open(Folder.READ_WRITE);
                messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                if (folder.getName().equals("INBOX")) {
                    messageTableView.setItems(messages.get(folder));
                }
                addFolderButton(folder);
            }

            Folder f = folderHandler.getFolder("INBOX");
//            Button b = (Button)foldersVBox.getChildren().get(0);
//            b.fire();
//            messageTableView.setItems(messages.get(f));
//            messagesListView.setItems(FXCollections.observableList(Arrays.asList(f.getMessages())));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}