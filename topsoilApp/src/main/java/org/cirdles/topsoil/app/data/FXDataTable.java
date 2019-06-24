package org.cirdles.topsoil.app.data;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.app.control.undo.UndoAction;
import org.cirdles.topsoil.app.control.undo.UndoManager;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataRow;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.DataTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FXDataTable implements DataTable {

    private StringProperty title = new SimpleStringProperty();
    public final StringProperty titleProperty() {
        return title;
    }
    @Override
    public final String getTitle() {
        return title.get();
    }
    @Override
    public final void setTitle(String title) {
        this.title.set(title);
    }

    private ListProperty<FXDataColumn<?>> columns = new SimpleListProperty<>(
            // Initializes the list property with an observable list that listens to changes in the properties of each
            // column
            FXCollections.observableArrayList(column -> {
                return new Observable[]{
                        column.titleProperty(),
                        column.selectedProperty(),
                        column.childColumnsProperty()
                };
            })
    );
    public final ReadOnlyListProperty<FXDataColumn<?>> columnsProperty() {
        return columns;
    }
    @Override
    public final ObservableList<FXDataColumn<?>> getColumns() {
        return columns.get();
    }

    private ListProperty<FXDataRow> rows = new SimpleListProperty<>(
            // Initializes the list property with an observable list that listens to changes in the properties of each
            // row
            FXCollections.observableArrayList(row -> {
                return new Observable[]{
                        row.titleProperty(),
                        row.selectedProperty(),
                        row.columnMapReadOnlyProperty(),
                        row.childRowsProperty()
                };
            })
    );
    public final ReadOnlyListProperty<FXDataRow> rowsProperty() {
        return rows;
    }
    @Override public final ObservableList<FXDataRow> getRows() {
        return rows.get();
    }

    private IntegerProperty maxFractionDigits = new SimpleIntegerProperty(-1);
    public final IntegerProperty maxFractionDigitsProperty() {
        return maxFractionDigits;
    }
    public final int getMaxFractionDigits() {
        return maxFractionDigits.get();
    }
    public final void setMaxFractionDigits(int n) {
        maxFractionDigits.set(n);
    }

    private BooleanProperty isScientificNotation = new SimpleBooleanProperty(false);
    public BooleanProperty scientificNotationProperty() {
        return isScientificNotation;
    }
    public Boolean isScientificNotation() {
        return isScientificNotation.get();
    }
    public void setScientificNotation(boolean value) {
        isScientificNotation.set(value);
    }

    private ObjectProperty<Uncertainty> uncertainty = new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
    public final ObjectProperty<Uncertainty> uncertaintyProperty() {
        return uncertainty;
    }
    public final Uncertainty getUncertainty() {
        return uncertainty.get();
    }
    public final void setUncertainty(Uncertainty u) {
        if (u == null) {
            throw new IllegalArgumentException("Uncertainty cannot be null.");
        }
        uncertainty.set(u);
    }

    private final DataTemplate template;
    private UndoManager undoManager = new UndoManager(50);

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public FXDataTable(DataTable table) {
        this.template = table.getTemplate();
        setTitle(table.getTitle());
        setUncertainty(table.getUncertainty());

        // Create FXDataColumns for each column
        List<? extends DataColumn<?>> columns = table.getColumns();
        for (DataColumn<?> column : columns) {
            this.columns.add(new FXDataColumn<>(column));
        }

        // Create FXDataRows for each row, and associate their values with the newly created FXDataColumn
        this.rows.addAll(mapRowsToFXDataColumns(table.getRows(), this.columns));
    }

    public FXDataTable(DataTemplate template, String title, List<FXDataColumn<?>> columns, List<FXDataRow> rows) {
        this.template = template;
        setTitle(title);

        if (columns != null) {
            this.columns.addAll(columns);
        }
        if (rows != null) {
            this.rows.addAll(rows);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public DataTemplate getTemplate() {
        return template;
    }

    public void addUndo(UndoAction action) {
        undoManager.add(action);
    }

    public void undoLastAction() {
        undoManager.undo();
    }

    public void redoLastAction() {
        undoManager.redo();
    }

    public String lastUndoName() {
        return undoManager.getUndoName();
    }

    public String lastRedoName() {
        return undoManager.getRedoName();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private List<FXDataRow> mapRowsToFXDataColumns(List<? extends DataRow> dataRows, List<FXDataColumn<?>> fxColumns) {
        List<FXDataRow> fxRows = new ArrayList<>();
        FXDataRow newRow;
        FXDataColumn<?> col;
        for (DataRow oldRow : dataRows) {
            newRow = new FXDataRow(oldRow.getTitle(), oldRow.isSelected());

            if (oldRow.countChildren() > 0) {
                newRow.getChildren().addAll(mapRowsToFXDataColumns(oldRow.getChildren(), fxColumns));
            } else {
                Map<DataColumn<?>, Object> map = new HashMap<>(oldRow.getColumnValueMap());
                for (Map.Entry<DataColumn<?>, Object> entry : map.entrySet()) {
                    for (FXDataColumn<?> fxColumn : fxColumns) {
                        col = (FXDataColumn<?>) fxColumn.find(entry.getKey().getTitle());
                        if (col != null) {
                            newRow.setValueForColumnUnsafe(col, entry.getValue());
                            break;
                        }
                    }
                }
            }

            fxRows.add(newRow);
        }
        return fxRows;
    }

}
