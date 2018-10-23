package org.cirdles.topsoil.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;

import java.awt.*;
import java.io.IOException;

/**
 * A controller for Topsoil's about screen, a small pop-up that appears when starting Topsoil. It contains About
 * information, as well as some helpful links to further CIRDLES resources. The {@link Stage} for this controller
 * doesn't have a border around it, so dragging is handled programmatically by
 * {@link #mousePressedAction(MouseEvent)} and {@link #mouseDraggedAction(MouseEvent)}.
 *
 * @author Jake Marotta
 */
public class TopsoilAboutScreen extends VBox {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final double DEFAULT_WIDTH = 550;
    private static final double DEFAULT_HEIGHT = 650;

    private static final String HOME_URL = "http://cirdles.org/projects/topsoil/";
    private static final String LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0";
    private static final String GITHUB_URL = "https://github.com/CIRDLES/Topsoil";
    private static final String CIRDLES_URL = "https://cirdles.org/";
    private static final String RELEASE_LOG_URL = "https://github.com/CIRDLES/Topsoil/releases";

    private static final String CONTROLLER_FXML = "topsoil-about-screen.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private ImageView topsoilLogo, cirdlesLogo;

    @FXML private Label versionLabel, messageLabel;

    @FXML private Hyperlink homePage, license, github, cirdles, releaseLog;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ResourceExtractor resourceExtractor = new ResourceExtractor(TopsoilAboutScreen.class);

    /**
     * The system default browser as a {@code DesktopWebBrowser}. This is used to navigate to the addresses for each
     * {@code Hyperlink}.
     */
    private DesktopWebBrowser browser;

    /**
     * A {@code double} value that represents the offset between the x position of the parent {@code Stage} and this
     * one. Used for programmatically changing the position of this window when it is dragged.
     */
    private double xOffset;

    /**
     * A {@code double} value that represents the offset between the y position of the parent {@code Stage} and this
     * one. Used for programmatically changing the position of this window when it is dragged.
     */
    private double yOffset;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilAboutScreen() {
        super();

        browser = new DesktopWebBrowser(Desktop.getDesktop());

        try {
            FXMLLoader loader = new FXMLLoader(resourceExtractor.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    /** {@inheritDoc}
     */
    @FXML protected void initialize() {
        topsoilLogo.setImage(new Image(resourceExtractor.extractResourceAsPath("topsoil-logo-text.png").toUri().toString()));
        cirdlesLogo.setImage(new Image(resourceExtractor.extractResourceAsPath("cirdles-logo-yellow.png").toUri().toString()));

        versionLabel.setText("Version " + (new TopsoilMetadata()).getVersion().split("-")[0]);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Stage getFloatingStage() {
        Scene scene = new Scene(new TopsoilAboutScreen(), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);

        double newX = MainWindow.getPrimaryStage().getX() + (MainWindow.getPrimaryStage().getWidth() / 2) -
                      (DEFAULT_WIDTH / 2);
        double newY = MainWindow.getPrimaryStage().getY() + (MainWindow.getPrimaryStage().getHeight() / 2) -
                      (DEFAULT_HEIGHT / 2);

        stage.setX(newX);
        stage.setY(newY);

        return stage;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    @FXML private void openHome() { browser.browse(HOME_URL); }

    /**
     * Opens the system default browser to Topsoil's GitHub repository.
     */
    @FXML private void openGitHub() {
        browser.browse(GITHUB_URL);
    }

    /**
     * Opens the system default browser to CIRDLES' website.
     */
    @FXML private void openCIRDLES() {
        browser.browse(CIRDLES_URL);
    }

    /**
     * Opens the system default browser to Topsoil's GitHub Release Log.
     */
    @FXML private void openReleaseLog() {
        browser.browse(RELEASE_LOG_URL);
    }

    /**
     * Opens the system default browser to the info page for Apache License 2.0.
     */
    @FXML private void openLicensePage() {
        browser.browse(LICENSE_URL);
    }

    /**
     * Sets the xOffset and yOffset of the window when the mouse is pressed on it.
     *
     * @param event a MouseEvent that triggers this action
     */
    @FXML private void mousePressedAction(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            xOffset = topsoilLogo.getScene().getWindow().getX() - event.getScreenX();
            yOffset = topsoilLogo.getScene().getWindow().getY() - event.getScreenY();
        }
    }

    /**
     * Changes the position of the window when the mouse is dragged on it, effectively creating a click-and-drag window.
     *
     * @param event a MouseEvent that triggers this action
     */
    @FXML private void mouseDraggedAction(MouseEvent event) {
        if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            topsoilLogo.getScene().getWindow().setX(event.getScreenX() + xOffset);
            topsoilLogo.getScene().getWindow().setY(event.getScreenY() + yOffset);
        }
    }
}
