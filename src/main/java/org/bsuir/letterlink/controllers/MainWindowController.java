package org.bsuir.letterlink.controllers;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bsuir.letterlink.classes.MessageHandler;
import org.bsuir.letterlink.classes.MessageRendererService;
import org.bsuir.letterlink.entities.EmailEntity;
import org.bsuir.letterlink.entities.MessageEntity;
import org.bsuir.letterlink.factories.AbstractWindowFactory;
import org.bsuir.letterlink.factories.MessageWindowFactory;
import org.bsuir.letterlink.tempclasses.DataClass;
import org.bsuir.letterlink.classes.FolderHandler;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class MainWindowController implements Initializable {

    DataClass data = new DataClass();
    final int BUTTONS_AMOUNT = 15;

    private FolderHandler folderHandler;
    public static HashMap<Folder, ObservableList<MessageEntity>> messages;
    public static HashMap<String, EmailEntity> emails = new HashMap<>();
    private Folder selectedFolder = null;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane viewAnchorPane;
    @FXML
    private ScrollPane folderScrollPane;
    @FXML
    private Pane messagePane;
    @FXML
    private Button testButton;
    @FXML
    private VBox foldersVBox;
    @FXML
    private TableView<MessageEntity> messageTableView;
    @FXML
    private TableColumn<MessageEntity, Boolean> isCheckedColumn;
    @FXML
    private TableColumn<MessageEntity, String> senderColumn;
    @FXML
    private TableColumn<MessageEntity, String> subjectColumn;
    @FXML
    private TableColumn<MessageEntity, String> dateColumn;
    @FXML
    private SplitPane splitPane;
    @FXML
    private WebView webView;
    @FXML
    private Button exitButton;
    @FXML
    private Button addFolderButton;
    @FXML
    private Button deleteFolderButton;
    @FXML
    private Button moveMessagesButton;
    @FXML
    private ComboBox folderComboBox;

    private static Popup popup;
    private MessageRendererService messageRendererService;

    private void setUpFolders(Folder folder) {
        selectedFolder = folder;
        messageTableView.setItems(messages.get(folder));
        List<Folder> otherFolders = messages.keySet().stream()
                .filter(exFolder -> !exFolder.equals(selectedFolder))
                .filter(exFolder -> !exFolder.equals(folderHandler.getTrashFolder(messages.keySet())))
                .collect(Collectors.toCollection(ArrayList::new));
        folderComboBox.setItems(FXCollections.observableList(otherFolders));
        folderComboBox.setValue(otherFolders.get(0));
    }

    void addFolderButton(Folder folder) {
        Button button = new Button(folder.getName().equals("INBOX") ? "Inbox" : folder.getName());
        button.setFont(new Font(16));
        button.setPrefHeight(1055.0 / BUTTONS_AMOUNT);
        button.setPrefWidth(foldersVBox.getPrefWidth());
        button.setOnAction(actionEvent -> {
            setUpFolders(folder);
        });
        foldersVBox.getChildren().add(button);
    }

    @FXML
    void onExitButtonClick() {

    }

    @FXML
    void onAddFolderButtonClick() {

    }
    @FXML
    void onDeleteFolderButtonClick() {

    }
    @FXML
    void onDeleteMessageClick() {
        List<MessageEntity> deletedMessages = messages.get(selectedFolder)
                .stream()
                .filter(entity -> entity.getChecked().isSelected())
                .collect(Collectors.toList());

        if (deletedMessages.isEmpty()) {
            deletedMessages.add(messageTableView.getSelectionModel().getSelectedItem());
        }

        Folder trashFolder = folderHandler.getTrashFolder(messages.keySet());
        if (trashFolder == null) {
            return;
        }

        try {
            if (!selectedFolder.getName().equals(trashFolder.getName())) {
                Message[] deletedArr = deletedMessages.stream()
                        .map(MessageEntity::getMessage)
                        .toArray(Message[]::new);

                trashFolder.appendMessages(deletedArr);
                ObservableList<MessageEntity> entities = MessageHandler.getMessageEntityList(trashFolder.getMessages());
                entities.addAll(messages.get(trashFolder));
                messages.put(trashFolder, entities);

            }
            for (MessageEntity entity : deletedMessages) {
                entity.getMessage().setFlag(Flags.Flag.DELETED, true);
            }
            messages.get(selectedFolder).removeAll(deletedMessages);
            selectedFolder.expunge();
        } catch (MessagingException e) {
            e.printStackTrace();
            return;
        }
    }

    @FXML
    void onMoveButtonClick()  {
        List<MessageEntity> movedMessages = messages.get(selectedFolder)
                .stream()
                .filter(entity -> entity.getChecked().isSelected())
                .collect(Collectors.toList());

        if (movedMessages.isEmpty()) {
            movedMessages.add(messageTableView.getSelectionModel().getSelectedItem());
        }

        Message[] movedMessagesArray = movedMessages.stream()
                .map(MessageEntity::getMessage)
                .toArray(Message[]::new);

        Folder destination = (Folder) folderComboBox.getSelectionModel().getSelectedItem();

        try {
            selectedFolder.copyMessages(movedMessagesArray, destination);
            for (Message message : movedMessagesArray) {
                message.setFlag(Flags.Flag.DELETED, true);
            }

            messages.get(selectedFolder).removeAll(movedMessages);
            messages.put(destination, MessageHandler.getMessageEntityList(destination.getMessages()));
            selectedFolder.expunge();
        } catch (MessagingException e) {
            e.printStackTrace();
            return;
        }
    }


    @FXML
    void onButtonClick(ActionEvent event) {
        Button button = new Button("New Butt on");
        button.setPrefHeight(1055.0 / BUTTONS_AMOUNT);
        button.setPrefWidth(foldersVBox.getPrefWidth());
        foldersVBox.getChildren().add(button);

//        showAutoHidePopup();
    }
    private void initVisuals() {
        folderScrollPane.prefHeightProperty().bind(anchorPane.heightProperty().subtract(100));
        foldersVBox.prefHeightProperty().bind(folderScrollPane.heightProperty().subtract(2));

        folderScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setPannable(true);
        splitPane.prefHeightProperty().bind(anchorPane.heightProperty());
        splitPane.prefWidthProperty().bind(anchorPane.widthProperty().subtract(160));
        splitPane.setDividerPosition(0, 0.4);

        AnchorPane.setBottomAnchor(exitButton, 0.0);
        AnchorPane.setBottomAnchor(addFolderButton, 50.0);
        AnchorPane.setBottomAnchor(deleteFolderButton, 50.0);

        webView.prefHeightProperty().bind(viewAnchorPane.heightProperty().subtract(webView.getLayoutY()));
        webView.prefWidthProperty().bind(viewAnchorPane.widthProperty());

        messageTableView.prefWidthProperty().bind(messagePane.widthProperty());
        messageTableView.prefHeightProperty().bind(messagePane.heightProperty().subtract(50));
        messagePane.prefHeightProperty().bind(splitPane.heightProperty());

        messageTableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = (double) newVal - 4;
            double columnWidth = (tableWidth - 22) / ( messageTableView.getColumns().size() - 1 );
            for (TableColumn column : messageTableView.getColumns()) {
                if (column.equals(isCheckedColumn)) {
                    column.setPrefWidth(22);
                } else {
                    column.setPrefWidth(columnWidth);
                }
            }
        });
    }
    private void initMessagesTableView() {
        isCheckedColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
        senderColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        senderColumn.setEditable(true);
        senderColumn.setOnEditCommit(event -> {
            System.out.println("edited");
        });
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        messageTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                messageRendererService.setEmailMessage(newValue);
                messageRendererService.restart();
            } else {
//                System.out.println("No message selected.");
            }
        });
    }
    private void initEmails() {
        emails.put("letterlink.test@mail.ru", new EmailEntity(DataClass.address, DataClass.password));
    }

    public void showAutoHidePopup() {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: lightgray;");
        Label label = new Label("New message received");
        label.setStyle("-fx-font-size: 24px");
        VBox.setMargin(label, new Insets(10));
        content.getChildren().add(label);

        popup = new Popup();
        popup.getContent().add(content);
        popup.setAutoHide(false);

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
                1920 - label.getWidth() - 300,
                1080 - label.getHeight() - 100);

        fadeOutTransition.setDelay(Duration.seconds(2));
        fadeOutTransition.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initVisuals();
        initMessagesTableView();
        initEmails();

        webView.getEngine().loadContent("");
        messageRendererService = new MessageRendererService(webView.getEngine());

        messages = new HashMap<>();
        Thread loaderThread = new Thread(() -> {
            folderHandler = new FolderHandler(data.imapHost, data.imapPort, data.address, data.password);
            try {
                folderHandler.openStore();
                for (Folder folder: folderHandler.getFolders()) {
                    folder.open(Folder.READ_WRITE);

                    messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                    if (folder.getName().equals("INBOX")) {
                        messageTableView.setItems(messages.get(folder));
                        selectedFolder = folder;
                    }
                    Platform.runLater(() -> addFolderButton(folder));
                }
                List<Folder> otherFolders = messages.keySet().stream()
                        .filter(exFolder -> !exFolder.equals(selectedFolder))
                        .collect(Collectors.toCollection(ArrayList::new));
                Platform.runLater(() -> {
                    folderComboBox.setItems(FXCollections.observableList(otherFolders));
                    folderComboBox.setValue(otherFolders.get(0));
                });
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