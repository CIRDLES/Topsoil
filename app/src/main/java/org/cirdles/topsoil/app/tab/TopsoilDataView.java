package org.cirdles.topsoil.app.tab;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import javafx.scene.layout.AnchorPane;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.app.plot.PlotGenerationHandler;
import org.cirdles.topsoil.app.plot.TopsoilPlotView;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.dialog.VariableChooserDialog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * @author marottajb
 *
 * @see TopsoilTab
 * @see TopsoilTabPane
 */
public class TopsoilDataView extends AnchorPane {

    private static final String CONTROLLER_FXML = "data-view.fxml";

    private ObservableDataTable data;
    private TopsoilSpreadsheetView spreadsheetView;

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private ComboBox<IsotopeSystem> isotopeSystemComboBox;

	@FXML private Button assignVariablesButton;
	@FXML private Button generatePlotButton;

    @FXML private AnchorPane spreadsheetPane;

	//**********************************************//
	//                  PROPERTIES                  //
	//**********************************************//

	private ObjectProperty<IsotopeSystem> isotopeSystem;
	public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
		if (isotopeSystem == null) {
			isotopeSystem = new SimpleObjectProperty<>();
			isotopeSystem.bindBidirectional(isotopeSystemComboBox.valueProperty());
		}
		return isotopeSystem;
	}
	public final IsotopeSystem getIsotopeSystem() {
		return isotopeSystemProperty().get();
	}
	public final void setIsotopeSystem(IsotopeSystem i ) {
		isotopeSystemProperty().set(i);
	}

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilDataView() {
        this(new ObservableDataTable());
    }

    public TopsoilDataView(ObservableDataTable data) {
        this.data = data;
        this.spreadsheetView = new TopsoilSpreadsheetView(data);
        try {
            FXMLLoader loader = new FXMLLoader(new ResourceExtractor(TopsoilDataView.class).extractResourceAsPath
		            (CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc}
     */
    @FXML public void initialize() {
        spreadsheetPane.getChildren().add(spreadsheetView);
        AnchorPane.setBottomAnchor(spreadsheetView, 0.0);
        AnchorPane.setRightAnchor(spreadsheetView, 0.0);
        AnchorPane.setTopAnchor(spreadsheetView, 0.0);
        AnchorPane.setLeftAnchor(spreadsheetView, 0.0);

	    isotopeSystemComboBox.getItems().addAll(IsotopeSystem.values());

	    isotopeSystemProperty().bindBidirectional(data.isotopeSystemProperty());
    }

    public TopsoilSpreadsheetView getSpreadsheetView() {
        return spreadsheetView;
    }

    public ObservableDataTable getData() {
        return spreadsheetView.getData();
    }

    public void showVariableChooserDialog(List<Variable<Number>> required) {
        Map<Variable<Number>, ObservableDataColumn> selections = VariableChooserDialog.showDialog(this, required);
        setVariableAssignments(selections);
    }

    public void setVariableAssignments(Map<Variable<Number>, ObservableDataColumn> assignments) {

        if (assignments != null) {
            data.setVariablesForColumns(assignments);

            for (TopsoilPlotView plotView : data.getOpenPlots().values()) {
                plotView.getPlot().setData(data.getPlotEntries());
	            // Re-name x and y axis titles
	            if (assignments.containsKey(Variables.X)) {
		            plotView.getPropertiesPanel().setXAxisTitle((assignments.get(Variables.X).getHeader()));
	            }
	            if (assignments.containsKey(Variables.Y)) {
		            plotView.getPropertiesPanel().setYAxisTitle((assignments.get(Variables.Y).getHeader()));
	            }
            }
        }
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    @FXML private void assignVariablesButtonAction() {
        showVariableChooserDialog(null);
    }

    @FXML private void generatePlotButtonAction() {

        // If X and Y aren't specified.
        if (! data.getVarMap().containsKey(Variables.X) || ! data.getVarMap().containsKey(Variables.Y)) {
            showVariableChooserDialog(asList(Variables.X, Variables.Y));
        }

        if (data.getVarMap().containsKey(Variables.X) && data.getVarMap().containsKey(Variables.Y)) {
            PlotGenerationHandler.generatePlotForSelectedTab(TabPaneHandler.getTabPane());
        }
    }

}
