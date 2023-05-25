package org.bsuir.letterlink.controllers;

import jakarta.mail.AuthenticationFailedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bsuir.letterlink.classes.FolderHandler;
import org.bsuir.letterlink.classes.SessionHandler;
import org.bsuir.letterlink.factories.AbstractWindowFactory;
import org.bsuir.letterlink.factories.MainWindowFactory;
import org.bsuir.letterlink.factories.RegistrationWindowFactory;
import org.bsuir.letterlink.tempclasses.DataClass;

public class LoginWindowController {
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private ImageView logoImageView;


    public void initialize() {
        logoImageView.setImage(new Image("D:\\logo.png"));
    }

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        login();
    }

    final String ip = "192.168.1.144";

    void login() {
        AbstractWindowFactory factory = new MainWindowFactory();
        try {
//            FolderHandler folderHandler = new FolderHandler(
//                    SessionHandler.getImapHost(
//                    emailField.getText()),
//                    SessionHandler.getImapPort(emailField.getText()),
//                    emailField.getText(), passwordField.getText()
//            );
            FolderHandler folderHandler = new FolderHandler(
                    DataClass.imapHost,
                    DataClass.imapPort,
                    emailField.getText(),
                    passwordField.getText()
            );
            folderHandler.openStore();
            if (rememberMeCheckBox.isSelected()) {
                saveCredentials();
            }
            factory.create("main-form.fxml", "Letterlink", emailField.getText(), passwordField.getText());
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.close();
            folderHandler.closeStore();
        } catch (Exception e) {
            System.out.println("incorrect credentials");
        }
    }

    void saveCredentials() {

    }
    @FXML
    void onSignUpButtonClick(ActionEvent event) {
        AbstractWindowFactory factory = new RegistrationWindowFactory();
        factory.create("registration-form.fxml", "Registration");
    }
}
