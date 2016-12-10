package org.cirdles.topsoil.app.progress.table;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benjaminmuldrow on 7/6/16.
 */
public class TopsoilTable implements GenericTable {

    private final Alerter alerter = new ErrorAlerter();
    private String[] headers;
    private TableView<TopsoilDataEntry> table;
    private IsotopeType isotopeType;
    private String title = "Untitled Table";
    private TopsoilDataEntry [] dataEntries;
    private Field[] fields;
    private HashMap<String, PlotInformation> openPlots;

    public TopsoilTable(String [] headers, IsotopeType isotopeType, TopsoilDataEntry... dataEntries) {

        // initialize table
        this.table = new TableView<>();
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableView.TableViewSelectionModel selectionModel = this.table.getSelectionModel();
        selectionModel.setCellSelectionEnabled(true);
        this.dataEntries = dataEntries;
        this.table.setEditable(true);

        // initialize isotope type
        this.isotopeType = isotopeType;

        // populate headers
        this.headers = createHeaders(headers);

        // populate table
        this.table.getColumns().addAll(createColumns(this.headers));
        resetIds();
        if (dataEntries.length == 0) { // no data provided

            TopsoilDataEntry dataEntry = new TopsoilDataEntry();

            // add a 0 value for each column
            for (String header : this.headers) {
                dataEntry.addEntries(0.0);
            }

            this.table.getItems().add(dataEntry);

        } else { // data is provided

            this.table.getItems().addAll(dataEntries);

        }

        // Create undoable Commands for column reordering
        table.getColumns().addListener((ListChangeListener<TableColumn<TopsoilDataEntry,?>>) c -> {
            c.next();
            //   if (c.wasRemoved() && c.wasAdded())
            if (c.wasReplaced()) {
                TableColumnReorderCommand reorderCommand = new TableColumnReorderCommand(this.table);
                ((TopsoilTabPane) this.table.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(reorderCommand);
                resetIds();
            } else if (c.wasAdded() ^ c.wasRemoved() ^ c.wasUpdated() ^ c.wasPermutated()) {
                resetIds();
            }
        });

        // Handle Keyboard Events
        table.setOnKeyPressed(keyevent -> {
            // Tab focuses right cell
            // Shift + Tab focuses left cell
            if (keyevent.getCode().equals(KeyCode.TAB)) {
                if (keyevent.isShiftDown()) {
                    selectionModel.selectLeftCell();
                } else {
                    selectionModel.selectRightCell();
                }

                keyevent.consume();

            // Enter moves down or creates new empty row
            // Shift + Enter moved up a row
            } else if (keyevent.getCode().equals(KeyCode.ENTER)) {
                if (keyevent.isShiftDown()) {
                    selectionModel.selectAboveCell();
                } else {
                    // if on last row
                    if (selectionModel.getSelectedIndex() == table.getItems().size() - 1) {
                        NewRowCommand newRowCommand = new NewRowCommand(this.table);
                        newRowCommand.execute();
                        ((TopsoilTabPane) this.table.getScene().lookup("#TopsoilTabPane")).getSelectedTab().addUndo(newRowCommand);
                    }
                    selectionModel.selectBelowCell();
                }
                keyevent.consume();
            }

        });

        // Create hashmap for storing information on open plots for this table.
        this.openPlots = new HashMap<>();
    }

    /**
     * Create functional TableColumns dynamically based on header count
     * @param headers Array of header strings
     * @return an Array of functional Table Columns
     */
    private TableColumn[] createColumns(String [] headers) {

        TableColumn[] result = new TableColumn[headers.length];
        fields = new NumberField[headers.length];

        for (int i = 0; i < headers.length; i++) {

            // make a new column for each header
            TableColumn<TopsoilDataEntry, Double> column = new TableColumn<>(headers[i]);
            fields[i] = new NumberField(headers[i]);
            final int columnIndex = i;

            // override cell value factory to accept the i'th index of a data entry for the i'th column
            column.setCellValueFactory(param -> {
                if (param.getValue().getProperties().size() == 0) {
                    return (ObservableValue) new SimpleDoubleProperty(0.0);
                } else {
                    return (ObservableValue) param.getValue().getProperties().get(columnIndex);
                }
            });

            // override cell factory to custom editable cells
            column.setCellFactory(value -> new TopsoilTableCell());

            // disable column sorting
            column.setSortable(false);

            // add functional column to the array of columns
            result[i] = column;
        }

        return result;
    }

    /**
     * Create headers based on both isotope flavor and user input
     * @param headers array of provided headers
     * @return updated array of headers
     */
    private String[] createHeaders(String [] headers) {

        String [] result = new String[this.isotopeType.getHeaders().length];

        // populate headers with defaults if no headers are provided
        if (headers == null) {
            result = isotopeType.getHeaders();

        // if some headers are provided, populate
        } else if (headers.length < isotopeType.getHeaders().length) {
            int difference = isotopeType.getHeaders().length - headers.length;
            result = new String[isotopeType.getHeaders().length];
            int numHeaders = isotopeType.getHeaders().length;

            // Copy headers to result.
            System.arraycopy(headers, 0, result, 0, numHeaders - difference);

            // Fill in with normal headers.
            System.arraycopy(isotopeType.getHeaders(), (numHeaders - difference),
                    result, (numHeaders - difference),
                    (numHeaders - (numHeaders - difference)));

        // if too many headers are provided, only use the first X (depending on isotope flavor)
        } else { // if (headers.length >= isotopeType.getHeaders().length)
            result = headers.clone();
        }

        return result;
    }

    /**
     * Extract data from table as a list of maps of columns : value
     * @return list of maps of columns : value
     */
    public List<Map<String, Object>> extractData() {

        ArrayList<Map<String, Object>> result = new ArrayList<>();
        HashMap<String, Double> [] columnMaps = new HashMap[this.getHeaders().length];
        for (int i = 0; i < table.getItems().size(); i ++) {
            int columnIndex = i % headers.length;
            columnMaps[columnIndex].put(headers[columnIndex],
                    table.getItems().get(i).getProperties().get(columnIndex).getValue());
        }
        for (HashMap column : columnMaps) {
            result.add(column);
        }
        return result;

    }

    @Override
    public void deleteRow(int index) {
        this.dataEntries[index].getProperties().remove(0, headers.length);
    }

    @Override
    public void addRow() {
        TopsoilDataEntry dataEntry = new TopsoilDataEntry();
        for (String header : this.headers) {
            dataEntry.addEntries(0.0);
        }
        this.table.getItems().add(dataEntry);
    }

    @Override
    public void clear() {
        this.getTable().getItems().clear();
    }

    @Override
    public TableView getTable() {
        return this.table;
    }

    public IsotopeType getIsotopeType() {
        return this.isotopeType;
    }

    public void setIsotopeType(IsotopeType isotopeType) {
        this.isotopeType = isotopeType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String [] getHeaders() {
        return this.headers.clone();
    }

    public void resetIds() {
        int id = 0;
        for (TableColumn<TopsoilDataEntry, ?> column : this.table.getColumns()) {
            column.setId(Integer.toString(id));
            id++;
        }
    }

    public boolean isCleared() {
        boolean rtnval = true;
        if (table.getItems().size() != 1) {
            rtnval = false;
        } else {
            for (int i = 0; i < table.getColumns().size(); i++) {
                if (Double.compare(table.getItems().get(0).getProperties().get(i).doubleValue(), 0.0) != 0) {
                    rtnval = false;
                }
            }
        }
        return rtnval;
    }

    public Collection<PlotInformation> getOpenPlots() {
        return this.openPlots.values();
    }

    public void addOpenPlot(PlotInformation plotInfo) {
        this.openPlots.put(plotInfo.getTopsoilPlotType().getName(), plotInfo);
    }

    public void removeOpenPlot(TopsoilPlotType plotType) {
        this.openPlots.remove(plotType.getName());
    }
}
