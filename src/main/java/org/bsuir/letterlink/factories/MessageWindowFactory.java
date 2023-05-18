package org.bsuir.letterlink.factories;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.bsuir.letterlink.Application;
import org.bsuir.letterlink.controllers.ControllerManager;

public class MessageWindowFactory implements AbstractWindowFactory {
    @Override
    public void create(String fxmlFilename, String formTitle) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcesRoot + fxmlFilename));
        Stage stage = new Stage();
        Object controller = ControllerManager.getController(formTitle, loader, stage);
        stage.showAndWait();
        assert controller != null;
//        return ((BookController)controller).getBook();
    }
}
