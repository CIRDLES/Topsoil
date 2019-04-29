package org.cirdles.topsoil.app;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.IOException;

/**
 * The main node for the Topsoil application.
 *
 * It is a singleton, since we only need one at time of writing. The instance is only accessible from inside this
 * package. The main content node will be automatically set to either a {@link HomeView}, if no data is showing, or a
 * {@link ProjectView}, if there is data showing, based on the value of {@link ProjectManager#projectProperty()}.
 */
class MainController extends VBox {

    private static MainController instance;

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

    private MainController() {
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
        ProjectManager.projectProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
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

    static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    /**
     * Returns the {@code HomeView} instance used by the controller.
     *
     * @return  HomeView
     */
    HomeView getHomeView() {
        return homeView;
    }

    /**
     * Returns the {@code ProjectView} being displayed by the controller, if one is being displayed.
     *
     * @return  current ProjectView, else null
     */
    ProjectView getProjectView() {
        Node view = mainContentPane.getChildren().get(0);
        return (view instanceof ProjectView) ? (ProjectView) view : null;
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
