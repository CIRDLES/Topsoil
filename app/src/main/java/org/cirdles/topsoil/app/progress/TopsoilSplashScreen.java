package org.cirdles.topsoil.app.progress;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.awt.*;

/**
 * @author marottajb
 */
public class TopsoilSplashScreen extends Pane {

    // Container
    @FXML private Pane containerPane;

    // Close Button
    @FXML private Button closeButton;

    // Logos
    @FXML private ImageView topsoilLogo;
    @FXML private ImageView cirdlesLogo;

    // Links
    @FXML private Hyperlink github, cirdles, releaseLog;
    private final String GITHUB_URL = "https://github.com/CIRDLES/Topsoil";
    private final String CIRDLES_URL = "https://cirdles.org/";
    private final String RELEASE_LOG_URL = "https://github.com/CIRDLES/Topsoil/releases";

    // System default browser
    private DesktopWebBrowser browser;

    // Window offsets
    private double xOffset;
    private double yOffset;

    // ResourceExtractor
    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(TopsoilSplashScreen.class);

    @FXML
    public void initialize() {
        assert topsoilLogo != null : "fx:id=\"topsoilLogo\" was not injected: check your FXML file " +
                                    "'topsoil-splash-screen.fxml'.";
        assert github != null : "fx:id=\"github\" was not injected: check your FXML file 'topsoil-splash-screen.fxml'.";
        assert cirdles != null : "fx:id=\"cirdles\" was not injected: check your FXML file 'topsoil-splash-screen.fxml'.";
        assert releaseLog != null : "fx:id=\"releaseLog\" was not injected: check your FXML file 'topsoilSplashScreen" +
                                    ".fxml'.";

        topsoilLogo.setImage(new Image(RESOURCE_EXTRACTOR.extractResourceAsPath("topsoil-logo-text.png").toUri().toString()));
        cirdlesLogo.setImage(new Image(RESOURCE_EXTRACTOR.extractResourceAsPath("cirdles-logo-yellow.png").toUri().toString()));

        browser = new DesktopWebBrowser(Desktop.getDesktop(), new ErrorAlerter());
    }

    @FXML private void closeButtonAction() {
        ((Stage) this.closeButton.getScene().getWindow()).close();
    }

    @FXML private void openGitHub() {
        browser.browse(GITHUB_URL);
    }

    @FXML private void openCIRDLES() {
        browser.browse(CIRDLES_URL);
    }

    @FXML private void openReleaseLog() {
        browser.browse(RELEASE_LOG_URL);
    }

    @FXML private void mousePressedEvent(MouseEvent event) {
        xOffset = containerPane.getScene().getWindow().getX() - event.getScreenX();
        yOffset = containerPane.getScene().getWindow().getY() - event.getScreenY();
    }

    @FXML private void mouseDraggedEvent(MouseEvent event) {
        containerPane.getScene().getWindow().setX(event.getScreenX() + xOffset);
        containerPane.getScene().getWindow().setY(event.getScreenY() + yOffset);
    }
}
