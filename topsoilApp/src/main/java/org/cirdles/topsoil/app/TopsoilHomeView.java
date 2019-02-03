package org.cirdles.topsoil.app;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilHomeView extends VBox {

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
    //                  PROPERTIES                  //
    //**********************************************//

    private ListProperty<Path> recentFilesList = new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<Path> recentFilesListProperty() {
        return recentFilesList;
    }
    public final List<Path> getRecentFilesList() {
        return recentFilesList.get();
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final ResourceExtractor re = new ResourceExtractor(TopsoilHomeView.class);

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    TopsoilHomeView(Path... recentFiles) {
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            this.recentFilesList.addAll(recentFiles);
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
    }

    @FXML
    protected void initialize() {
        cirdlesLogo.setImage(new Image(re.extractResourceAsPath("cirdles-logo-yellow.png").toUri().toString()));
        if (recentFilesList.isEmpty()) {
            noRecentFilesLabel.setStyle("-fx-font-style: italic;");
            recentFilesLinkBox.getChildren().add(noRecentFilesLabel);
        } else {
            Hyperlink link;
            for (Path path : recentFilesList) {
                link = new Hyperlink(path.getFileName().toString());
                link.setOnAction(event -> {
                    // TODO Open recent file
                });
                recentFilesLinkBox.getChildren().add(link);
            }
        }
    }

}
