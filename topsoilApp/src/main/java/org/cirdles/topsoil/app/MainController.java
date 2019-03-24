package org.cirdles.topsoil.app;

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

    public static MainController instance;

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
        TopsoilProject project = ProjectManager.getProject();

        // Set the main window content appropriately based on whether or not a project is present
        replaceMainContent((project != null) ? new ProjectView(project) : homeView);
        ProjectManager.projectProperty().addListener(c -> {
            if (ProjectManager.getProject() == null) {
                homeView.refreshRecentFiles();
                replaceMainContent(homeView);
            } else {
                replaceMainContent(new ProjectView(ProjectManager.getProject()));
            }
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    /**
     * Returns the main content node of the controller, at time of writing either a {@code HomeView} or a {@code ProjectView},
     * depending on whether a project is loaded.
     *
     * @return  Node
     */
    public Node getMainContent() {
        return mainContentPane.getChildren().get(0);
    }

    /**
     * Returns the {@code HomeView} instance used by the controller.
     *
     * @return  HomeView
     */
    public HomeView getHomeView() {
        return homeView;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void replaceMainContent(Node content) {
        mainContentPane.getChildren().clear();
        mainContentPane.getChildren().add(content);
        FXMLUtils.setAnchorPaneConstraints(content, 0.0, 0.0, 0.0, 0.0);
    }
}
