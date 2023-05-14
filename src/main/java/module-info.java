module org.bsuir.letterlink {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires jakarta.mail;
    requires static lombok;
    requires slf4j.api;

    opens org.bsuir.letterlink to javafx.fxml;

    exports org.bsuir.letterlink;
    exports org.bsuir.letterlink.controllers;
    opens org.bsuir.letterlink.controllers to javafx.fxml;
    exports org.bsuir.letterlink.classes;
    opens org.bsuir.letterlink.classes to javafx.fxml;
    exports org.bsuir.letterlink.tempclasses;
    opens org.bsuir.letterlink.tempclasses to javafx.fxml;
    exports org.bsuir.letterlink.entities;
    opens org.bsuir.letterlink.entities to javafx.fxml;
}