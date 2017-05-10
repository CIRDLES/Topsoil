package org.cirdles.topsoil.app;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.menu.MainMenuBar;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;

/**
 * A controller class for Topsoil's {@link MainWindow}.
 *
 * @author Jake Marotta
 * @see MainWindow
 */
public class MainWindowController {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code VBox} that contains both the {@link TopsoilTabPane} and {@link MainMenuBar} for the
     * {@code MainWindow}.
     */
    @FXML private VBox container;   // tabs and menuBar are children of container

    /**
     * A {@code TopsoilTabPane} that holds all {@code Tab}s open in Topsoil.
     */
    @FXML private TabPane tabs;

    /**
     * The {@code MenuBar} for the {@link MainWindow}.
     */
    @FXML private MenuBar menuBar;

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    public void initialize() {
        assert tabs != null : "fx:id=\"tabs\" was not injected: check your FXML file 'main-window.fxml'.";
        assert menuBar != null : "fx:id=\"mainMenuBar\" was not injected: check your FXML file 'main-window.fxml'.";

        tabs = new TopsoilTabPane();
        VBox.setVgrow(tabs, Priority.ALWAYS);
        tabs.setId("TopsoilTabPane");

        menuBar = new MainMenuBar((TopsoilTabPane) tabs).getMenuBar();
        menuBar.setId("MenuBar");

        container.getChildren().setAll(menuBar, tabs);
        container.setStyle("-fx-background-color: lightgrey");
        ((TopsoilTabPane) tabs).isEmptyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                container.setStyle("-fx-background-color: lightgrey");
            } else {
                container.setStyle("-fx-background-color: whitesmoke");
            }
        });
    }

    /**
     * Returns the {@code TopsoilTabPane} associated with this window.
     *
     * @return  the TopsoilTabPane associated with this window
     */
    TopsoilTabPane getTabPane() {
        return (TopsoilTabPane) tabs;
    }
}
