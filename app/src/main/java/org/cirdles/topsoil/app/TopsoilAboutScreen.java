package org.cirdles.topsoil.app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.util.dialog.ErrorAlerter;

import java.awt.*;

/**
 * A controller for Topsoil's about screen, a small pop-up that appears when starting Topsoil. It contains About
 * information, as well as some helpful links to further CIRDLES resources. The {@link Stage} for this controller
 * doesn't have a border around it, so dragging is handled programmatically by
 * {@link #mousePressedAction(MouseEvent)} and {@link #mouseDraggedAction(MouseEvent)}.
 *
 * @author Jake Marotta
 */
public class TopsoilAboutScreen extends Pane {

    //***********************
    // Attributes
    //***********************

    /**
     * An {@code ImageView} for the Topsoil logo.
     */
    @FXML private ImageView topsoilLogo;

    /**
     * An {@code ImageView} for the CIRDLES logo.
     */
    @FXML private ImageView cirdlesLogo;

    /**
     * A {@code Hyperlink} that leads to the Topsoil GitHub repository.
     */
    @FXML private Hyperlink github;

    /**
     * A {@code Hyperlink} that leads to the CIRDLES home page.
     */
    @FXML private Hyperlink cirdles;

    /**
     * A {@code Hyperlink} that leads to the Topsoil GitHub release log.
     */
    @FXML private Hyperlink releaseLog;

    /**
     * The {@code String} URL for the Topsoil GitHub repository.
     */
    private final String GITHUB_URL = "https://github.com/CIRDLES/Topsoil";

    /**
     * The {@code String} URL for the CIRDLES home page.
     */
    private final String CIRDLES_URL = "https://cirdles.org/";

    /**
     * The {@code String} URL for the Topsoil GitHub release log.
     */
    private final String RELEASE_LOG_URL = "https://github.com/CIRDLES/Topsoil/releases";

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

    // ResourceExtractor
    /**
     * A custom extractor for extracting necessary resources. Used by CIRDLES projects.
     */
    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(TopsoilAboutScreen.class);

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @FXML public void initialize() {
        assert topsoilLogo != null : "fx:id=\"topsoilLogo\" was not injected: check your FXML file " +
                                    "'topsoil-about-screen.fxml'.";
        assert github != null : "fx:id=\"github\" was not injected: check your FXML file 'topsoil-about-screen.fxml'.";
        assert cirdles != null : "fx:id=\"cirdles\" was not injected: check your FXML file 'topsoil-about-screen.fxml'.";
        assert releaseLog != null : "fx:id=\"releaseLog\" was not injected: check your FXML file " +
                                    "'topsoil-about-screen.fxml'.";

        topsoilLogo.setImage(new Image(RESOURCE_EXTRACTOR.extractResourceAsPath("topsoil-logo-text.png").toUri().toString()));
        cirdlesLogo.setImage(new Image(RESOURCE_EXTRACTOR.extractResourceAsPath("cirdles-logo-yellow.png").toUri().toString()));

        browser = new DesktopWebBrowser(Desktop.getDesktop(), new ErrorAlerter());
    }

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
