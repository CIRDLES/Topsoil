package org.cirdles.topsoil.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.control.HomeView;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.IOException;

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
    private boolean projectShowing = false;

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
            projectShowing = false;
        } else {
            replaceMainContent(new ProjectView(project));
            projectShowing = true;
        }
        this.project.set(project);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    MainController() {
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
        projectShowing = false;
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
