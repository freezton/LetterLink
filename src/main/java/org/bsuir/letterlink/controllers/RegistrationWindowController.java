package org.bsuir.letterlink.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.bsuir.letterlink.classes.Validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RegistrationWindowController {

    final String IP = "192.168.1.144";
    final int PORT = 85;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField repeatPasswordField;
    @FXML
    private Button signUpButton;

    private boolean isCorrectAddress(String address) {
        return address.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    }

    private boolean isCorrectPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$");
    }

    @FXML
    void onSignUpButtonClick() {
        String address = emailField.getText();
        String password = passwordField.getText();
        if (!isCorrectAddress(address)) {
            Validator.showAlert(Alert.AlertType.WARNING, "Incorrect address", "Address must be as 'example@letterlink.com'", "Try one more time");
            return;
        }
//        if (!isCorrectPassword(password)) {
//            Validator.showAlert(Alert.AlertType.WARNING, "Incorrect password", "Password must contain latin symbols, numbers and at least one special symbol (one of them: !@#&)", "Try one more time");
//            return;
//        }
        if (!password.equals(repeatPasswordField.getText())) {
            Validator.showAlert(Alert.AlertType.WARNING, "Incorrect password", "Password mismatch", "Try one more time");
            return;
        }
        try {
            String login = address.substring(0, address.indexOf('@'));
            Socket socket = new Socket(IP, PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            StringBuilder message = new StringBuilder("REG ");
            message.append(login).append(" ").append(password);
            writer.println(message);
        } catch (IOException e) {
            Validator.showAlert(Alert.AlertType.WARNING, "Registration", "Something went wrong", "Try one more time");
        }
    }
}
