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
import org.bsuir.letterlink.factories.MessageWindowFactory;
import org.bsuir.letterlink.factories.RegistrationWindowFactory;

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

    void login() {
        AbstractWindowFactory factory = new MainWindowFactory();
        try {
            FolderHandler folderHandler = new FolderHandler(SessionHandler.getImapHost(
                    emailField.getText()),
                    SessionHandler.getImapPort(emailField.getText()),
                    emailField.getText(), passwordField.getText()
            );
            // попытка получить доступ к хранилищу
            folderHandler.openStore();
            // сохранение данных для входа, если установлен чекбокс
            if (rememberMeCheckBox.isSelected()) {
                saveCredentials();
            }
            // отображение главного окна
            factory.create("main-form.fxml", "Letterlink", emailField.getText(), passwordField.getText());
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.close();
            folderHandler.closeStore();
        } catch (Exception e) {
            // вывод сообщения об ошибке
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
