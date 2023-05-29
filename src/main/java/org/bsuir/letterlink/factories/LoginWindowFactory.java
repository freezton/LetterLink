package org.bsuir.letterlink.factories;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.bsuir.letterlink.controllers.ControllerManager;

public class LoginWindowFactory implements AbstractWindowFactory{
    @Override
    public Stage create(String fxmlFilename, String formTitle, String ... params) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcesRoot + fxmlFilename));
        Stage stage = new Stage();
        Object controller = ControllerManager.getController(formTitle, loader, stage, false);
        stage.show();
        assert controller != null;
        return stage;
    }
}
