package org.cirdles.topsoil.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.view.TopsoilProjectView;

import java.io.IOException;

/**
 * A controller class for Topsoil's {@link Main}.
 *
 * @author Jake Marotta
 * @see Main
 */
public class MainController extends VBox {

	//**********************************************//
	//                  CONSTANTS                   //
	//**********************************************//

	private static final String CONTROLLER_FXML = "main-window.fxml";
	private static final String TOPSOIL_LOGO = "topsoil-logo.png";

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private AnchorPane mainContentPane;
	private Image topsoilLogo;
	private TopsoilHomeView homeView;

	//**********************************************//
	//                  PROPERTIES                  //
	//**********************************************//

	private BooleanProperty dataShowing = new SimpleBooleanProperty(false);
	public BooleanProperty dataShowingProperty() {
		return dataShowing;
	}
	public boolean isDataShowing() {
		return (mainContentPane.getChildren().get(0) instanceof TopsoilProjectView);
	}

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	MainController() {
		final ResourceExtractor re = new ResourceExtractor(MainController.class);
		FXMLLoader loader;

		try {
			loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
			loader.setRoot(this);
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
		}

		topsoilLogo = new Image(re.extractResourceAsPath(TOPSOIL_LOGO).toUri().toString());
		Main.primaryStage.getIcons().add(topsoilLogo);
	}

	@FXML
	protected void initialize() {
		homeView = new TopsoilHomeView();
		replaceMainContent(homeView);
	}

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	public Node getMainContent() {
		return mainContentPane.getChildren().get(0);
	}

	public Node setProjectView(TopsoilProjectView projectView) {
		return replaceMainContent(projectView);
	}

	public void closeProjectView() {
		replaceMainContent(homeView);
	}

	public Image getTopsoilLogo() {
	    return topsoilLogo;
    }

	//**********************************************//
	//                PRIVATE METHODS               //
	//**********************************************//

    private Node replaceMainContent(Node content) {
    	Node rtnval = mainContentPane.getChildren().isEmpty() ? null : mainContentPane.getChildren().get(0);
    	mainContentPane.getChildren().clear();
    	mainContentPane.getChildren().add(content);
    	AnchorPane.setTopAnchor(content, 0.0);
    	AnchorPane.setRightAnchor(content, 0.0);
    	AnchorPane.setBottomAnchor(content, 0.0);
    	AnchorPane.setLeftAnchor(content, 0.0);
    	return rtnval;
	}
}
