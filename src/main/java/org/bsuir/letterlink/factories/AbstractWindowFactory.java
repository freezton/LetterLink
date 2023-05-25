package org.bsuir.letterlink.factories;

import javafx.stage.Stage;

public interface AbstractWindowFactory {

    String resourcesRoot = "/org/bsuir/letterlink/fxml/";

    Stage create(String fxmlFilename, String formTitle, String ... params);
}
