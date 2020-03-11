package org.cirdles.topsoil.app.data;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.data.AbstractDataTable;
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

public class FXDataTable extends AbstractDataTable<FXDataColumn<?>, FXDataRow> {

    private ReadOnlyStringWrapper titleProperty = new ReadOnlyStringWrapper();
    public final ReadOnlyStringProperty titleProperty() {
        return titleProperty.getReadOnlyProperty();
    }
    @Override
    public final String getTitle() {
        return titleProperty.get();
    }
    @Override
    public final void setTitle(String title) {
        this.titleProperty.set(title);
    }

    private ReadOnlyListWrapper<FXDataColumn<?>> columnsListProperty = new ReadOnlyListWrapper<>(
            // Initializes the list property with an observable list that listens to changes in the properties of each
            // column
            FXCollections.observableArrayList((FXDataColumn<?> column) -> {
                return new Observable[]{
                        column.titleProperty(),
                        column.selectedProperty(),
                        column.childColumnsProperty()
                };
            })
    );
    public final ReadOnlyListProperty<FXDataColumn<?>> columnsProperty() {
        return columnsListProperty.getReadOnlyProperty();
    }
    @Override
    public final ObservableList<FXDataColumn<?>> getColumns() {
        return columnsListProperty.get();
    }

    private ReadOnlyListWrapper<FXDataRow> rowsListProperty = new ReadOnlyListWrapper<>(
            // Initializes the list property with an observable list that listens to changes in the properties of each
            // row
            FXCollections.observableArrayList((FXDataRow row) -> {
                return new Observable[]{
                        row.titleProperty(),
                        row.selectedProperty(),
                        row.columnMapReadOnlyProperty(),
                        row.childRowsProperty()
                };
            })
    );
    public final ReadOnlyListProperty<FXDataRow> rowsProperty() {
        return rowsListProperty.getReadOnlyProperty();
    }
    @Override public final ObservableList<FXDataRow> getRows() {
        return rowsListProperty.get();
    }

    private ReadOnlyIntegerWrapper maxFractionDigits = new ReadOnlyIntegerWrapper(-1);
    public final ReadOnlyIntegerProperty maxFractionDigitsProperty() {
        return maxFractionDigits.getReadOnlyProperty();
    }
    public final int getMaxFractionDigits() {
        return maxFractionDigits.get();
    }
    public final void setMaxFractionDigits(int n) {
        maxFractionDigits.set(n);
    }

    private ReadOnlyBooleanWrapper isScientificNotation = new ReadOnlyBooleanWrapper(false);
    public ReadOnlyBooleanProperty scientificNotationProperty() {
        return isScientificNotation.getReadOnlyProperty();
    }
    public Boolean isScientificNotation() {
        return isScientificNotation.get();
    }
    public void setScientificNotation(boolean value) {
        isScientificNotation.set(value);
    }

    private ReadOnlyObjectWrapper<Uncertainty> uncertainty = new ReadOnlyObjectWrapper<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
    public final ReadOnlyObjectProperty<Uncertainty> uncertaintyProperty() {
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

    private UndoManager undoManager = new UndoManager(50);

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public FXDataTable(DataTable<FXDataColumn<?>, FXDataRow> table) {
        this(
                table.getTemplate(),
                table.getTitle(),
                table.getUncertainty(),
                table.getColumns(),
                table.getRows()
        );
    }

    public FXDataTable(DataTemplate template, String title, Uncertainty uncertainty, List<FXDataColumn<?>> columns, List<FXDataRow> rows) {
        super(template, title, uncertainty, columns, rows);

        this.columnsListProperty.addAll(this.columns);
        this.rowsListProperty.addAll(this.rows);
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
