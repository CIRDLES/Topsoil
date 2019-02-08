package org.cirdles.topsoil.app;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.style.StyleLoader;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see Application
 * @see MainController
 */
public class Main extends Application {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    static Stage primaryStage;
    private static MainController controller;

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String ARIMO_FONT = "style/font/arimo/Arimo-Regular.ttf";
    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void start(Stage primary) {

        ResourceExtractor resourceExtractor = new ResourceExtractor(Main.class);

        Main.primaryStage = primary;
        Main.controller = new MainController();

        // Create main Scene
        Scene scene = new Scene(controller, 1200, 750);

        // Load CSS
        try {
            Font.loadFont(resourceExtractor.extractResourceAsFile(ARIMO_FONT).toURI().toURL().toExternalForm(), 14);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StyleLoader styleLoader = new StyleLoader();
        scene.getStylesheets().addAll(styleLoader.getStylesheets());
        StyleManager.getInstance().setUserAgentStylesheets(styleLoader.getStylesheets());
        primaryStage.setScene(scene);

        // If main window is closed, all other windows close.
        configureCloseRequest(primaryStage);

        // If a .topsoil file is open, the name of the file is appended to "Topsoil" at the top of the window
        primaryStage.titleProperty().bind(Bindings.createStringBinding(() -> {
            return ProjectSerializer.getCurrentProjectPath() != null
                    ? "Topsoil - " + ProjectSerializer.getCurrentProjectPath().getFileName().toString()
                    : "Topsoil";
        }, ProjectSerializer.currentProjectPathProperty()));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static MainController getController() {
        return controller;
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

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void shutdown() {
        List<Stage> stages = StageHelper.getStages();
        for (int index = stages.size() - 1; index > 0; index--) {
            stages.get(index).close();
        }
        Platform.exit();
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private static void configureCloseRequest(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();
            FileMenuHelper.exitTopsoilSafely();
        });
    }

}
