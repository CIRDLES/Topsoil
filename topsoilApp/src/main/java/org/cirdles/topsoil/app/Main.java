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
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String ARIMO_FONT = "style/font/arimo/Arimo-Regular.ttf";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static MainController controller;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.controller = new MainController(primaryStage);
        Scene scene = new Scene(controller, 1200, 750);

        ResourceExtractor resourceExtractor = new ResourceExtractor(Main.class);
        // Load font
        try {
            Font.loadFont(resourceExtractor.extractResourceAsFile(ARIMO_FONT).toURI().toURL().toExternalForm(), 14);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StyleLoader styleLoader = new StyleLoader();
        scene.getStylesheets().addAll(styleLoader.getStylesheets());
        StyleManager.getInstance().setUserAgentStylesheets(styleLoader.getStylesheets());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static MainController getController() {
        return controller;
    }

    /**
     * Exits the application.
     */
    public static void shutdown() {
        List<Stage> stages = StageHelper.getStages();
        for (int index = stages.size() - 1; index > 0; index--) {
            stages.get(index).close();
        }
        Platform.exit();
    }

}
