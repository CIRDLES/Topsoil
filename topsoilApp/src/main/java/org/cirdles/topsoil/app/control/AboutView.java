package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.util.ResourceBundles;

import java.awt.*;
import java.io.IOException;
import java.util.ResourceBundle;

import static org.cirdles.topsoil.app.util.ResourceBundles.MAIN;

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

    @FXML private Label versionLabel, aboutLabel, messageLabel, subMessageLabel, linksLabel;

    @FXML private Hyperlink homePage, license, github, cirdles, releaseLog;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ResourceExtractor resourceExtractor = new ResourceExtractor(AboutView.class);
    private DesktopWebBrowser browser;

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

    @FXML protected void initialize() {
        ResourceBundle bundle = ResourceBundles.MAIN.getBundle();
        versionLabel.setText(bundle.getString("appVersion"));
        aboutLabel.setText(bundle.getString("aboutLabel"));
        messageLabel.setText(bundle.getString("aboutMessage"));
        subMessageLabel.setText(bundle.getString("aboutSubMessage"));
        linksLabel.setText(bundle.getString("linksLabel"));
        homePage.setText(bundle.getString("homePageLink"));
        license.setText(bundle.getString("licenseLink"));
        github.setText(bundle.getString("githubLink"));
        cirdles.setText(bundle.getString("cirdlesLink"));
        releaseLog.setText(bundle.getString("releaseLogLink"));

        topsoilLogo.setImage(new Image(resourceExtractor.extractResourceAsPath(LOGO).toUri().toString()));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Opens an undecorated {@code Stage} containing an {@code AboutView}.
     *
     * @param owner     Stage owner
     */
    public static void show(Stage owner) {
        AboutView aboutView = new AboutView();
        aboutView.setStyle("-fx-border-color: #67ccff; -fx-border-width: 0.5em;");
        Scene scene = new Scene(aboutView, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);

        Stage primaryStage = Topsoil.getPrimaryStage();

        double newX = primaryStage.getX() + (primaryStage.getWidth() / 2) -
                      (DEFAULT_WIDTH / 2);
        double newY = primaryStage.getY() + (primaryStage.getHeight() / 2) -
                      (DEFAULT_HEIGHT / 2);

        stage.setX(newX);
        stage.setY(newY);

        stage.initOwner(owner);
        stage.initModality(Modality.NONE);
        // Close window if main window gains focus.
        owner.getScene().getWindow().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                stage.close();
            }
        });

        stage.show();
        stage.requestFocus();
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    @FXML private void openHome() { browser.browse(HOME_URL); }

    @FXML private void openGitHub() {
        browser.browse(GITHUB_URL);
    }

    @FXML private void openCIRDLES() {
        browser.browse(CIRDLES_URL);
    }

    @FXML private void openReleaseLog() {
        browser.browse(RELEASE_LOG_URL);
    }

    @FXML private void openLicensePage() {
        browser.browse(LICENSE_URL);
    }

}
