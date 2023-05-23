package org.bsuir.letterlink.factories;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.bsuir.letterlink.controllers.ControllerManager;
import org.bsuir.letterlink.controllers.MainWindowController;
import org.bsuir.letterlink.entities.EmailEntity;

public class MainWindowFactory implements AbstractWindowFactory {
    @Override
    public Stage create(String fxmlFilename, String formTitle, String ... params) {
        MainWindowController.email = new EmailEntity(params[0], params[1]);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcesRoot + fxmlFilename));
        Stage stage = new Stage();
        stage.setResizable(true);
        Object controller = ControllerManager.getController(formTitle, loader, stage, true);
        stage.show();
        assert controller != null;
        return stage;
    }
}
