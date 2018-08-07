package org.cirdles.topsoil.app.tab;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.PlotGenerationHandler;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.table.command.InsertRowCommand;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This is the primary view for a {@link TopsoilDataTable}. It contains the {@link TableView} that the data is loaded
 * into, the {@link PlotPropertiesPanel} that controls the attributes of any plots for the data, and any
 * other visual controls in the {@link TopsoilTab}
 *
 * @author Jake Marotta
 * @see PlotPropertiesPanel
 * @see Tab
 * @see TopsoilTabPane
 */
public class TopsoilTabContent extends AnchorPane {

	private static final String CONTROLLER_FXML = "topsoil-tab-content.fxml";

	private TopsoilDataTable table;

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

    @FXML private ComboBox<IsotopeType> isotopeSystemComboBox;
    @FXML private ComboBox<UncertaintyFormat> uncertaintyFormatComboBox;

    @FXML private Button assignVariablesButton;
    @FXML private Button generatePlotButton;

    @FXML private TableView<TopsoilDataEntry> tableView;

	//**********************************************//
	//                  PROPERTIES                  //
	//**********************************************//

    private ObjectProperty<IsotopeType> isotopeSystem;
    public ObjectProperty<IsotopeType> isotopeSystemProperty() {
    	if (isotopeSystem == null) {
    		isotopeSystem = new SimpleObjectProperty<>();
    		isotopeSystem.bindBidirectional(isotopeSystemComboBox.valueProperty());
	    }
	    return isotopeSystem;
    }
    public final IsotopeType getIsotopeSystem() {
    	return isotopeSystemProperty().get();
    }
    public final void setIsotopeSystem(IsotopeType i) {
    	isotopeSystemProperty().set(i);
    }

    private ObjectProperty<UncertaintyFormat> uncertaintyFormat;
    public ObjectProperty<UncertaintyFormat> uncertaintyFormatProperty() {
    	if (uncertaintyFormat == null) {
    		uncertaintyFormat = new SimpleObjectProperty<>();
    		uncertaintyFormat.bindBidirectional(uncertaintyFormatComboBox.valueProperty());
	    }
	    return uncertaintyFormat;
    }
    public final UncertaintyFormat getUncertaintyFormat() {
    	return uncertaintyFormatProperty().get();
    }
    public final void setUncertaintyFormat(UncertaintyFormat format) {
    	uncertaintyFormatProperty().set(format);
    }

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	public TopsoilTabContent(TopsoilDataTable table) {
    	this.table = table;
    	try {
    		FXMLLoader loader = new FXMLLoader(new ResourceExtractor(TopsoilTabContent.class).extractResourceAsPath
				    (CONTROLLER_FXML).toUri().toURL());
    		loader.setRoot(this);
    		loader.setController(this);
    		loader.load();
	    } catch (IOException e) {
    		e.printStackTrace();
	    }
	}

    @FXML protected void initialize() {

    	isotopeSystemComboBox.getItems().addAll(IsotopeType.values());
    	uncertaintyFormatComboBox.getItems().addAll(UncertaintyFormat.values());

	    isotopeSystemProperty().bindBidirectional(table.isotopeTypeObjectProperty());
	    setUncertaintyFormat(table.getUncertaintyFormat());
	    uncertaintyFormatComboBox.setDisable(true);

        // Handle Keyboard Events
        tableView.setOnKeyPressed(this::handleTableViewKeyEvent);

        // Enables individual cell selection instead of rows.
        tableView.getSelectionModel().setCellSelectionEnabled(true);

        // Enables multiple cell selection.
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        configureColumns();
        resetIds();

        // A somewhat hacky method of disabling column dragging in the TableView.
        tableView.widthProperty().addListener(c -> {
            TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(ch -> header.setReordering(false));
        });
    }

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	public void addColumn(int index) {
		List<TableColumn<TopsoilDataEntry, ?>> columns = tableView.getColumns();

		TableColumn<TopsoilDataEntry, Double> newColumn;

		newColumn = new TableColumn<>("New Column");

		newColumn.setCellValueFactory(param -> {
			if (param.getValue().getProperties().size() == 0) {
				return (ObservableValue) new SimpleDoubleProperty(0.0);
			} else {
				if (param.getValue().getProperties().size() < index + 1) {
					SimpleDoubleProperty newProperty = new SimpleDoubleProperty(Double.NaN);
					param.getValue().getProperties().add(newProperty);
					return (ObservableValue) newProperty;
				} else {
					return (ObservableValue) param.getValue().getProperties().get(index);
				}
			}
		});

		// override cell factory to custom editable cells
		newColumn.setCellFactory(value -> new TopsoilTableCell());

		// disable column sorting
		newColumn.setSortable(false);

		// initial width
		newColumn.setPrefWidth(160.0);

		// add functional column to the array of columns
		columns.add(index, newColumn);
	}

	public void removeColumn(int index) {
		tableView.getColumns().remove(index);
	}

	/**
	 * Returns the {@code TableView} from this {@code TopsoilTabContent}.
	 *
	 * @return TableView
	 */
	public TableView<TopsoilDataEntry> getTableView() {
		return tableView;
	}

	/**
	 * Sets the data to be displayed.
	 *
	 * @param dataEntries   an ObservableList of TopsoilDataEntries
	 */
	public void setData(ObservableList<TopsoilDataEntry> dataEntries) {
		tableView.setItems(dataEntries);
		tableView.getColumns().clear();
		configureColumns();
	}

	//**********************************************//
	//                PRIVATE METHODS               //
	//**********************************************//

    /**
     * Handles keyboard events in the {@code TableView}.
     *
     * @param keyEvent  a KeyEvent
     */
    private void handleTableViewKeyEvent(KeyEvent keyEvent) {

        List<TableColumn<TopsoilDataEntry, ?>> columns = tableView.getColumns();
        TableView.TableViewSelectionModel<TopsoilDataEntry> selectionModel = tableView.getSelectionModel();

        // Tab focuses right cell
        // Shift + Tab focuses left cell
        if (keyEvent.getCode().equals(KeyCode.TAB)) {
            if (keyEvent.isShiftDown()) {
                if (selectionModel.getSelectedCells().get(0).getColumn() == 0 &&
                        selectionModel.getSelectedIndex() != 0) {
                    selectionModel.select(selectionModel.getSelectedIndex() - 1, this.tableView.getColumns().get
                            (columns.size() - 1));
                } else {
                    selectionModel.selectLeftCell();
                }
            } else {
                if (selectionModel.getSelectedCells().get(0).getColumn() ==
                        columns.size() - 1 && selectionModel.getSelectedIndex() != tableView.getItems().size() - 1) {
                    selectionModel.select(selectionModel.getSelectedIndex() + 1, this.tableView.getColumns().get(0));
                } else {
                    selectionModel.selectRightCell();
                }
            }

            keyEvent.consume();

            // Enter moves down or creates new empty row
            // Shift + Enter moved up a row
        } else if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (keyEvent.isShiftDown()) {
                selectionModel.selectAboveCell();
            } else {
                // if on last row
                if (selectionModel.getSelectedIndex() == tableView.getItems().size() - 1) {
                    InsertRowCommand insertRowCommand = new InsertRowCommand(this.tableView);
                    insertRowCommand.execute();
                    ((TopsoilTabPane) this.tableView.getScene().lookup("#TopsoilTabPane")).getSelectedTab()
                                                                                          .addUndo(insertRowCommand);
                }
                selectionModel.selectBelowCell();
            }
            keyEvent.consume();
        }
    }

    /**
     * Configures the {@code TableColumn}s in the {@code TableView}.
     */
    private void configureColumns() {

        int maxRowLength = 0;
        for (TopsoilDataEntry row : tableView.getItems()) {
            maxRowLength = Math.max(maxRowLength, row.getProperties().size());
        }

        for (int i = 0; i < maxRowLength; i++) {
            addColumn(i);
        }
    }

    /**
     * Resets the {@code String} ids associated with each {@code TableColumn} in the {@code TableView}.
     * <p>Each {@code TableColumn} has an associated String id assigned to it, increasing numerically from 1, left to
     * right. This is to keep track of the order of the columns before and after they are re-ordered due to clicking and
     * dragging.
     * </p>
     */
    private void resetIds() {
        int id = 0;
        for (TableColumn<TopsoilDataEntry, ?> column : this.tableView.getColumns()) {
            column.setId(Integer.toString(id));
            id++;
        }
    }

    @FXML private void assignVariablesButtonAction() {
        ((TopsoilTabPane) MainWindow.getPrimaryStage().getScene().lookup("#TopsoilTabPane"))
                .getSelectedTab().getTableController().showVariableChooserDialog(null);
    }

    @FXML private void generatePlotButtonAction() {
        TopsoilTableController tableController = ((TopsoilTabPane) MainWindow.getPrimaryStage().getScene().lookup
                ("#TopsoilTabPane"))
                .getSelectedTab().getTableController();

        // If X and Y aren't specified.
        if (!tableController.getTable().getVariableAssignments().containsKey(Variables.X)
            || !tableController.getTable().getVariableAssignments().containsKey(Variables.Y)) {
            tableController.showVariableChooserDialog(Arrays.asList(Variables.X, Variables.Y));
        }

        if (tableController.getTable().getVariableAssignments().containsKey(Variables.X)
            && tableController.getTable().getVariableAssignments().containsKey(Variables.Y)) {
            PlotGenerationHandler.handlePlotGenerationForSelectedTab((TopsoilTabPane) generatePlotButton.getScene().lookup
                    ("#TopsoilTabPane"));
        }
    }

}
