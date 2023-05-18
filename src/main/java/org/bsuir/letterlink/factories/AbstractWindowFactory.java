package org.bsuir.letterlink.factories;

public interface AbstractWindowFactory {

    String resourcesRoot = "/org/bsuir/letterlink/fxml/";

    void create(String fxmlFilename, String formTitle);
//    void edit(Message)
}
