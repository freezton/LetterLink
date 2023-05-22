package org.bsuir.letterlink.controllers;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bsuir.letterlink.classes.SessionHandler;
import org.bsuir.letterlink.entities.EmailEntity;
import org.bsuir.letterlink.tempclasses.DataClass;
import org.bsuir.letterlink.tempclasses.TestSMTP;

import java.net.URL;
import java.util.ResourceBundle;

public class MessageWindowController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recipientTextField.setText("qwjvwjl@gmail.com");
    }

    @FXML
    void attachBtnAction(ActionEvent event) {

    }

    @FXML
    void sendButtonAction(ActionEvent event) {
        Thread emailThread = new Thread(() -> {
            EmailEntity entity = MainWindowController.emails.get("letterlink.test@mail.ru");
            Session session = SessionHandler.getSession("smtp", DataClass.smtpHost, DataClass.smtpPort, entity.getAuth());
            MimeMessage msg = new MimeMessage(session);
            try {
                Platform.runLater(() -> showAutoHidePopup("Sending message..."));
                System.out.println(entity.getAddress());
                msg.setFrom(new InternetAddress(entity.getAddress()));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientTextField.getText()));  //"qwjvwjl@gmail.com"
                msg.setSubject(subjectTextField.getText(), "UTF-8");
                msg.setContent(htmlEditor.getHtmlText(), "text/html");
                Transport.send(msg);
                Platform.runLater(() -> showAutoHidePopup("Message successfully sent"));
            } catch (MessagingException e) {
//                throw new RuntimeException(e);
                Platform.runLater(() -> showAutoHidePopup("Oops! Something went wrong"));
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
