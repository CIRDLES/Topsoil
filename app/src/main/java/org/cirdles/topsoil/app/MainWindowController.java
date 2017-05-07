package org.cirdles.topsoil.app;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.menu.MainMenuBar;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;

public class MainWindowController {

    @FXML private VBox container;
    @FXML private TopsoilTabPane tabs;
    private MenuBar menuBar;

    public void initialize() {
        assert tabs != null : "fx:id=\"tabs\" was not injected: check your FXML file 'main-window.fxml'.";
        assert menuBar != null : "fx:id=\"mainMenuBar\" was not injected: check your FXML file 'main-window.fxml'.";


        menuBar = new MainMenuBar(tabs).getMenuBar();
        menuBar.setId("MenuBar");

        tabs.setId("TopsoilTabPane");

        container.getChildren().setAll(menuBar, tabs);
        container.setStyle("-fx-background-color: lightgrey");
        tabs.isEmptyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                container.setStyle("-fx-background-color: lightgrey");
            } else {
                container.setStyle("-fx-background-color: whitesmoke");
            }
        });
    }

    TopsoilTabPane getTabPane() {
        return tabs;
    }
}
