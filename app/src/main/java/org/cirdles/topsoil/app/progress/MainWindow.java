package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    }

    public static void main(String[] args) {
        launch(args);
    }

}
