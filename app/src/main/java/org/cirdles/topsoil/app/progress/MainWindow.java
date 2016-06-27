package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(new VBox(), 750, 750);

        MainMenuBar menuBar = new MainMenuBar(scene);
        MainButtonsBar buttonBar = new MainButtonsBar();

        TabPane tabs = new TabPane();
        Button addButton = new Button("+");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Tab tab = new Tab("Tab " + (tabs.getTabs().size() + 1));
                tabs.getTabs().add(tab);
                tabs.getSelectionModel().select(tab);
            }
        });

        // Create Scene
        ((VBox) scene.getRoot()).getChildren().addAll(
                menuBar.getMenuBar(),
                buttonBar.getButtons(),
                tabs,
                addButton
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
