package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.serialization.TopsoilSerializer;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.io.IOException;
import java.net.MalformedURLException;

public class MainWindow extends Application {

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(MainWindow.class);
    private final String TOPSOIL_MAIN_WINDOW_FXML_PATH = "main-window.fxml";
    private final String TOPSOIL_SPLASH_SCREEN_FXML_PATH = "topsoil-splash-screen.fxml";
    private final String TOPSOIL_CSS_FILE_PATH = "topsoil-stylesheet.css";
    private final String TOPSOIL_LOGO_FILE_PATH = "topsoil-logo.png";

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent mainWindow;
            MainWindowController mainWindowController;

            // Load FXML for MainWindowController
            try {
                FXMLLoader mainFXMLLoader = new FXMLLoader(
                        RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_MAIN_WINDOW_FXML_PATH).toUri().toURL());
                mainWindow = mainFXMLLoader.load();
                mainWindowController = mainFXMLLoader.getController();
            } catch (IOException|NullPointerException e) {
                e.printStackTrace();
                throw new LoadException("Could not load " + TOPSOIL_MAIN_WINDOW_FXML_PATH);
            }

            Scene scene = new Scene(mainWindow, 750, 750);

            // Load CSS
            try {
                String css = RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_CSS_FILE_PATH).toUri().toURL()
                                               .toExternalForm();
                scene.getStylesheets().add(css);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.err.printf("Could not load CSS.\n");
            }

            // If main window is closed, all other windows close.
            primaryStage.setOnCloseRequest(event -> verifyWindowClose(event, mainWindowController.getTabPane()));

            // Load logo for use in window and system task bar
            try {
                // TODO ResourceExtractor
                primaryStage.getIcons().add(new Image(RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_LOGO_FILE_PATH)
                                                                        .toUri().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            primaryStage.setMinHeight(400.0);
            primaryStage.setMinWidth(650.0);

            primaryStage.setTitle("Topsoil");
            TopsoilSerializer.currentProjectFileProperty().addListener(c -> {
                if (TopsoilSerializer.projectFileExists()) {
                    primaryStage.setTitle("Topsoil - " + TopsoilSerializer.getCurrentProjectFile().getName());
                } else {
                    primaryStage.setTitle("Topsoil");
                }
            });

            primaryStage.setScene(scene);
            primaryStage.show();

            // TODO Move to MainWindowController
            // Load splash screen
            try {
                Parent splashScreen = FXMLLoader.load(
                        RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_SPLASH_SCREEN_FXML_PATH).toUri().toURL());
                Scene splashScene = new Scene(splashScreen, 450, 600);
                Stage splashWindow = new Stage(StageStyle.UNDECORATED);
                splashWindow.setResizable(false);
                splashWindow.setScene(splashScene);

                splashWindow.requestFocus();
                splashWindow.initOwner(primaryStage);
                splashWindow.initModality(Modality.NONE);
                // Close window if main window gains focus.
                primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        splashWindow.close();
                    }
                });
                splashWindow.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Handle Keyboard Shortcuts
            scene.setOnKeyPressed(keyEvent -> {
                setTableKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
                setUndoKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
                keyEvent.consume();
            });
        } catch (LoadException e) {
            e.printStackTrace();
        }
    }

    private static void setTableKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
        // shortcut + T creates a new tab containing an empty table
        if (keyEvent.getCode() == KeyCode.T &&
                keyEvent.isShortcutDown()) {
            TopsoilTable table = MenuItemEventHandler.handleNewTable();
            tabs.add(table);
        }
        // shortcut + I imports a new table from a file
        if (keyEvent.getCode() == KeyCode.I &&
                keyEvent.isShortcutDown()) {
            try {
                TopsoilTable table = MenuItemEventHandler.handleTableFromFile();
                tabs.add(table);
            } catch (IOException e) {
                Alerter alerter = new ErrorAlerter();
                alerter.alert("File I/O Error.");
                e.printStackTrace();
            }
        }
    }

    private static void setUndoKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
        // shortcut + Z undoes the last undoable action
        if (keyEvent.getCode() == KeyCode.Z &&
                keyEvent.isShortcutDown() &&
                !tabs.isEmpty()) {
            tabs.getSelectedTab().undo();
        }
        // shortcut + Y redoes the last undone action
        if (keyEvent.getCode() == KeyCode.Y &&
                keyEvent.isShortcutDown() &&
                !tabs.isEmpty()) {
            tabs.getSelectedTab().redo();
        }
    }

    private static void verifyWindowClose(Event event, TopsoilTabPane tabs) {
        if (!tabs.isEmpty()) {
            Alert verification = new Alert(Alert.AlertType.CONFIRMATION,
                    "Would you like to save your work?",
                    ButtonType.CANCEL,
                    ButtonType.NO,
                    ButtonType.YES);
            verification.showAndWait().ifPresent(response -> {
                if (response == ButtonType.CANCEL) {
                    event.consume();
                } else {
                    if (response == ButtonType.YES) {
                        MenuItemEventHandler.handleSaveAsProjectFile(tabs);
                    }
                    Platform.exit();
                }
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
