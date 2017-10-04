package org.cirdles.topsoil.app.tab;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.table.command.InsertRowCommand;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.plot.PlotPropertiesPanelController;

import java.io.IOException;
import java.util.List;

/**
 * This is the primary view for a {@link TopsoilDataTable}. It contains the {@link TableView} that the data is loaded
 * into, the {@link PlotPropertiesPanelController} that controls the attributes of any plots for the data, and any
 * other visual controls in the {@link TopsoilTab}
 *
 * @author Jake Marotta
 * @see PlotPropertiesPanelController
 * @see Tab
 * @see TopsoilTabPane
 */
public class TopsoilTabContent extends SplitPane {

    //***********************
    // Attributes
    //***********************

    @FXML private Label messageLabel;

    /**
     * A {@code TableView} that displays the table data.
     */
    @FXML private TableView<TopsoilDataEntry> tableView;

    /**
     * An {@code AnchorPane} that contains the {@link PlotPropertiesPanelController} for this tab.
     */
    @FXML private AnchorPane plotPropertiesAnchorPane;

    /**
     * The {@code PlotPropertiesPanelController} for this tab.
     */
    private PlotPropertiesPanelController plotPropertiesPanelController;

    /**
     * The {@code String} path to the {@code .fxml} file for the {@link PlotPropertiesPanelController}.
     */
    private static final String PROPERTIES_PANEL_FXML_PATH = "plot-properties-panel.fxml";

    /**
     * A {@code ResourceExtractor} for extracting necessary resources. Used by CIRDLES projects.
     */
    private static final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(TopsoilTabContent.class);

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @FXML public void initialize() {

        // Handle Keyboard Events
        tableView.setOnKeyPressed(this::handleTableViewKeyEvent);

        // Enables individual cell selection instead of rows.
        tableView.getSelectionModel().setCellSelectionEnabled(true);

        // Enables multiple cell selection.
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        configureColumns();
        resetIds();

        initializePlotPropertiesPanel();

        // A somewhat hacky method of disabling column dragging in the TableView.
        tableView.widthProperty().addListener(c -> {
            TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener(ch -> header.setReordering(false));
        });

    }

    /**
     * Loads and initializes the {@code PlotPropertiesPanelController} from FXML.
     */
    private void initializePlotPropertiesPanel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RESOURCE_EXTRACTOR.extractResourceAsPath(PROPERTIES_PANEL_FXML_PATH).toUri().toURL());
            Node panel = fxmlLoader.load();
            plotPropertiesPanelController = fxmlLoader.getController();
            AnchorPane.setTopAnchor(panel, 0.0);
            AnchorPane.setRightAnchor(panel, 0.0);
            AnchorPane.setBottomAnchor(panel, 0.0);
            AnchorPane.setLeftAnchor(panel, 0.0);
            plotPropertiesAnchorPane.getChildren().add(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
     * Returns the {@code PlotPropertiesPanelController} from this {@code TopsoilTabContent}.
     *
     * @return  PlotPropertiesPanelController
     */
    public PlotPropertiesPanelController getPlotPropertiesPanelController() {
        return plotPropertiesPanelController;
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

    void setUncertaintyFormatLabel(String s) {
        messageLabel.setText("Uncertainty values are in " + s + " format.");
    }

}
