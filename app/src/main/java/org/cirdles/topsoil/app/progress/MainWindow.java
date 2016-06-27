package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(new VBox(), 750, 750);

        // Menu Bar
        MainMenuBar menuBar = new MainMenuBar(scene);
        MainButtonsBar buttonBar = new MainButtonsBar();

        // Create Scene
        ((VBox) scene.getRoot()).getChildren().addAll(
                menuBar.getMenuBar(),
                buttonBar.getButtons()
        );

        // Display Scene
        primaryStage.setTitle("Topsoil Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
