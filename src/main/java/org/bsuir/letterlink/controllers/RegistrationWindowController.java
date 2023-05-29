package org.bsuir.letterlink.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.bsuir.letterlink.classes.ServerConfig;
import org.bsuir.letterlink.classes.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationWindowController implements Initializable {

    private String IP;
    final int PORT = 85;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
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
        final String[] response = {""};
        Thread thread = new Thread(() -> {
            try {
                Platform.runLater(()-> {
                    signUpButton.setDisable(true);
                    signUpButton.setText("Please, wait...");
                });
                String login = address.substring(0, address.indexOf('@'));
                Socket socket = new Socket(IP, PORT);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                StringBuilder message = new StringBuilder("REG ");
                message.append(login).append(" ").append(password);
                writer.println(message);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                response[0] = reader.readLine();
                response[0] = reader.readLine();

            } catch (IOException e) {
                Platform.runLater(()-> Validator.showAlert(Alert.AlertType.WARNING, "Registration", "Something went wrong", "Try one more time"));
            } finally {
                Platform.runLater(()-> {
                    signUpButton.setDisable(false);
                    signUpButton.setText("Sign up");
                });
                if (response[0].startsWith("250")) {
                    Platform.runLater(()-> Validator.showAlert(Alert.AlertType.INFORMATION, "Registration", "Success", "You have registered a new account"));
                }
                else {
                    Platform.runLater(()-> Validator.showAlert(Alert.AlertType.INFORMATION, "Registration", "Oops", "An account with the same name already exists"));
                }
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        IP = ServerConfig.getIp();
    }
}
