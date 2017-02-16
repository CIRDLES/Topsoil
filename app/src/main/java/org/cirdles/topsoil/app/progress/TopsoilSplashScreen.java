package org.cirdles.topsoil.app.progress;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class TopsoilSplashScreen extends Pane implements Initializable {

    // Links
    @FXML private Hyperlink github, cirdles, releaseLog;
    private final String GITHUB_URL = "https://github.com/CIRDLES/Topsoil";
    private final String CIRDLES_URL = "https://cirdles.org/";
    private final String RELEASE_LOG_URL = "https://github.com/CIRDLES/Topsoil/releases";

    // Recent Files
    @FXML private Hyperlink moreRecentFiles;
    @FXML private Hyperlink recentFile1;
    @FXML private Hyperlink recentFile2;
    @FXML private Hyperlink recentFile3;
    @FXML private VBox recentFileContainer;

    private DesktopWebBrowser browser;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert github != null : "fx:id=\"github\" was not injected: check your FXML file 'topsoilSplashScreen.fxml'.";
        assert cirdles != null : "fx:id=\"cirdles\" was not injected: check your FXML file 'topsoilSplashScreen.fxml'.";
        assert releaseLog != null : "fx:id=\"releaseLog\" was not injected: check your FXML file 'topsoilSplashScreen" +
                                    ".fxml'.";
        assert recentFile1 != null : "fx:id=\"recentFile1\" was not injected: check your FXML file " +
                                     "'topsoilSplashScreen.fxml'.";
        assert recentFile2 != null : "fx:id=\"recentFile2\" was not injected: check your FXML file " +
                                     "'topsoilSplashScreen.fxml'.";
        assert recentFile3 != null : "fx:id=\"recentFile3\" was not injected: check your FXML file " +
                                     "'topsoilSplashScreen.fxml'.";
        assert moreRecentFiles != null : "fx:id=\"moreRecentFiles\" was not injected: check your FXML file " +
                                         "'topsoilSplashScreen.fxml'.";
        assert recentFileContainer != null : "fx:id=\"recentFileContainer\" was not injected: check your FXML file " +
                                             "'topsoilSplashScreen.fxml'.";

        browser = new DesktopWebBrowser(Desktop.getDesktop(), new ErrorAlerter());

        recentFileContainer.getChildren().remove(0, recentFileContainer.getChildren().size());
        Label comingSoonLabel = new Label("Not supported yet.");
        comingSoonLabel.setFont(Font.font("System", FontPosture.ITALIC, 20));
        recentFileContainer.getChildren().add(comingSoonLabel);

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

    @FXML private void openRecentTopsoilFile(ActionEvent event) {

    }
}
