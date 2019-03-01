package org.cirdles.topsoil.app;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.control.HomeView;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.app.style.StyleLoader;
import org.cirdles.topsoil.app.file.ProjectSerializer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

/**
 * @see Application
 * @see MainController
 */
public class Topsoil extends Application {

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
        Topsoil.controller = new MainController(primaryStage);
        Topsoil.controller.closeProjectView();

        Scene scene = new Scene(controller, 1200, 750);

        ResourceExtractor resourceExtractor = new ResourceExtractor(Topsoil.class);
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

    public static class MainController extends VBox {

        //**********************************************//
        //                  CONSTANTS                   //
        //**********************************************//

        private static final String CONTROLLER_FXML = "main-window.fxml";
        private static final String TOPSOIL_LOGO = "topsoil-logo.png";

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML
        private AnchorPane mainContentPane;
        private Image topsoilLogo;

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private Stage primaryStage;
        private RecentFiles recentFiles = new RecentFiles();

        //**********************************************//
        //                  PROPERTIES                  //
        //**********************************************//

        private BooleanProperty dataShowing = new SimpleBooleanProperty(false);
        public BooleanProperty dataShowingProperty() {
            return dataShowing;
        }
        public boolean isDataShowing() {
            return (mainContentPane.getChildren().get(0) instanceof ProjectView);
        }

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        MainController(Stage primaryStage) {
            this.primaryStage = primaryStage;
            // If a .topsoil file is open, the name of the file is appended to "Topsoil" at the top of the window
            this.primaryStage.titleProperty().bind(Bindings.createStringBinding(() -> {
                return ProjectSerializer.getCurrentProjectPath() != null
                        ? "Topsoil - " + ProjectSerializer.getCurrentProjectPath().getFileName().toString()
                        : "Topsoil";
            }, ProjectSerializer.currentProjectPathProperty()));
            this.primaryStage.setOnCloseRequest(event -> {
                event.consume();
                FileMenuHelper.exitTopsoilSafely();
            });

            try {
                final ResourceExtractor re = new ResourceExtractor(MainController.class);
                FXMLUtils.loadController(CONTROLLER_FXML, MainController.class, this);
                topsoilLogo = new Image(re.extractResourceAsPath(TOPSOIL_LOGO).toUri().toString());
                this.primaryStage.getIcons().add(topsoilLogo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void initialize() {

        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        public Node getMainContent() {
            return mainContentPane.getChildren().get(0);
        }

        public Node setProjectView(ProjectView projectView) {
            return replaceMainContent(projectView);
        }

        public Stage getPrimaryStage() {
            return primaryStage;
        }

        public void setPrimaryStage(Stage primaryStage) {
            this.primaryStage = primaryStage;
        }

        public void closeProjectView() {
            replaceMainContent(new HomeView());
        }

        public Image getTopsoilLogo() {
            return topsoilLogo;
        }

        public Path[] getRecentFiles() {
            return recentFiles.getRecentFiles();
        }

        public void addRecentFile(Path path) {
            recentFiles.addRecentFile(path);
        }

        public void clearRecentFiles() {
            recentFiles.clearRecentFiles();
        }

        //**********************************************//
        //               PRIVATE METHODS                //
        //**********************************************//

        private Node replaceMainContent(Node content) {
            Node rtnval = mainContentPane.getChildren().isEmpty() ? null : mainContentPane.getChildren().get(0);
            mainContentPane.getChildren().clear();
            mainContentPane.getChildren().add(content);
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            return rtnval;
        }
    }
}
