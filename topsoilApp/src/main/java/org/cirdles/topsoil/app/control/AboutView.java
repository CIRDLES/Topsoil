package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;

import java.awt.*;
import java.io.IOException;

/**
 * @author marottajb
 */
public class AboutView extends VBox {

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

    private static final String CONTROLLER_FXML = "about-view.fxml";
    private static final String LOGO = "topsoil-logo-text.png";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private ImageView topsoilLogo, cirdlesLogo;

    @FXML private Label versionLabel, messageLabel;

    @FXML private Hyperlink homePage, license, github, cirdles, releaseLog;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ResourceExtractor resourceExtractor = new ResourceExtractor(AboutView.class);

    /**
     * The system default browser as a {@code DesktopWebBrowser}. This is used to navigate to the addresses for each
     * {@code Hyperlink}.
     */
    private DesktopWebBrowser browser;

    /**
     * A {@code double} value that represents the offset between the x position of the parent {@code Stage} and this
     * one. Used for programmatically changing the position of this window when it is dragged.
     */
    double xOffset;

    /**
     * A {@code double} value that represents the offset between the y position of the parent {@code Stage} and this
     * one. Used for programmatically changing the position of this window when it is dragged.
     */
    double yOffset;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public AboutView() {
        super();
        browser = new DesktopWebBrowser(Desktop.getDesktop());
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, AboutView.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc}
     */
    @FXML protected void initialize() {
        topsoilLogo.setImage(new Image(resourceExtractor.extractResourceAsPath(LOGO).toUri().toString()));
        versionLabel.setText("Version " + (new TopsoilMetadata()).getVersion().split("-")[0]);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Stage getFloatingStage() {
        AboutView aboutView = new AboutView();
        aboutView.setStyle("-fx-border-color: #67ccff; -fx-border-width: 0.5em;");
//        aboutView.setOnMousePressed(event -> {
//            aboutView.xOffset = Topsoil.getPrimaryStage().getX() - event.getScreenX();
//            aboutView.yOffset = Topsoil.getPrimaryStage().getScene().getWindow().getY() - event.getScreenY();
//        });
//        aboutView.setOnMouseDragged(event -> {
//            aboutView.getScene().getWindow().setX(event.getScreenX() + aboutView.xOffset);
//            aboutView.getScene().getWindow().setY(event.getScreenY() + aboutView.yOffset);
//        });
        Scene scene = new Scene(aboutView, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);

        Stage primaryStage = Topsoil.getController().getPrimaryStage();

        double newX = primaryStage.getX() + (primaryStage.getWidth() / 2) -
                      (DEFAULT_WIDTH / 2);
        double newY = primaryStage.getY() + (primaryStage.getHeight() / 2) -
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

}
