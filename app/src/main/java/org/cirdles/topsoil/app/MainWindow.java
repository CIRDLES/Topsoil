package org.cirdles.topsoil.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.menu.MenuItemEventHandler;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main class of Topsoil.
 *
 * @see Application
 * @see MainWindowController
 */
public class MainWindow extends Application {

    //***********************
    // Attributes
    //***********************

    /**
     * A {@code ResourceExtractor} for extracting necessary resources. Used by CIRDLES projects.
     */
    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(MainWindow.class);

    /**
     * The {@code String} path to the {@code .fxml} file for {@link MainWindowController}.
     */
    private final String TOPSOIL_MAIN_WINDOW_FXML_PATH = "main-window.fxml";

    /**
     * The {@code String} path to the {@code .fxml} file for {@link TopsoilAboutScreen}.
     */
    private final String TOPSOIL_ABOUT_SCREEN_FXML_PATH = "topsoil-about-screen.fxml";

    /**
     * The {@code String} path to the {@code .css} file for Topsoil.
     */
    private final String TOPSOIL_CSS_FILE_PATH = "topsoil-stylesheet.css";

    /**
     * The {@code String} path to the Topsoil logo.
     */
    private final String TOPSOIL_LOGO_FILE_PATH = "topsoil-logo.png";

    private static Stage primaryStage;
    private static Image windowIcon;
    private static String OS;

    private static final String WINDOWS = "Windows";
    private static final String MAC = "Mac";
    private static final String LINUX = "Linux";

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {

        setPrimaryStage(primaryStage);

        // Detect OS
        String OSName = System.getProperty("os.name").toLowerCase();

        if (OSName.contains("windows")) {
            setOS(WINDOWS);
        } else if (OSName.contains("mac") || OSName.contains("os x") || OSName.contains("macos")) {
            setOS(MAC);
        } else if (OSName.contains("nix") || OSName.contains("nux") || OSName.contains("aix")) {
            setOS(LINUX);
        }

        try {
            Parent mainWindow;
            MainWindowController mainWindowController;

            // Load FXML for MainWindowController
            try {
                FXMLLoader mainFXMLLoader = new FXMLLoader(
                        RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_MAIN_WINDOW_FXML_PATH).toUri().toURL());
                mainWindow = mainFXMLLoader.load();
                mainWindowController = mainFXMLLoader.getController();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                throw new LoadException("Could not load " + TOPSOIL_MAIN_WINDOW_FXML_PATH);
            }

            // Create main Scene
            Scene scene = new Scene(mainWindow, 850, 900);

            // Load CSS
            try {
                String css = RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_CSS_FILE_PATH).toUri().toURL()
                                               .toExternalForm();
                scene.getStylesheets().add(css);
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
                                Platform.exit();
                            }
                        // If user doesn't want to save
                        } else {
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
                Image icon = new Image(RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_LOGO_FILE_PATH)
                                                         .toUri().toString());
                primaryStage.getIcons().add(icon);
                setWindowIcon(icon);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Set minimum window dimensions
            primaryStage.setMinHeight(400.0);
            primaryStage.setMinWidth(650.0);

            // If a .topsoil file is open, the name of the file is appended to "Topsoil" at the top of the window
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
                Parent splashScreen = FXMLLoader.load(RESOURCE_EXTRACTOR.extractResourceAsPath(TOPSOIL_ABOUT_SCREEN_FXML_PATH).toUri().toURL());
                Scene splashScene = new Scene(splashScreen, 550, 650);
                Stage splashWindow = new Stage(StageStyle.UNDECORATED);
                splashWindow.setResizable(false);
                splashWindow.setScene(splashScene);
                
                //splashWindow.setX(primaryStage.getX());
                //splashWindow.setY(primaryStage.getY());

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
                initializeTableKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
                initializeUndoKeyboardShortcuts(keyEvent, mainWindowController.getTabPane());
                keyEvent.consume();
            });
        } catch (LoadException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes shortcuts for managing tables.
     *
     * @param keyEvent  an occurring KeyEvent
     * @param tabs  the TopsoilTabPane where the tables are located
     */
    private static void initializeTableKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
        // shortcut + T creates a new tab containing an empty table
        if (keyEvent.getCode() == KeyCode.T &&
            keyEvent.isShortcutDown()) {
            TopsoilDataTable table = MenuItemEventHandler.handleNewTable();
            tabs.add(table);
        }
        // shortcut + I imports a new table from a file
        if (keyEvent.getCode() == KeyCode.I &&
                keyEvent.isShortcutDown()) {
            try {
                TopsoilDataTable table = MenuItemEventHandler.handleTableFromFile();
                tabs.add(table);
            } catch (IOException e) {
                TopsoilNotification.showNotification(
                        TopsoilNotification.NotificationType.ERROR,
                        "Error",
                        "File I/O Error."
                );
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes shortcuts for managing undo and redo.
     *
     * @param keyEvent  an occurring KeyEvent
     * @param tabs  the TopsoilTabPane where the tables are located
     */
    private static void initializeUndoKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
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

    public static void setOS(String OS) {
        MainWindow.OS = OS;
    }

    public static Image getWindowIcon() {
        return windowIcon;
    }

    private static void setWindowIcon(Image image) {
        windowIcon = image;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /** {@inheritDoc}
     */
    public static void main(String[] args) {
        launch(args);
    }

}
