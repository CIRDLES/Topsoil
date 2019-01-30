package org.cirdles.topsoil.app.menu.helpers;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.TopsoilAboutScreen;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.util.issue.IssueCreator;
import org.cirdles.topsoil.app.util.issue.StandardGitHubIssueCreator;

import java.awt.*;

/**
 * @author marottajb
 */
public class HelpMenuHelper {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static void openOnlineHelp() {
        String TOPSOIL_URL = "http://cirdles.org/projects/topsoil/";
        new DesktopWebBrowser(Desktop.getDesktop()).browse(TOPSOIL_URL);
    }

    public static void openIssueReporter() {
        IssueCreator issueCreator = new StandardGitHubIssueCreator(
                new TopsoilMetadata(),
                System.getProperties(),
                new DesktopWebBrowser(Desktop.getDesktop()),
                new StringBuilder()
        );
        issueCreator.create();
    }

    public static void openAboutScreen(Stage owner) {
        Stage aboutWindow = TopsoilAboutScreen.getFloatingStage();

        aboutWindow.requestFocus();
        aboutWindow.initOwner(owner);
        aboutWindow.initModality(Modality.NONE);
        // Close window if main window gains focus.
        owner.getScene().getWindow().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                aboutWindow.close();
            }
        });
        aboutWindow.show();
    }
}
