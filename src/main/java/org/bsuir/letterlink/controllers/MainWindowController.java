package org.bsuir.letterlink.controllers;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bsuir.letterlink.classes.*;
import org.bsuir.letterlink.entities.EmailEntity;
import org.bsuir.letterlink.entities.MessageEntity;
import org.bsuir.letterlink.factories.AbstractWindowFactory;
import org.bsuir.letterlink.factories.LoginWindowFactory;
import org.bsuir.letterlink.factories.MessageWindowFactory;
import org.bsuir.letterlink.tempclasses.DataClass;

import java.util.*;
import java.util.stream.Collectors;


public class MainWindowController {

    final String ip = "192.168.1.144";
    DataClass data = new DataClass();
    final int BUTTONS_AMOUNT = 15;

    private FolderHandler folderHandler;
    public static HashMap<Folder, ObservableList<MessageEntity>> messages;
    public static EmailEntity email;
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
    private Button updateButton;
    @FXML
    private Button addFolderButton;
    @FXML
    private Button deleteFolderButton;
    @FXML
    private Button moveMessagesButton;
    @FXML
    private ComboBox folderComboBox;
    @FXML
    private ImageView updateButtonImg;

    private Button selectedButton;
    private static Popup popup;
    private MessageRendererService messageRendererService;

    public static boolean isLetterlink(String address) {
        if (address.contains("@letterlink.com"))
            return true;
        return false;
    }

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
        Button button = new Button(folder.getName());
        button.setFont(new Font(16));
        button.setPrefHeight(1055.0 / BUTTONS_AMOUNT);
        button.setPrefWidth(foldersVBox.getPrefWidth());
        button.setOnAction(actionEvent -> {
            selectedButton = button;
            setUpFolders(folder);
        });
        foldersVBox.getChildren().add(button);
    }

    @FXML
    void onExitButtonClick() {
        try {
            for (Folder folder: messages.keySet()) {
                folder.close();
            }
            folderHandler.closeStore();
        } catch (MessagingException e) {

        }
        AbstractWindowFactory factory = new LoginWindowFactory();
        factory.create("login-form.fxml", "Login");
        ((Stage)anchorPane.getScene().getWindow()).close();
    }

    @FXML
    void onUpdateButtonClick() throws MessagingException {
        if (!isLetterlink(email.getAddress())) {
            for (Folder folder: messages.keySet()) {
                try {
//                    messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                    messages.get(folder).clear();
                    messages.get(folder).addAll(MessageHandler.getMessageEntityList(folder.getMessages()));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (Node node: foldersVBox.getChildren()) {
                Button button = null;
                if (((Button)node).getText().toLowerCase().equals(selectedFolder.getName().toLowerCase())) {
                    button = (Button) node;
                }
                if (button != null) {
                    Folder folder = folderHandler.getFolder(button.getText());
                    folder.open(Folder.READ_WRITE);
                    Folder oldFolder = null;
                    for (Folder f : messages.keySet()) {
                        if (f.getName().equals(folder.getName())) {
                            oldFolder = f;
                            break;
                        }
                    }
                    messages.remove(oldFolder);
                    messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                    Button finalButton = button;
                    button.setOnAction(actionEvent -> {
                        selectedButton = finalButton;
                        setUpFolders(folder);
                    });
                    button.fire();
                }
            }

            }
//        }
        messageTableView.refresh();
    }

    @FXML
    void onAddFolderButtonClick() {
        String name = Validator.showInputField("Add folder", "Please, enter the name", "Name");
        if (name == null || name.isBlank())
            return;
        Thread addFolderThread = new Thread(() -> {
            Platform.runLater(() -> addFolderButton.setDisable(true));
            try {
                Folder f = folderHandler.getFolder(name);
                if (!folderHandler.isFolderExists(messages.keySet(), name)) {
                    f.create(Folder.HOLDS_MESSAGES);
                    f.open(Folder.READ_WRITE);
                    System.out.println("Success folder");
                    messages.put(f, MessageHandler.getMessageEntityList(f.getMessages()));
                    Platform.runLater(() -> addFolderButton(f));
                } else {
                    Platform.runLater(() -> Validator.showAlert(Alert.AlertType.WARNING, "Add folder", "This folder can not be created", "Folder with this name already exists"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> addFolderButton.setDisable(false));
                Thread.currentThread().interrupt();
            }
        });
        addFolderThread.start();
    }

    @FXML
    void onDeleteFolderButtonClick() throws MessagingException {
        System.out.println(messages.keySet());
        selectedFolder.close();
        selectedFolder.delete(false);
        try {
            selectedFolder.open(Folder.READ_WRITE);
            Validator.showAlert(Alert.AlertType.ERROR, "Delete folder", "Can't delete selected folder", "This folder can't be deleted");
        } catch (Exception e) {
            messages.remove(selectedFolder);
            foldersVBox.getChildren().remove(selectedButton);
            Validator.showAlert(Alert.AlertType.INFORMATION, "Delete folder", "Success", "Folder was successfully deleted");
        }
    }

    private void deleteMessages() {
        List<MessageEntity> deletedMessages = messages.get(selectedFolder)
                .stream()
                .filter(entity -> entity.getChecked().isSelected())
                .collect(Collectors.toList());
        if (deletedMessages.isEmpty()) {
            deletedMessages.add(messageTableView.getSelectionModel().getSelectedItem());
        }
        if (deletedMessages.isEmpty() || deletedMessages.get(0) == null)
            return;
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
//                messages.put(trashFolder, entities);
                messages.get(trashFolder).clear();
                messages.get(trashFolder).addAll(entities);
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
    void onDeleteMessageClick() {
        if (!isLetterlink(email.getAddress())) {
            deleteMessages();
            return;
        }
        List<MessageEntity> deletedMessages = messages.get(selectedFolder)
                .stream()
                .filter(entity -> entity.getChecked().isSelected())
                .collect(Collectors.toList());
        if (deletedMessages.isEmpty()) {
            deletedMessages.add(messageTableView.getSelectionModel().getSelectedItem());
        }
        if (deletedMessages.isEmpty())
            return;
        Folder trashFolder = folderHandler.getTrashFolder(messages.keySet());
        if (trashFolder == null) {
            return;
        }
        try {
            if (!selectedFolder.getName().equals(trashFolder.getName())) {
                Message[] deletedArr = deletedMessages.stream()
                        .map(MessageEntity::getMessage)
                        .toArray(Message[]::new);
                selectedFolder.copyMessages(deletedArr, trashFolder);
                ObservableList<MessageEntity> entities = MessageHandler.getMessageEntityList(trashFolder.getMessages());
                entities.addAll(messages.get(trashFolder));
                if (!isLetterlink(email.getAddress())) {
                    messages.put(trashFolder, entities);
                } else {
                    Button button = null;
                    trashFolder.close();
                    for (Node node: foldersVBox.getChildren()) {
                        if (((Button)node).getText().toLowerCase().equals(trashFolder.getName().toLowerCase())) {
                            button = (Button) node;
                            break;
                        }
                    }
                    Folder folder = folderHandler.getFolder(trashFolder.getName());
                    folder.open(Folder.READ_WRITE);
                    messages.remove(trashFolder);
                    messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                    Button finalButton = button;
                    button.setOnAction(actionEvent -> {
                        selectedButton = finalButton;
                        setUpFolders(folder);
                    });
                }
            }
            for (MessageEntity entity : deletedMessages) {
                entity.getMessage().setFlag(Flags.Flag.DELETED, true);
            }
            messages.get(selectedFolder).removeAll(deletedMessages);
            selectedFolder.expunge();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onMoveButtonClick()  {
//        if (!isLetterlink(email.getAddress())){
//            deleteMessages();
//            return;
//        }
        List<MessageEntity> movedMessages = messages.get(selectedFolder)
                .stream()
                .filter(entity -> entity.getChecked().isSelected())
                .collect(Collectors.toList());
        if (movedMessages.isEmpty()) {
            movedMessages.add(messageTableView.getSelectionModel().getSelectedItem());
        }
        if (movedMessages.isEmpty() || movedMessages.get(0) == null)
            return;
        Message[] movedMessagesArray = movedMessages.stream()
                .map(MessageEntity::getMessage)
                .toArray(Message[]::new);
        Folder destination = (Folder) folderComboBox.getSelectionModel().getSelectedItem();
        try {
            selectedFolder.copyMessages(movedMessagesArray, destination);
            if (!isLetterlink(email.getAddress())) {
                for (Message message : movedMessagesArray) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }
            messages.get(selectedFolder).removeAll(movedMessages);
            if (isLetterlink(email.getAddress())) {
                for (Message message : movedMessagesArray) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
                selectedFolder.expunge();
            }
            if (!isLetterlink(email.getAddress())) {
                messages.put(destination, MessageHandler.getMessageEntityList(destination.getMessages()));
            } else {
                Button button = null;
                destination.close();
                for (Node node: foldersVBox.getChildren()) {
                    if (((Button)node).getText().toLowerCase().equals(destination.getName().toLowerCase())) {
                        button = (Button) node;
                        break;
                    }
                }
                Folder folder = folderHandler.getFolder(destination.getName());
                folder.open(Folder.READ_WRITE);
                messages.remove(destination);
                messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
                Button finalButton = button;
                button.setOnAction(actionEvent -> {
                    selectedButton = finalButton;
                    setUpFolders(folder);
                });
            }
            if (!isLetterlink(email.getAddress()))
                selectedFolder.expunge();
//            setUpComboBox();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @FXML
    void onButtonClick() {
        MessageWindowController.recipient = messageTableView.getSelectionModel().getSelectedItem().getSender();
        AbstractWindowFactory factory = new MessageWindowFactory();
        MessageWindowController.host = ServerConfig.getSmtpHost(email.getAddress());
        MessageWindowController.port = "587";
        factory.create("message-form.fxml", "New message");
    }
    private void initVisuals() {
        folderScrollPane.prefHeightProperty().bind(anchorPane.heightProperty().subtract(100));
        foldersVBox.prefHeightProperty().bind(folderScrollPane.heightProperty().subtract(2));

        folderScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderScrollPane.setPannable(true);
        splitPane.prefHeightProperty().bind(anchorPane.heightProperty());
        splitPane.prefWidthProperty().bind(anchorPane.widthProperty().subtract(160));
        splitPane.setDividerPosition(0, 0.5);

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
        testButton.setDisable(true);
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
                testButton.setDisable(false);
                messageRendererService.setEmailMessage(newValue);
                messageRendererService.restart();
            } else {
                testButton.setDisable(true);
            }
        });
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

    private void getFolders(Folder[] folders) throws MessagingException {
        for (Folder folder: folders) {
            if (folder.getName().equals("INBOX") && isLetterlink(email.getAddress())) {
                Folder[] subfolders = folder.list();
                if (subfolders != null && subfolders.length > 0) {
                    getFolders(subfolders);
                }
                continue;
            }
            folder.open(Folder.READ_WRITE);
            messages.put(folder, MessageHandler.getMessageEntityList(folder.getMessages()));
            if (folder.getName().equals("INBOX")) {
                messageTableView.setItems(messages.get(folder));
                selectedFolder = folder;
            }
            Platform.runLater(() -> addFolderButton(folder));

        }
    }

    private void setUpComboBox() {
        List<Folder> otherFolders = messages.keySet().stream()
                .filter(exFolder -> !exFolder.equals(folderHandler.getTrashFolder(messages.keySet())))
                .filter(exFolder -> !exFolder.equals(selectedFolder))
                .collect(Collectors.toCollection(ArrayList::new));
        folderComboBox.setItems(FXCollections.observableList(otherFolders));
        folderComboBox.setValue(otherFolders.get(0));
    }

    public void initialize() {
        updateButtonImg.setImage(new Image("D:\\Uni\\2 year\\2nd semester\\ksis\\LetterLink\\src\\main\\resources\\org\\bsuir\\letterlink\\css\\arrow.png"));
        initVisuals();
        initMessagesTableView();
        messageRendererService = new MessageRendererService(webView.getEngine());
        messages = new HashMap<>();
        folderHandler = new FolderHandler(
                ServerConfig.getImapHost(email.getAddress()),
                DataClass.imapPort,
                email.getAddress(),
                email.getPassword()
        );
        try {
            folderHandler.openStore();
            getFolders(folderHandler.getFolders());
            List<Folder> otherFolders = messages.keySet().stream()
                    .filter(exFolder -> !exFolder.equals(folderHandler.getTrashFolder(messages.keySet())))
                    .filter(exFolder -> !exFolder.equals(selectedFolder))
                    .collect(Collectors.toCollection(ArrayList::new));
            Platform.runLater(() -> {
                folderComboBox.setItems(FXCollections.observableList(otherFolders));
                folderComboBox.setValue(otherFolders.get(0));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onMessageButtonClick() {
        MessageWindowController.recipient = null;
        AbstractWindowFactory factory = new MessageWindowFactory();
        MessageWindowController.host = ServerConfig.getSmtpHost(email.getAddress());
        MessageWindowController.port = "587";
        factory.create("message-form.fxml", "New message");
    }
}