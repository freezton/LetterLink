package org.bsuir.letterlink.controllers;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
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
                System.out.println("trying...");
                System.out.println(entity.getAddress());
                msg.setFrom(new InternetAddress(entity.getAddress()));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientTextField.getText()));  //"qwjvwjl@gmail.com"
                msg.setSubject(subjectTextField.getText(), "UTF-8");
                msg.setContent(htmlEditor.getHtmlText(), "text/html");
                Transport.send(msg);
                System.out.println("sent successfully");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            Thread.currentThread().interrupt();
        });
        emailThread.start();
    }

}
