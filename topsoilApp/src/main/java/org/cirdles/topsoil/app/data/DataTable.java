package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBiMap;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.column.NumberColumnStringConverter;
import org.cirdles.topsoil.app.control.undo.UndoAction;
import org.cirdles.topsoil.app.control.undo.UndoManager;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.util.*;

/**
 * A full table of data loaded from a single file or string.
 *
 * @author marottajb
 */
public class DataTable implements ObservableObjectValue<DataTable> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String DEFAULT_LABEL = "NewTable";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private ColumnRoot columnRoot;
    private DataRoot dataRoot;
    private DataTemplate template;
    private UndoManager undoManager = new UndoManager(50);
    private ExpressionHelper<DataTable> helper;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private StringProperty label = new SimpleStringProperty(DEFAULT_LABEL);
    public StringProperty labelProperty() {
        return label;
    }
    public final String getLabel() { return label.get(); }
    public final void setLabel(String label) { this.label.set(label); }

    private ObjectProperty<IsotopeSystem> isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() { return isotopeSystem.get(); }
    public final void setIsotopeSystem(IsotopeSystem system) { isotopeSystem.set(system); }

    private ObjectProperty<Uncertainty> uncertainty = new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
    public ObjectProperty<Uncertainty> uncertaintyProperty() {
        return uncertainty;
    }
    public final Uncertainty getUncertainty() { return uncertainty.get(); }
    public final void setUncertainty(Uncertainty uncertainty) { this.uncertainty.set(uncertainty); }

    /**
     * The maximum number of fraction digits for the table. A value of -1 signifies no restriction.
     */
    private IntegerProperty fractionDigits = new SimpleIntegerProperty(-1);
    public IntegerProperty fracionDigitsProperty() {
        return fractionDigits;
    }
    public final int getMaxFractionDigits() {
        return fractionDigits.get();
    }
    public final void setMaxFractionDigits(int n) {
        fractionDigits.set(n);
        updateFractionDigits();
    }

    /**
     * This is a property with a bi-directional map representing associations between plotting variables and data columns
     * in the table. Internally, it is a {@link HashBiMap}, so uniqueness among values is maintained.
     */
    private final MapProperty<Variable<?>, DataColumn<?>> variableColumnMap = new SimpleMapProperty<>(FXCollections.observableMap(HashBiMap.create()));
    public MapProperty<Variable<?>, DataColumn<?>> variableColumnMapProperty() {
        return variableColumnMap;
    }
    public Map<Variable<?>, DataColumn<?>> getVariableColumnMap() {
        return variableColumnMap.get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataTable(DataTemplate template, String label, ColumnRoot columnRoot, DataRoot dataRoot) {
        this(template, label, columnRoot, dataRoot, IsotopeSystem.GENERIC, Uncertainty.ONE_SIGMA_ABSOLUTE);
    }

    public DataTable(DataTemplate template, String label, ColumnRoot columnRoot, DataRoot dataRoot,
                     IsotopeSystem isotopeSystem, Uncertainty uncertainty) {
        setLabel(label);
        this.template = template;
        setIsotopeSystem(isotopeSystem);
        setUncertainty(uncertainty);
        this.columnRoot = columnRoot;
        this.dataRoot = dataRoot;
        this.dataRoot.labelProperty().bind(labelProperty());

        this.label.addListener(c -> fireValueChangedEvent());
        this.fractionDigits.addListener(c -> fireValueChangedEvent());
        this.isotopeSystem.addListener(c -> fireValueChangedEvent());
        this.uncertainty.addListener(c -> fireValueChangedEvent());
        this.variableColumnMap.addListener(
                (MapChangeListener<? super Variable<?>, ? super DataColumn<?>>) c -> fireValueChangedEvent()
        );

        for (DataRow row : getDataRows()) {
            row.selectedProperty().addListener(c -> fireValueChangedEvent());
            for (DataRow.DataValue<?> value : row.getValueMap().values()) {
                value.valueProperty().addListener(c -> fireValueChangedEvent());
            }
        }

        // Sets the fraction digits for each column to align values by their decimal separators
        updateFractionDigits();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ColumnRoot getColumnRoot() {
        return columnRoot;
    }

    public List<DataColumn<?>> getDataColumns() {
        return columnRoot.getLeafNodes();
    }

    public DataRoot getDataRoot() {
        return dataRoot;
    }

    public List<DataRow> getDataRows() {
        List<DataRow> rows = new ArrayList<>();
        for (DataSegment segment : dataRoot.getChildren()) {
            rows.addAll(segment.getChildren());
        }
        return rows;
    }

    public DataTemplate getTemplate() {
        return template;
    }

    public DataColumn<?> getColumnForVariable(Variable<?> variable) {
        return variableColumnMap.get(variable);
    }

    public Variable<?> getVariableForColumn(DataColumn<?> column) {
        return HashBiMap.create(variableColumnMap).inverse().get(column);
    }

    /**
     * Assigns a {@code DataColumn} from the table to the provided {@code Variable}.
     *
     * @param var   Variable
     * @param col   DataColumn
     * @return      the previous DataColumn assignment, if it exists
     */
    public DataColumn<?> setColumnForVariable(Variable<?> var, DataColumn<?> col) {
        return variableColumnMap.putIfAbsent(var, col);
    }

    /**
     * A convenience method for setting all variable/column mappings for the table.
     *
     * @param map   a Map containing the new mappings
     */
    public void setColumnsForAllVariables(Map<Variable<?>, DataColumn<?>> map) {
        variableColumnMap.clear();
        if (map != null) {
            for (Map.Entry<Variable<?>, DataColumn<?>> entry : map.entrySet()) {
                variableColumnMap.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Returns a list containing the value contained in each {@code DataRow} for the specified {@code DataColumn}.
     *
     * @param column    DataColumn
     * @param <T>       type of DataColumn
     *
     * @return          List of values for column
     */
    public <T> List<T> getValuesForColumn(DataColumn<T> column) {
        if (column == null) {
            throw new IllegalArgumentException("column cannot be null.");
        }
        if (! getDataColumns().contains(column)) {
            throw new IllegalArgumentException("DataColumn not in table.");
        }

        List<T> values = new ArrayList<>();
        for (DataRow row : dataRoot.getLeafNodes()) {
            if (row.getValueForColumn(column) == null) {
                values.add(null);
            } else {
                values.add(row.getValueForColumn(column).getValue());
            }
        }
        return values;
    }

    public void addUndoAction(UndoAction action) {
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

    public String lastRedoName(){
        return undoManager.getRedoName();
    }

    /** {@inheritDoc} */
    @Override
    public DataTable get() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(InvalidationListener listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(InvalidationListener listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(ChangeListener<? super DataTable> listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(ChangeListener<? super DataTable> listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    /** {@inheritDoc} */
    @Override
    public DataTable getValue() {
        return get();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getLabel();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void updateFractionDigits() {
        for (DataColumn<?> column : getDataColumns()) {
            if (column.getType() == Number.class) {
                updateFractionDigitsForNumberColumn((DataColumn<Number>) column);
            }
        }
    }

    /**
     * Checks the maximum number of fraction digits for the values for the specified number column, and appropriately
     * sets its {@link NumberColumnStringConverter}.
     *
     * @param column    DataColumn of type Number
     */
    private void updateFractionDigitsForNumberColumn(DataColumn<Number> column) {
        int n = -1;
        if (fractionDigits.get() == -1) {
            for (Number value : getValuesForColumn(column)) {
                n = Math.max(n, DataUtils.countFractionDigits(value));
            }
        } else {
            n = fractionDigits.get();
        }
        ((NumberColumnStringConverter) column.getStringConverter()).setNumFractionDigits(n);
    }

    /**
     * Sends notifications to all attached
     * {@link javafx.beans.InvalidationListener InvalidationListeners} and
     * {@link javafx.beans.value.ChangeListener ChangeListeners}.
     */
    private void fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(helper);
    }

}
