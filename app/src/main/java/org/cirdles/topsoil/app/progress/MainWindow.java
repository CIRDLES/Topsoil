package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.io.IOException;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(new VBox(), 750, 750);

        TopsoilTabPane tabs = new TopsoilTabPane();
        MainMenuBar menuBar = new MainMenuBar(tabs);
        MainButtonsBar buttonBar = new MainButtonsBar(tabs);

        // Create Scene
        ((VBox) scene.getRoot()).getChildren().addAll(
                menuBar.getMenuBar(),
                buttonBar.getButtons(),
                tabs
        );

        // Display Scene
        primaryStage.setTitle("Topsoil Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Handle Keyboard Shortcuts
        scene.setOnKeyPressed(keyevent -> {

            // shortcut + T creates a new tab
            if (keyevent.getCode() == KeyCode.T &&
                    keyevent.isShortcutDown()) {
                TopsoilTable table = MenuItemEventHandler.handleNewTable();
                tabs.add(table);
            }
            // shortcut + I imports a new table from a file
            if (keyevent.getCode() == KeyCode.I &&
                    keyevent.isShortcutDown()) {
                try {
                    TopsoilTable table = MenuItemEventHandler.handleTableFromFile();
                    tabs.add(table);
                } catch (IOException e) {
                    Alerter alerter = new ErrorAlerter();
                    alerter.alert("File I/O Error.");
                    e.printStackTrace();
                }
            }

            keyevent.consume();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
