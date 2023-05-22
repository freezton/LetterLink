package org.bsuir.letterlink.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RegistrationWindowController {

    final String ip = "192.168.1.144";
    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button signUpButton;

    @FXML
    void onSignUpButtonClick() throws IOException {
        Socket socket = new Socket(ip, 85);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        String message = "REG exmaple 1234";
        writer.println(message);
    }
}
