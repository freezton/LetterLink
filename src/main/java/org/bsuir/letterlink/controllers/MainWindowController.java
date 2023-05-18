package org.bsuir.letterlink.controllers;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bsuir.letterlink.classes.MessageHandler;
import org.bsuir.letterlink.entities.EmailEntity;
import org.bsuir.letterlink.entities.MessageEntity;
import org.bsuir.letterlink.factories.AbstractWindowFactory;
import org.bsuir.letterlink.factories.MessageWindowFactory;
import org.bsuir.letterlink.tempclasses.DataClass;
import org.bsuir.letterlink.classes.FolderHandler;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    final double FOLDER_BUTTON_HEIGHT = 65.45;

    private HashMap<Folder, ObservableList<MessageEntity>> messages;
    public static HashMap<String, EmailEntity> emails = new HashMap<>();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ScrollPane folderScrollPane;
    @FXML
    private Pane messagePane;
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
    @FXML
    private SplitPane splitPane;
    private Popup popup;

    void addFolderButton(Folder folder) {
        Button button = new Button(folder.getName());
        button.setPrefHeight(FOLDER_BUTTON_HEIGHT);
        button.setPrefWidth(foldersVBox.getPrefWidth());
        button.setOnAction(actionEvent -> {
            messageTableView.setItems(messages.get(folder));
        });
        foldersVBox.getChildren().add(button);
    }

    @FXML
    void onDeleteMessageClick(ActionEvent event) {

    }
    @FXML
    void onButtonClick(ActionEvent event) {
//        Button button = new Button("New Butt on");
//        button.setPrefHeight(FOLDER_BUTTON_HEIGHT);
//        button.setPrefWidth(foldersVBox.getPrefWidth());
//        foldersVBox.getChildren().add(button);
        showAutoHidePopup();
    }
    private void initVisuals() {
        folderScrollPane.prefHeightProperty().bind(anchorPane.heightProperty());
        foldersVBox.prefHeightProperty().bind(folderScrollPane.heightProperty().subtract(2));
        folderScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setPannable(true);
        splitPane.prefHeightProperty().bind(anchorPane.heightProperty());
        splitPane.prefWidthProperty().bind(anchorPane.widthProperty());

//        AnchorPane.setTopAnchor(messageTableView, 0.0);
//        AnchorPane.setBottomAnchor(messageTableView, 0.0);
//        AnchorPane.setLeftAnchor(messageTableView, 0.0);
//        AnchorPane.setRightAnchor(messageTableView, 0.0);

        messageTableView.prefWidthProperty().bind(messagePane.widthProperty());
        messageTableView.prefHeightProperty().bind(messagePane.heightProperty().subtract(50));
        messagePane.prefHeightProperty().bind(splitPane.heightProperty());

        messageTableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = (double) newVal - 4;
            double columnWidth = tableWidth / messageTableView.getColumns().size();
            for (TableColumn column : messageTableView.getColumns()) {
                column.setPrefWidth(columnWidth);
            }
        });
    }
    private void initMessagesTableView() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }
    private void initEmails() {
        emails.put("letterlink.test@mail.ru", new EmailEntity(DataClass.address, DataClass.password));
    }

    private void showAutoHidePopup() {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: lightgray;");
        Label label = new Label("New message received");
        VBox.setMargin(label, new Insets(10));
        content.getChildren().add(label);

        popup = new Popup();
        popup.getContent().add(content);
        popup.setAutoHide(true);

        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), content);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);

        fadeInTransition.play();

        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), content);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(actionEvent -> popup.hide());

        Stage primaryStage = (Stage)anchorPane.getScene().getWindow();
        popup.show(primaryStage,
                primaryStage.getX() + primaryStage.getMaxWidth() - 20,
                primaryStage.getY() + primaryStage.getHeight() - 20);

        fadeOutTransition.setDelay(Duration.seconds(2));
        fadeOutTransition.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initVisuals();
        initMessagesTableView();
        initEmails();

        messages = new HashMap<>();
        Thread loaderThread = new Thread(() -> {
            FolderHandler folderHandler = new FolderHandler(DataClass.imapHost, DataClass.imapPort, DataClass.address, DataClass.password);
            try {
                folderHandler.openStore();
                for (Folder folder: folderHandler.getFolders()) {
                    folder.open(Folder.READ_WRITE);
                    messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                    if (folder.getName().equals("INBOX")) {
                        messageTableView.setItems(messages.get(folder));
                    }
                    Platform.runLater(() -> addFolderButton(folder));
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } finally {
                Thread.currentThread().interrupt();
            }
        });
        loaderThread.start();
    }

    @FXML
    void onMessageButtonClick() {
        AbstractWindowFactory factory = new MessageWindowFactory();
        factory.create("message-form.fxml", "New message");
    }
}