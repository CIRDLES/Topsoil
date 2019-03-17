package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.menu.helpers.FileMenuHelper;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.RecentFiles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private ImageView cirdlesLogo;
    @FXML private VBox recentFilesLinkBox;

    private Label noRecentFilesLabel = new Label("No recent files.");

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ResourceExtractor re = new ResourceExtractor(HomeView.class);

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
        cirdlesLogo.setImage(new Image(re.extractResourceAsPath("cirdles-logo-yellow.png").toUri().toString()));
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
                Path fileName = path.getFileName();
                if (fileName != null) {
                    link = new Hyperlink(fileName.toString());
                    link.setOnAction(event -> {
                        TopsoilProject project = FileMenuHelper.openProject(path);
                        if (project != null) {
                            Topsoil.getController().setProject(project);
                        }
                    });
                    recentFilesLinkBox.getChildren().add(link);
                }
            }
        }
    }

    public void clearRecentFiles() {
        recentFilesLinkBox.getChildren().setAll(noRecentFilesLabel);
    }

}
