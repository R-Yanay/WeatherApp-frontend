package com.wettergui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Einstiegspunkt fuer die Wetter-App GUI.
 * Ruft das Hauptfenster auf.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        WeatherAppGUI gui = new WeatherAppGUI(stage);
        gui.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
