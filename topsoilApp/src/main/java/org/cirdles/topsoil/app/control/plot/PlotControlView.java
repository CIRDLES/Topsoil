package org.cirdles.topsoil.app.control.plot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.PlotConfigDialog;
import org.cirdles.topsoil.app.control.plot.panel.PlotOptionsPanel;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotFunction;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.internal.PDFSaver;
import org.cirdles.topsoil.plot.internal.SVGSaver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A custom control for viewing a {@link Plot}, containing the plot itself, its button bar, and its
 * {@link PlotOptionsPanel}.
 */
public class PlotControlView extends VBox {

	private static final String CONTROLLER_FXML = "plot-control-view.fxml";

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private ToolBar toolbar;
	@FXML private Button plotConfigButton, saveSVGButton, savePDFButton, resetViewButton;

	private PlotView plot;
	private PlotOptionsPanel propertiesPanel;

	@FXML private AnchorPane plotAnchorPane;
	@FXML private AnchorPane propertiesPanelAnchorPane;

	//**********************************************//
	//                  ATTRIBUTES                  //
	//**********************************************//

	private FXDataTable table;
	private ScheduledExecutorService plotObserver;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	PlotControlView(PlotView plot, FXDataTable table) {
		super();
		this.plot = plot;
		this.table = table;
		this.propertiesPanel = new PlotOptionsPanel(plot);
		this.plotObserver = new PlotObservationThread().initializePlotObservation(this.plot, this.propertiesPanel);
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, PlotControlView.class, this);
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
	 * Returns the {@code Plot} instance for this {@code PlotControlView}.
	 *
	 * @return	Plot
	 */
	public Plot getPlot() {
		return plot;
	}

	/**
	 * Returns the properties panel of this {@code PlotControlView}.
	 *
	 * @return	PlotOptionsPanel
	 */
	PlotOptionsPanel getPropertiesPanel() {
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

	@FXML private void plotConfigButtonAction() {
		Map<PlotConfigDialog.Key, Object> settings = new HashMap<>();
		settings.put(PlotConfigDialog.Key.VARIABLE_MAP, getPlot().getVariableMap());
		settings.put(PlotConfigDialog.Key.ISOTOPE_SYSTEM, getPlot().getOptions().get(PlotOption.ISOTOPE_SYSTEM));

		PlotConfigDialog dialog = new PlotConfigDialog(table, settings);
		Map<PlotConfigDialog.Key, Object> newSettings = dialog.showAndWait().orElse(null);
		if (newSettings != null) {
			getPlot().setVariableMap((Map<Variable<?>, DataColumn<?>>) newSettings.get(PlotConfigDialog.Key.VARIABLE_MAP));
			getPlot().getOptions().put(PlotOption.ISOTOPE_SYSTEM, newSettings.get(PlotConfigDialog.Key.ISOTOPE_SYSTEM));
		}
	}

	/**
	 * Saves the current view of the plot as an SVG file.
	 */
	@FXML private void saveSVGButtonAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export to SVG");
		fileChooser.getExtensionFilters().setAll(
				new FileChooser.ExtensionFilter("SVG Image", "*.svg")
		);

		File file = fileChooser.showSaveDialog(Topsoil.getPrimaryStage());
		if (file != null) {
			new SVGSaver().save(plot.toSVGDocument(), file);
		}
	}

	/**
	 * Saves the current view of the plot as a PDF file.
	 */
	@FXML private void savePDFButtonAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export to PDF");
		fileChooser.getExtensionFilters().setAll(
				new FileChooser.ExtensionFilter("PDF Document", "*.pdf")
		);

		File file = fileChooser.showSaveDialog(Topsoil.getPrimaryStage());
		if (file != null) {
			PDFSaver.save(plot, file);
		}
	}

	/**
	 * Resets the plot to its initial view.
	 */
	@FXML private void resetViewButtonAction() {
		plot.call(PlotFunction.Scatter.RECENTER);
	}

	private void initializePlot() {
		plotAnchorPane.getChildren().add(plot);
		FXMLUtils.setAnchorPaneConstraints(plot, 0.0, 0.0, 0.0, 0.0);
	}

	private void initializePropertiesPanel() {
		// Bind IsotopeSystem of table and properties panel
//		propertiesPanel.isotopeSystemProperty().bindBidirectional(table.isotopeSystemProperty());

		propertiesPanelAnchorPane.getChildren().add(propertiesPanel);
		FXMLUtils.setAnchorPaneConstraints(propertiesPanel, 0.0, 0.0, 0.0, 0.0);
	}

}
