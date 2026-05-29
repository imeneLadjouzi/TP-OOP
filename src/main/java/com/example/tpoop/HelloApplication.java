package com.example.tpoop;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        MainController mc = new MainController(stage);
        mc.show();
    }
}