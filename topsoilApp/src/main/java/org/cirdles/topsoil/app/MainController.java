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

        replaceMainContent((project != null) ? new ProjectView(project) : homeView);
        ProjectManager.projectProperty().addListener(c -> {
            if (ProjectManager.getProject() == null) {
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

    public Node getMainContent() {
        return mainContentPane.getChildren().get(0);
    }

    public HomeView getHomeView() {
        return homeView;
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
