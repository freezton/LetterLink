package org.bsuir.letterlink.controllers;

import jakarta.mail.AuthenticationFailedException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bsuir.letterlink.classes.FolderHandler;
import org.bsuir.letterlink.classes.ServerConfig;
import org.bsuir.letterlink.classes.SessionHandler;
import org.bsuir.letterlink.classes.Validator;
import org.bsuir.letterlink.factories.AbstractWindowFactory;
import org.bsuir.letterlink.factories.MainWindowFactory;
import org.bsuir.letterlink.factories.RegistrationWindowFactory;
import org.bsuir.letterlink.tempclasses.DataClass;

public class LoginWindowController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;


    public void initialize() {
        logoImageView.setImage(new Image("D:\\logo.png"));
    }

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        login();
    }

    void login() {
        AbstractWindowFactory factory = new MainWindowFactory();
        Thread loginThread = new Thread(() -> {
            try {

                Platform.runLater(() -> {
                    loginButton.setDisable(true);
                    signupButton.setDisable(true);
                });
                FolderHandler folderHandler = new FolderHandler(
                        ServerConfig.getImapHost(emailField.getText()),
                        DataClass.imapPort,
                        emailField.getText(),
                        passwordField.getText()
                );
                folderHandler.openStore();
                if (rememberMeCheckBox.isSelected()) {
                    saveCredentials();
                }
                Platform.runLater(() -> {
                    factory.create("main-form.fxml", "Letterlink", emailField.getText(), passwordField.getText());
                    Stage stage = (Stage) passwordField.getScene().getWindow();
                    stage.close();
                });
                folderHandler.closeStore();
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> Validator.showAlert(Alert.AlertType.ERROR, "Login", "Error", "Invalid credentials"));
                System.out.println("incorrect credentials");
            } finally {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    signupButton.setDisable(false);
                });
                Thread.currentThread().interrupt();
            }
        });
        loginThread.start();
    }

    void saveCredentials() {

    }
    @FXML
    void onSignUpButtonClick(ActionEvent event) {
//        emailField.setText("letterlink.test@mail.ru");
//        passwordField.setText("xt3tTHf6bMSFqxWrWN3Y");
        AbstractWindowFactory factory = new RegistrationWindowFactory();
        factory.create("registration-form.fxml", "Registration");
    }
}
