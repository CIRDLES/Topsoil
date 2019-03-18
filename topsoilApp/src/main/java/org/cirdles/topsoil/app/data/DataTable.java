package org.cirdles.topsoil.app.data;

import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.util.NumberColumnStringConverter;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.util.*;

/**
 *
 * @author marottajb
 *
 * @see ColumnRoot
 * @see DataComposite
 * @see DataRow
 * @see DataSegment
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

    protected transient StringProperty label;
    public StringProperty labelProperty() {
        if (label == null) {
            label = new SimpleStringProperty(DEFAULT_LABEL);
        }
        return label;
    }
    public String getLabel() { return labelProperty().get(); }
    public void setLabel(String label) { labelProperty().set(label); }

    private transient ObjectProperty<IsotopeSystem> isotopeSystem;
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        if (isotopeSystem == null) {
            isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
        }
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() { return isotopeSystemProperty().get(); }
    public final void setIsotopeSystem(IsotopeSystem type ) { isotopeSystemProperty().set(type); }

    private transient ObjectProperty<Uncertainty> uncertainty;
    public ObjectProperty<Uncertainty> uncertaintyProperty() {
        if (uncertainty == null) {
            uncertainty = new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
        }
        return uncertainty;
    }
    public final Uncertainty getUncertainty() { return uncertaintyProperty().get(); }
    public final void setUncertainty(Uncertainty unct) { uncertaintyProperty().set(unct); }

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
        for (DataRow row : this.dataRoot.getLeafNodes()) {
            row.selectedProperty().addListener(c -> {
                setChanged();
                notifyObservers();
            });
        }
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

    public List<DataRow> getDataRows() {
        return dataRoot.getLeafNodes();
    }

    public DataTemplate getTemplate() {
        return template;
    }

    public Map<Variable<?>, DataColumn<?>> getVariableColumnMap() {
        return HashBiMap.create(varMap);
    }

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
        for (Map.Entry<Variable<?>, DataColumn<?>> entry : map.entrySet()) {
            varMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
    }

    DataRow getRowByIndex(int index) {
        DataSegment segment;
        int rowCount = 0;
        for (int segIndex = 0; segIndex < getDataRoot().getChildren().size(); segIndex++) {
            segment = getDataRoot().getChildren().get(segIndex);
            if (index < rowCount + segment.getChildren().size()) {
                return segment.getChildren().get(index - rowCount);
            }
            rowCount += segment.getChildren().size();
        }
        return null;
    }

    <T> List<T> getValuesForColumn(DataColumn<T> column) {
        List<T> values = new ArrayList<>();
        for (DataRow row : this.getDataRows()) {
            if (row.getPropertyForColumn(column) == null) {
                values.add(null);
            } else {
                values.add(row.getPropertyForColumn(column).getValue());
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
