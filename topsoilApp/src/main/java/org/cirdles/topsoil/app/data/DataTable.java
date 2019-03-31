package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.util.ListUtils;
import org.cirdles.topsoil.app.util.NumberColumnStringConverter;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.util.*;

/**
 * A full table of data loaded from a single file or string.
 *
 * @author marottajb
 */
public class DataTable extends Observable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String DEFAULT_LABEL = "NewTable";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Map<Variable<?>, DataColumn<?>> varMap = new HashMap<>();
    private ColumnRoot columnRoot;
    private DataRoot dataRoot;
    private DataTemplate template;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    protected StringProperty label;
    public StringProperty labelProperty() {
        if (label == null) {
            label = new SimpleStringProperty(DEFAULT_LABEL);
        }
        return label;
    }
    public String getLabel() { return labelProperty().get(); }
    public void setLabel(String label) { labelProperty().set(label); }

    private ObjectProperty<IsotopeSystem> isotopeSystem;
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        if (isotopeSystem == null) {
            isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
        }
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() { return isotopeSystemProperty().get(); }
    public final void setIsotopeSystem(IsotopeSystem type ) { isotopeSystemProperty().set(type); }

    private ObjectProperty<Uncertainty> uncertainty;
    public ObjectProperty<Uncertainty> uncertaintyProperty() {
        if (uncertainty == null) {
            uncertainty = new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
        }
        return uncertainty;
    }
    public final Uncertainty getUncertainty() { return uncertaintyProperty().get(); }
    public final void setUncertainty(Uncertainty unct) { uncertaintyProperty().set(unct); }

    private SetProperty<DataRow> dataRows;
    public SetProperty<DataRow> dataRowsProperty() {
        if (dataRows == null) {
            List<ObservableList<DataRow>> rowLists = new ArrayList<>();
            for (DataSegment segment : getDataRoot().getChildren()) {
                rowLists.add(segment.getChildren());
            }
            dataRows = new SimpleSetProperty<>(ListUtils.mergeObservableLists(rowLists));
        }
        return dataRows;
    }
    public final Set<DataRow> getDataRows() {
        return dataRowsProperty().get();
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

        // Notify observers on row selection changes
        for (DataRow row : getDataRows()) {
            row.selectedProperty().addListener(c -> {
                setChanged();
                notifyObservers();
            });
        }

        // Sets the fraction digits for each column to align values by their decimal separators
        for (DataColumn<?> column : this.columnRoot.getLeafNodes()) {
            if (column.getType() == Number.class) {
                setFractionDigitsForNumberColumn((DataColumn<Number>) column);
            }
        }
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

    public DataTemplate getTemplate() {
        return template;
    }

    /**
     * Returns a {@code Map} of variable/column associations for the table. The {@code Variable} key is represented by
     * the corresponding {@code DataColumn} value in the table.
     *
     * @return  Map of Variable/DataColumn associations
     */
    public Map<Variable<?>, DataColumn<?>> getVariableColumnMap() {
        return HashBiMap.create(varMap);
    }

    /**
     * Assigns a {@code DataColumn} from the table to the provided {@code Variable}.
     *
     * @param var   Variable
     * @param col   DataColumn
     * @return      the previous DataColumn assignment, if it exists
     */
    public DataColumn<?> setColumnForVariable(Variable<?> var, DataColumn<?> col) {
        return varMap.putIfAbsent(var, col);
    }

    /**
     * A convenience method for setting all variable/column mappings for the table.
     *
     * @param map   a Map containing the new mappings
     */
    public void setColumnsForAllVariables(Map<Variable<?>, DataColumn<?>> map) {
        varMap.clear();
        if (map != null) {
            for (Map.Entry<Variable<?>, DataColumn<?>> entry : map.entrySet()) {
                varMap.putIfAbsent(entry.getKey(), entry.getValue());
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

    @Override
    public String toString() {
        return getLabel();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void setFractionDigitsForNumberColumn(DataColumn<Number> column) {
        int maxFractionDigits = 0;
        for (Number n : getValuesForColumn(column)) {
            maxFractionDigits = Math.max(maxFractionDigits, NumberColumnStringConverter.countFractionDigits(n));
        }
        ((NumberColumnStringConverter) column.getStringConverter()).setNumFractionDigits(maxFractionDigits);
    }

}
