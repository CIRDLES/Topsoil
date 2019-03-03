package org.cirdles.topsoil.app.data;

import com.google.common.collect.BiMap;
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
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a table of column-based data. Each {@code DataTable} is composed of one or more {@code DataSegment}s,
 * which contain {@link DataRow}s representing single entries of data. A data table has a corresponding
 * {@link ColumnRoot}, which defines the structure of the table's columns. Each {@code DataRow} contains {@code
 * DataValue}s which map {@code DataColumn}s to the row's values.
 *
 * @author marottajb
 *
 * @see ColumnRoot
 * @see DataComposite
 * @see DataRow
 * @see DataSegment
 */
public class DataTable extends Observable implements Serializable {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -2011274290514367222L;
    private static final String DEFAULT_LABEL = "NewTable";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    /**
     * A bi-directional map of {@code Variable}s to the columns that are assigned to them for the table.
     */
    private HashBiMap<Variable<?>, DataColumn<?>> varMap = HashBiMap.create();
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

    private transient ObjectProperty<Uncertainty> unctFormat;
    public ObjectProperty<Uncertainty> unctFormatProperty() {
        if (unctFormat == null) {
            unctFormat = new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
        }
        return unctFormat;
    }
    public final Uncertainty getUnctFormat() { return unctFormatProperty().get(); }
    public final void setUnctFormat(Uncertainty format) { unctFormatProperty().set(format); }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataTable(DataTemplate template, String label, ColumnRoot columnRoot, DataRoot dataRoot) {
        this(template, label, columnRoot, dataRoot, IsotopeSystem.GENERIC, Uncertainty.ONE_SIGMA_ABSOLUTE);
    }

    public DataTable(DataTemplate template, String label, ColumnRoot columnRoot, DataRoot dataRoot,
                     IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
        setLabel(label);
        this.template = template;
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
        // TODO Check that columnRoot and dataSegments match columns
        this.columnRoot = columnRoot;
        this.dataRoot = dataRoot;
        this.dataRoot.labelProperty().bind(labelProperty());
        for (DataRow row : this.dataRoot.getLeafNodes()) {
            row.selectedProperty().addListener(c -> {
                setChanged();
                notifyObservers();
            });
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ColumnRoot getColumnRoot() {
        return columnRoot;
    }

    public DataRoot getDataRoot() {
        return dataRoot;
    }

    public DataTemplate getTemplate() {
        return template;
    }

    public BiMap<Variable<?>, DataColumn<?>> getVariableColumnMap() {
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

    public DataRow getRowByIndex(int index) {
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

    public <T extends Serializable> List<T> getValuesForColumn(DataColumn<T> column) {
        List<T> values = new ArrayList<>();
        for (DataRow row : this.getDataRoot().getLeafNodes()) {
            values.add(row.getPropertyForColumn(column).getValue());
        }
        return values;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataTable) {
            DataTable other = (DataTable) object;
            if (this.getIsotopeSystem() != other.getIsotopeSystem()) {
                return false;
            }
            if (this.getUnctFormat() != other.getUnctFormat()) {
                return false;
            }
            if (! this.getColumnRoot().equals(other.getColumnRoot())) {
                return false;
            }
            if (! this.getVariableColumnMap().equals(other.getVariableColumnMap())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getLabel());
        out.writeObject(varMap);
        out.writeObject(columnRoot);
        out.writeObject(getIsotopeSystem());
        out.writeObject(getUnctFormat());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setLabel(in.readUTF());
        varMap = (HashBiMap<Variable<?>, DataColumn<?>>) in.readObject();
        columnRoot = (ColumnRoot) in.readObject();
        setIsotopeSystem((IsotopeSystem) in.readObject());
        setUnctFormat((Uncertainty) in.readObject());
    }

}
