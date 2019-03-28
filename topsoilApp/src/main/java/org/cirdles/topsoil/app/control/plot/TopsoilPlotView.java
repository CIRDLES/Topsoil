package org.cirdles.topsoil.app.control.plot;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.internal.PDFSaver;
import org.cirdles.topsoil.plot.internal.SVGSaver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * A custom control for viewing a {@link Plot}, containing the plot itself, its button bar, and its
 * {@link PlotPropertiesPanel}.
 */
public class TopsoilPlotView extends VBox {

	private static final String CONTROLLER_FXML = "plot-view.fxml";

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private ToolBar toolbar;
	@FXML private Button saveSVGButton;
	@FXML private Button savePDFButton;
	@FXML private Button resetViewButton;
	@FXML private Button snapToCornersButton;

	private PlotPropertiesPanel propertiesPanel;

	@FXML private AnchorPane plotAnchorPane;
	@FXML private AnchorPane propertiesPanelAnchorPane;

	//**********************************************//
	//                  ATTRIBUTES                  //
	//**********************************************//

	private Plot plot;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	public TopsoilPlotView(Plot plot) {
		super();
		this.plot = plot;
		this.propertiesPanel = new PlotPropertiesPanel(plot);
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, TopsoilPlotView.class, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
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

	/**
	 * Saves the current view of the plot as an SVG file.
	 */
	@FXML private void saveSVGButtonAction() {

		String uncValue = propertiesPanel.getUncertaintyFormat().toString();
		Document doc = plot.displayAsSVGDocument();

		Element ele = doc.getDocumentElement();
		Element textNode = doc.createElement("text");
		textNode.setAttribute("x", "230");
		textNode.setAttribute("y", "553");
		textNode.setAttribute("font-family", "sans-serif");
		textNode.setAttribute("font-size", "18px");
		textNode.setTextContent("Uncertainty Format: " + uncValue);

		ele.appendChild(textNode);

		new SVGSaver().save(doc);
	}

	/**
	 * Saves the current view of the plot as a PDF file.
	 */
	@FXML private void savePDFButtonAction() {

//		WritableImage plotSnap = plotAnchorPane.snapshot(new SnapshotParameters(), null);
//		String uncValue = propertiesPanel.getUncertaintyFormat().toString();
//		PDFSaver.saveToPDF(plotSnap, uncValue);

		PDFSaver.save(plot);

	}

	/**
	 * Resets the plot to its initial view.
	 */
	@FXML private void resetViewButtonAction() {
		plot.recenter();
	}

	/**
	 * If applicable, adjusts the plot view so that the concordia line passes through the corners.
	 */
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
