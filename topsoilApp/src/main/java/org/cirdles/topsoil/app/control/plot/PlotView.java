package org.cirdles.topsoil.app.control.plot;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.internal.PDFSaver;
import org.cirdles.topsoil.plot.internal.SVGSaver;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A custom control for viewing a {@link Plot}, containing the plot itself, its button bar, and its
 * {@link PlotPropertiesPanel}.
 */
public class PlotView extends VBox {

	private static final String CONTROLLER_FXML = "plot-view.fxml";

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private ToolBar toolbar;
	@FXML private Button saveSVGButton;
	@FXML private Button savePDFButton;
	@FXML private Button resetViewButton;

	private PlotPropertiesPanel propertiesPanel;

	@FXML private AnchorPane plotAnchorPane;
	@FXML private AnchorPane propertiesPanelAnchorPane;

	//**********************************************//
	//                  ATTRIBUTES                  //
	//**********************************************//

	private Plot plot;
	private DataTable table;
	private ScheduledExecutorService plotObserver;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	PlotView(Plot plot, DataTable table) {
		super();
		this.plot = plot;
		this.table = table;
		this.propertiesPanel = new PlotPropertiesPanel(plot);
		this.plotObserver = new PlotObservationThread().initializePlotObservation(this.plot, this.propertiesPanel);
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, PlotView.class, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML protected void initialize() {
		initializePlot();
		initializePropertiesPanel();

	}

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	/**
	 * Returns the {@code Plot} instance for this {@code PlotView}.
	 *
	 * @return	Plot
	 */
	public Plot getPlot() {
		return plot;
	}

	/**
	 * Returns the properties panel of this {@code PlotView}.
	 *
	 * @return	PlotPropertiesPanel
	 */
	PlotPropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}

	/**
	 * Shuts down the {@link ScheduledExecutorService} responsible for updating the properties panel with changes in the
	 * plot. Should only be called if this instance will no longer be used.
	 */
	void shutdownObserver() {
		plotObserver.shutdown();
	}

	//**********************************************//
	//                PRIVATE METHODS               //
	//**********************************************//

	/**
	 * Saves the current view of the plot as an SVG file.
	 */
	@FXML private void saveSVGButtonAction() {
		Document doc = plot.displayAsSVGDocument();
		new SVGSaver().save(doc);
	}

	/**
	 * Saves the current view of the plot as a PDF file.
	 */
	@FXML private void savePDFButtonAction() {
		PDFSaver.save(plot);
	}

	/**
	 * Resets the plot to its initial view.
	 */
	@FXML private void resetViewButtonAction() {
		plot.recenter();
	}

	private void initializePlot() {
		Node n = plot.displayAsNode();
		plotAnchorPane.getChildren().add(n);
		FXMLUtils.setAnchorPaneConstraints(n, 0.0, 0.0, 0.0, 0.0);
	}

	private void initializePropertiesPanel() {
		// Bind IsotopeSystem of table and properties panel
		propertiesPanel.isotopeSystemProperty().bindBidirectional(table.isotopeSystemProperty());

		propertiesPanelAnchorPane.getChildren().add(propertiesPanel);
		FXMLUtils.setAnchorPaneConstraints(propertiesPanel, 0.0, 0.0, 0.0, 0.0);
	}

}
