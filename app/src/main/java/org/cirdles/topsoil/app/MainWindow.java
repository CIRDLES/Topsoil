package org.cirdles.topsoil.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.menu.MenuItemEventHandler;
import org.cirdles.topsoil.app.tab.TopsoilTab;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.ObservableTableData;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see Application
 * @see MainWindowController
 */
public class MainWindow extends Application {

    /**
     * The {@code String} path to the {@code .fxml} file for {@link MainWindowController}.
     */
    private static final String MAIN_WINDOW_FXML = "main-window.fxml";
    /**
     * The {@code String} path to the {@code .css} file for Topsoil.
     */
    private static final String TOPSOIL_CSS = "topsoil-stylesheet.css";
    /**
     * The {@code String} path to the Topsoil logo.
     */
    private static final String TOPSOIL_LOGO = "topsoil-logo.png";

    private static final String WINDOWS = "Windows";
    private static final String MAC = "Mac";
    private static final String LINUX = "Linux";

    /**
     * A {@code ResourceExtractor} for extracting necessary resources. Used by CIRDLES projects.
     */
    private static final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(MainWindow.class);

    private static Stage primaryStage;
    private static Image windowIcon;
    private static String OS;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {

        MainWindow.primaryStage = primaryStage;

        // Detect OS
        String OSName = System.getProperty("os.name").toLowerCase();
        if (OSName.contains("windows")) {
            MainWindow.OS = WINDOWS;
        } else if (OSName.contains("mac") || OSName.contains("os x") || OSName.contains("macos")) {
            MainWindow.OS = MAC;
        } else if (OSName.contains("nix") || OSName.contains("nux") || OSName.contains("aix")) {
            MainWindow.OS = LINUX;
        }

        Parent mainWindow;
        MainWindowController mainWindowController;

        // Load FXML for MainWindowController
        try {
            FXMLLoader loader = new FXMLLoader(
                    RESOURCE_EXTRACTOR.extractResourceAsPath(MAIN_WINDOW_FXML).toUri().toURL());
            mainWindow = loader.load();
            mainWindowController = loader.getController();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Could not load " + MAIN_WINDOW_FXML, e);
        }

        // Create main Scene
        Scene scene = new Scene(mainWindow, 1100, 800);
        primaryStage.setScene(scene);

        // Load CSS
        try {
            String css = RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_CSS).toUri().toURL().toExternalForm();
            scene.getStylesheets().add(css);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not load " + TOPSOIL_CSS, e);
        }

        // If main window is closed, all other windows close.
        primaryStage.setOnCloseRequest(event -> {

            event.consume();
            // If something is open
            if (!mainWindowController.getTabPane().isEmpty()) {
                Boolean save = verifyFinalSave();
                // If save verification was not cancelled
                if (save != null) {
                    if (save) {
                        // If file was successfully saved
                        if (MenuItemEventHandler.handleSaveAsProjectFile(mainWindowController.getTabPane())) {
                            //Close all open plots
                            for(TopsoilTab tab : mainWindowController.getTabPane().getTopsoilTabs()) {
                                tab.closeTabPlots();
                            }
                            Platform.exit();
                        }
                    // If user doesn't want to save
                    } else {
                        for(TopsoilTab tab : mainWindowController.getTabPane().getTopsoilTabs()) {
                            tab.closeTabPlots();
                        }
                        Platform.exit();
                    }
                }

            // If nothing is open.
            } else {
                Platform.exit();
            }
        });

        // Load logo for use in window and system task bar
        try {
            Image icon = new Image(RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_LOGO)
                                                     .toUri().toString());
            primaryStage.getIcons().add(icon);
            MainWindow.windowIcon = icon;

        } catch (Exception e) {
            throw new RuntimeException("Could not load " + TOPSOIL_LOGO, e);
        }

        // Set minimum window dimensions
        primaryStage.setMinHeight(400.0);
        primaryStage.setMinWidth(650.0);

        // If a .topsoil file is open, the name of the file is appended to "Topsoil" at the top of the window
        primaryStage.titleProperty().bind(Bindings.createStringBinding(() -> {
            return TopsoilSerializer.projectFileExists()
                    ? "Topsoil - " + TopsoilSerializer.getCurrentProjectFile().getName()
                    : "Topsoil";
        }, TopsoilSerializer.currentProjectFileProperty()));

        primaryStage.show();

        // Load about screen
        Stage aboutWindow = TopsoilAboutScreen.getFloatingStage();

        aboutWindow.requestFocus();
        aboutWindow.initOwner(primaryStage);
        aboutWindow.initModality(Modality.NONE);
        // Close window if main window gains focus.
        primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                aboutWindow.close();
            }
        });
        aboutWindow.show();

        // Handle Keyboard Shortcuts
        scene.setOnKeyPressed(keyEvent -> {
            initializeTableKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
            initializeUndoKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
            initializeCopyPasteKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
            keyEvent.consume();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Asks the user whether they want to save their work, typically when exiting Topsoil.
     *
     * @return true if saving, false if not, null if cancelled
     */
    public static Boolean verifyFinalSave() {
        final AtomicReference<Boolean> reference = new AtomicReference<>(null);

        TopsoilNotification.showNotification(
                TopsoilNotification.NotificationType.YES_NO,
                "Save Changes",
                "Would you like to save your work?"
        ).ifPresent(response -> {
            if (response == ButtonType.YES) {
                reference.set(true);
            } else if (response == ButtonType.NO) {
                reference.set(false);
            }
        });

        return reference.get();
    }

    public static String getOS() {
        return OS;
    }

    public static Image getWindowIcon() {
        return windowIcon;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private static void initializeTableKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
        // shortcut + T creates a new tab containing an empty table
        if (keyEvent.getCode() == KeyCode.T &&
            keyEvent.isShortcutDown()) {
            ObservableTableData table = MenuItemEventHandler.handleNewTable();
            tabs.add(table);
        }
        // shortcut + I imports a new table from a file
        if (keyEvent.getCode() == KeyCode.I &&
            keyEvent.isShortcutDown()) {

            ObservableTableData table = MenuItemEventHandler.importDataFromFile();
            tabs.add(table);
        }
    }

    private static void initializeUndoKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
	    if ( ! tabs.isEmpty() ) {
		    // shortcut + Z undoes the last undoable action
		    if ( keyEvent.getCode() == KeyCode.Z && keyEvent.isShortcutDown() ) {
			    tabs.getSelectedTab().undo();
		    }
		    // shortcut + Y redoes the last undone action
		    if ( keyEvent.getCode() == KeyCode.Y && keyEvent.isShortcutDown() ) {
			    tabs.getSelectedTab().redo();
		    }
	    }
    }

    private static void initializeCopyPasteKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
    	if ( ! tabs.isEmpty() ) {
		    // shortcut + C copies a selection from the clipboard
    		if ( keyEvent.getCode() == KeyCode.C && keyEvent.isShortcutDown() ) {
    			tabs.getSelectedTab().getDataView().getSpreadsheetView().copyClipboard();
		    }
		    // shortcut + V pastes content to the clipboard
		    if ( keyEvent.getCode() == KeyCode.V && keyEvent.isShortcutDown() ) {
    			tabs.getSelectedTab().getDataView().getSpreadsheetView().pasteClipboard();
		    }
	    }
    }

}
