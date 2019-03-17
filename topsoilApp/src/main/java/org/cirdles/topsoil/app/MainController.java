package org.cirdles.topsoil.app;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.control.HomeView;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.ProjectSerializer;

import java.io.IOException;
import java.nio.file.Path;

public class MainController extends VBox {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "main-window.fxml";


    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private AnchorPane mainContentPane;
    private HomeView homeView;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ObjectProperty<TopsoilProject> project = new SimpleObjectProperty<>();
    public ObjectProperty<TopsoilProject> projectProperty() {
        return project;
    }
    public TopsoilProject getProject() {
        return project.get();
    }
    public void setProject(TopsoilProject project) {
        if (project == null) {
            replaceMainContent(homeView);
        } else {
            replaceMainContent(new ProjectView(project));
        }
        this.project.set(project);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    MainController(Stage primaryStage) {
        // If a .topsoil file is open, the name of the file is appended to "Topsoil" at the top of the window
        primaryStage.titleProperty().bind(Bindings.createStringBinding(() -> {
            Path path = ProjectSerializer.getCurrentPath();
            if (path != null) {
                if (path.getFileName() != null) {
                    return "Topsoil - " + path.getFileName().toString();
                }
            }
            return "Topsoil";
        }, ProjectSerializer.currentPathProperty()));

        try {
            FXMLUtils.loadController(CONTROLLER_FXML, MainController.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        homeView = new HomeView();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Node getMainContent() {
        return mainContentPane.getChildren().get(0);
    }

    public HomeView getHomeView() {
        return homeView;
    }

    public void setHomeView() {
        homeView.refreshRecentFiles();
        replaceMainContent(homeView);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void replaceMainContent(Node content) {
        mainContentPane.getChildren().clear();
        mainContentPane.getChildren().add(content);
        FXMLUtils.setAnchorPaneBounds(content, 0.0, 0.0, 0.0, 0.0);
    }
}
