module com.example.tpoop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires org.kordamp.ikonli.core;

    requires langchain4j;
    requires langchain4j.core;

    requires org.slf4j;

    requires langchain4j.ollama;

    opens com.example.tpoop to javafx.fxml;
    exports com.example.tpoop;
}