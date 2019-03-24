package org.cirdles.topsoil.app.menu.helpers;

import javafx.stage.Stage;
import org.cirdles.topsoil.app.control.AboutView;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.util.issue.IssueCreator;
import org.cirdles.topsoil.app.util.issue.StandardGitHubIssueCreator;

import java.awt.*;

/**
 * A utility class providing helper methods for the logic behind items in
 * {@link org.cirdles.topsoil.app.menu.TopsoilMenuBar}.
 *
 * @author marottajb
 */
public class HelpMenuHelper {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Opens the system default browser to the Topsoil help page.
     */
    public static void openOnlineHelp() {
        String TOPSOIL_URL = "http://cirdles.org/projects/topsoil/";
        new DesktopWebBrowser(Desktop.getDesktop()).browse(TOPSOIL_URL);
    }

    /**
     * Opens the system default browser to the "New Issue" form for Topsoil on GitHub, and loads relevant information
     * into the issue body.
     */
    public static void openIssueReporter() {
        IssueCreator issueCreator = new StandardGitHubIssueCreator(
                new TopsoilMetadata(),
                System.getProperties(),
                new DesktopWebBrowser(Desktop.getDesktop()),
                new StringBuilder()
        );
        issueCreator.create();
    }

    /**
     * Opens a floating stage containing About information.
     *
     * @param owner Stage
     */
    public static void openAboutScreen(Stage owner) {
        AboutView.show(owner);
    }
}
