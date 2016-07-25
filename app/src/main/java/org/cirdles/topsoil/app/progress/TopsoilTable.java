package org.cirdles.topsoil.app.progress;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

/**
 * Created by benjaminmuldrow on 7/6/16.
 */
public class TopsoilTable {

    private final Alerter alerter = new ErrorAlerter();
    private String[] headers;
    private TableView<TopsoilDataEntry> table;
    private IsotopeType isotopeType;
    private String title = "Untitled Table";

    public TopsoilTable(String [] headers, IsotopeType isotopeType, TopsoilDataEntry... dataEntries) {

        // initialize table
        this.table = new TableView<>();
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableView.TableViewSelectionModel selectionModel = this.table.getSelectionModel();
        selectionModel.setCellSelectionEnabled(true);

        // initialize isotope type
        this.isotopeType = isotopeType;

        // populate headers
        this.headers = createHeaders(headers);

        // populate table
        this.table.getColumns().addAll(createColumns(this.headers));
        if (dataEntries.length == 0) {
            this.table.getItems().add(new TopsoilDataEntry());
        } else {
            this.table.getItems().addAll(dataEntries);
        }

    }

    /**
     * Create functional TableColumns dynamically based on header count
     * @param headers Array of header strings
     * @return an Array of functional Table Columns
     */
    private TableColumn [] createColumns(String [] headers) {

        TableColumn [] result = new TableColumn[headers.length];

        for (int i = 0; i < headers.length; i ++) {

            TableColumn<TopsoilDataEntry, Double> column = new TableColumn<>(headers[i]);
            final int columnIndex = i;

            // override cell value factory to accept the i'th index of a data entry for the i'th column
            column.setCellValueFactory(param -> {
                if (param.getValue().getProperties().size() == 0) {
                    return (ObservableValue) new SimpleDoubleProperty(0.0);
                } else {
                    return (ObservableValue) param.getValue().getProperties().get(columnIndex);
                }
            });

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
            for (int i = 0; i < isotopeType.getHeaders().length - difference; i++) {
                result[i] = headers[i];
            }
            for (int i = isotopeType.getHeaders().length - difference;
                    i < isotopeType.getHeaders().length; i++) {
                result[i] = isotopeType.getHeaders()[i];
            }

        // if too many headers are provided, only use the first X (depending on isotope flavor)
        } else { // if (headers.length >= isotopeType.getHeaders().length)
            for (int i = 0; i < isotopeType.getHeaders().length; i ++) {
                result[i] = headers[i];
            }
        }

        return result;
    }

    public TableView getTable() {
        return this.table;
    }

    public IsotopeType getIsotopeType() {
        return this.isotopeType;
    }

    public String getTitle() {
        return title;
    }
}
