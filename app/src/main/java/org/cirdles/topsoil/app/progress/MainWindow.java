package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Menu Bar
        MainMenuBar menuBar = new MainMenuBar();
        MainButtonsBar buttonBar = new MainButtonsBar();

        // Create Scene
        Scene scene = new Scene(new VBox(), 750, 750);
        ((VBox) scene.getRoot()).getChildren().addAll(
                menuBar.getMenuBar(),
                buttonBar.getButtons(),
                new TableView<DataEntry>());

        // Display Scene
        primaryStage.setTitle("Topsoil Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
