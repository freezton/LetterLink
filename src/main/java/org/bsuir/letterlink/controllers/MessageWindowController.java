package org.bsuir.letterlink.controllers;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bsuir.letterlink.classes.SessionHandler;
import org.bsuir.letterlink.classes.Validator;
import org.bsuir.letterlink.entities.EmailEntity;
import org.bsuir.letterlink.tempclasses.DataClass;
import org.bsuir.letterlink.tempclasses.TestSMTP;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MessageWindowController implements Initializable {

    @FXML
    private Button attachButton;

    @FXML
    private ComboBox<String> attachComboBox;

    @FXML
    private Button deleteAttachButton;
    @FXML
    private Label emailAddress;
    @FXML
    private Label errorLabel;
    @FXML
    private HTMLEditor htmlEditor;
    @FXML
    private TextField recipientTextField;
    @FXML
    private TextField subjectTextField;
    @FXML
    private Label infoLabel;

//    private List<String> attachments;
    public static String host, port;
    public static String recipient;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emailAddress.setText(MainWindowController.email.getAddress());
        infoLabel.setText("");
        if (recipient != null && !recipient.isEmpty()) {
            recipientTextField.setText(recipient);
        }
        if (MainWindowController.isLetterlink(MainWindowController.email.getAddress())) {
            attachButton.setDisable(true);
            deleteAttachButton.setDisable(true);
            attachComboBox.setDisable(true);
        }
        attachComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                deleteAttachButton.setDisable(true);
                attachComboBox.setDisable(true);
            } else {
                deleteAttachButton.setDisable(false);
                attachComboBox.setDisable(false);
            }
        });
    }

    @FXML
    void attachBtnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        File selectedFile = fileChooser.showOpenDialog(attachButton.getScene().getWindow());
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            attachComboBox.getItems().add(filePath);
            attachComboBox.getSelectionModel().select(filePath);
        }
    }

    @FXML
    void deleteAttachButtonClick(ActionEvent event) {
        attachComboBox.getItems().remove(attachComboBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    void sendButtonAction() {
        if (recipientTextField.getText().isEmpty()) {
            Validator.showAlert(Alert.AlertType.ERROR, "Send message", "Error", "You must specify the recipient");
            return;
        }
        Thread emailThread = new Thread(() -> {
            EmailEntity entity = MainWindowController.email;
            Session session;
            if (MainWindowController.isLetterlink(MainWindowController.email.getAddress()))
                session = SessionHandler.getSession("smtp", host, port, MainWindowController.email.getAuth());
            else
                session = SessionHandler.getSession("smtps", host, port, MainWindowController.email.getAuth());
            MimeMessage message = new MimeMessage(session);
            try {

                Platform.runLater(() -> infoLabel.setText("Sending message. Please, wait..."));
                message.setFrom(new InternetAddress(entity.getAddress()));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientTextField.getText()));
                message.setSubject(subjectTextField.getText(), "UTF-8");
                message.setContent(htmlEditor.getHtmlText(), "text/html");

                if (attachComboBox.getItems() != null && !attachComboBox.getItems().isEmpty()) {
                    Multipart multipart = new MimeMultipart();
                    for (String file : attachComboBox.getItems()) {
                        MimeBodyPart attachment = new MimeBodyPart();
                        attachment.attachFile(file);
                        multipart.addBodyPart(attachment);
                    }
                    if (!(multipart.getCount() == 0))
                        message.setContent(multipart);
                }

                Transport.send(message);
                Platform.runLater(()-> Validator.showAlert(Alert.AlertType.INFORMATION, "Send message", "Success", "Message was successfully sent"));
                Platform.runLater(()-> ((Stage)infoLabel.getScene().getWindow()).close());
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
                Platform.runLater(()-> Validator.showAlert(Alert.AlertType.ERROR, "Send message", "Error", "Message could not be delivered"));
            }
            Thread.currentThread().interrupt();
        });
        emailThread.start();
    }

    private void showAutoHidePopup(String text) {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: lightgray;");
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 24px");
        VBox.setMargin(label, new Insets(10));
        content.getChildren().add(label);

        Popup popup = new Popup();
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

        Stage primaryStage = (Stage)htmlEditor.getScene().getWindow();
        popup.show(primaryStage,
                1920 - label.getWidth() - 300,
                1080 - label.getHeight() - 100);

        fadeOutTransition.setDelay(Duration.seconds(2));
        fadeOutTransition.play();
    }

}
