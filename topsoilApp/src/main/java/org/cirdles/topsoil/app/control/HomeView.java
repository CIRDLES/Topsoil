package org.cirdles.topsoil.app.control;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.app.util.ResourceBundles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * The main view of Topsoil when no data is showing.
 *
 * @author marottajb
 */
public class HomeView extends GridPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "home-view.fxml";
    private static final String CIRDLES_LOGO = "cirdles-logo-yellow.png";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private Label recentFilesLabel, cirdlesLabel;
    @FXML private ImageView cirdlesLogo;
    @FXML private VBox recentFilesLinkBox;

    private Label noRecentFilesLabel = new Label("No recent files.");

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public HomeView() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, HomeView.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        ResourceBundle resources = ResourceBundles.MAIN.getBundle();
        recentFilesLabel.setText(resources.getString("recentFiles"));
        cirdlesLabel.setText(resources.getString("cirdlesLabel"));

        final ResourceExtractor re = new ResourceExtractor(HomeView.class);
        cirdlesLogo.setImage(new Image(re.extractResourceAsPath(CIRDLES_LOGO).toUri().toString()));
        noRecentFilesLabel.setStyle("-fx-font-style: italic;");
        refreshRecentFiles();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void refreshRecentFiles() {
        recentFilesLinkBox.getChildren().clear();
        Path[] recentFiles = RecentFiles.getPaths();
        if (recentFiles.length == 0) {
            recentFilesLinkBox.getChildren().add(noRecentFilesLabel);
        } else {
            Hyperlink link;
            for (Path path : recentFiles) {
                link = new Hyperlink(path.toString());
                link.setOnAction(event -> FileMenuHelper.openProject(path));
                recentFilesLinkBox.getChildren().add(link);
            }
        }
    }

    public void clearRecentFiles() {
        recentFilesLinkBox.getChildren().setAll(noRecentFilesLabel);
    }

}
