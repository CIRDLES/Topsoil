package org.cirdles.topsoil.app.plot;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.DefaultProperties;
import org.cirdles.topsoil.app.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperty;
import org.cirdles.topsoil.plot.internal.SVGSaver;

import java.io.IOException;
import java.util.Map;

public class TopsoilPlotView extends VBox {

	private static final String CONTROLLER_FXML = "plot-view.fxml";

	private Plot plot;

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private ToolBar toolbar;
	@FXML private Button saveSVGButton;
	@FXML private Button resetViewButton;
	@FXML private Button snapToCornersButton;

	private PlotPropertiesPanel propertiesPanel;

	@FXML private AnchorPane plotAnchorPane;
	@FXML private AnchorPane propertiesPanelAnchorPane;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	public TopsoilPlotView(Plot plot) {
		this(plot, new DefaultProperties());
	}

	public TopsoilPlotView(Plot plot, Map<PlotProperty, Object> properties) {
		super();
		this.plot = plot;
		this.propertiesPanel = new PlotPropertiesPanel(plot, properties);
		try {
			FXMLLoader loader = new FXMLLoader(new ResourceExtractor(TopsoilPlotView.class).extractResourceAsPath(CONTROLLER_FXML)
					                                   .toUri().toURL());
			loader.setRoot(this);
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML protected void initialize() {
		initializeToolbar();
		initializePlot();
		initializePropertiesPanel();
	}

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	public Plot getPlot() {
		return plot;
	}

	public PlotPropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}

	//**********************************************//
	//                PRIVATE METHODS               //
	//**********************************************//

	@FXML private void saveSVGButtonAction() {
		new SVGSaver().save(plot.displayAsSVGDocument());
	}

	@FXML private void resetViewButtonAction() {
		plot.recenter();
	}

	@FXML private void snapToCornersButtonAction() {
		plot.snapToCorners();
	}

	private void initializeToolbar() {
		if (plot instanceof JavaScriptPlot ) {
			JavaScriptPlot javaScriptPlot = (JavaScriptPlot) plot;

			Text loadingIndicator = new Text("Loading...");

			javaScriptPlot.getLoadFuture().thenRunAsync(() -> {
				                                            loadingIndicator.visibleProperty().bind(
						                                            javaScriptPlot.getWebEngine().getLoadWorker()
								                                            .stateProperty().isEqualTo(Worker.State.RUNNING));
				                                            propertiesPanel.refreshPlot();
			                                            },
			                                            Platform::runLater
			                                           );

			toolbar.getItems().addAll(loadingIndicator);
		}
	}

	private void initializePlot() {
		Node n = plot.displayAsNode();
		plotAnchorPane.getChildren().add(n);
		AnchorPane.setBottomAnchor(n, 0.0);
		AnchorPane.setLeftAnchor(n, 0.0);
		AnchorPane.setTopAnchor(n, 0.0);
		AnchorPane.setRightAnchor(n, 0.0);
	}

	private void initializePropertiesPanel() {
		propertiesPanelAnchorPane.getChildren().add(propertiesPanel);
		AnchorPane.setBottomAnchor(propertiesPanel, 0.0);
		AnchorPane.setLeftAnchor(propertiesPanel, 0.0);
		AnchorPane.setTopAnchor(propertiesPanel, 0.0);
		AnchorPane.setRightAnchor(propertiesPanel, 0.0);
	}

}
