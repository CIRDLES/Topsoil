package org.cirdles.topsoil.app.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.model.composite.DataComposite;
import org.cirdles.topsoil.app.model.composite.DataComponent;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Represents a table of column-based data. Each {@code DataTable} is composed of one or more {@code DataSegment}s,
 * which contain {@link DataRow}s representing single entries of data. A data table has a corresponding
 * {@link ColumnTree}, which defines the structure of the table's columns. Each {@code DataRow} contains {@code
 * DataValue}s which map {@code DataColumn}s to the row's values.
 *
 * @author marottajb
 *
 * @see ColumnTree
 * @see DataComposite
 * @see DataRow
 * @see DataSegment
 * @see DataValue
 */
public class DataTable extends DataComposite<DataSegment> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 2353637336965993167L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    /**
     * A bi-directional map of {@code Variable}s to the columns that are assigned to them for the table.
     */
    private HashBiMap<Variable<?>, DataColumn<?>> varMap = HashBiMap.create();
    private ColumnTree columnTree;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private transient final ObjectProperty<IsotopeSystem> isotopeSystem =
            new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() {
        return isotopeSystem.get();
    }
    public final void setIsotopeSystem(IsotopeSystem type ) {
        isotopeSystem.set(type);
    }

    private transient final ObjectProperty<Uncertainty> unctFormat =
            new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
    public ObjectProperty<Uncertainty> unctFormatProperty() {
        return unctFormat;
    }
    public final Uncertainty getUnctFormat() {
        return unctFormat.get();
    }
    public final void setUnctFormat(Uncertainty format) {
        unctFormat.set(format);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataTable(String label, ColumnTree columnTree, List<DataSegment> dataSegments) {
        this(label, columnTree, dataSegments, IsotopeSystem.GENERIC, Uncertainty.ONE_SIGMA_ABSOLUTE);
    }

    public DataTable(String label, ColumnTree columnTree, List<DataSegment> dataSegments,
                     IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
        super(label);
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
        this.columnTree = columnTree;
        this.children.addAll(dataSegments);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ColumnTree getColumnTree() {
        return columnTree;
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

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataTable) {
            DataTable other = (DataTable) object;
            if (! super.equals(object)) {
                return false;
            }
            if (this.getIsotopeSystem() != other.getIsotopeSystem()) {
                return false;
            }
            if (this.getUnctFormat() != other.getUnctFormat()) {
                return false;
            }
            if (! this.getColumnTree().equals(other.getColumnTree())) {
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
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("varMap", varMap);
        fields.put("columnTree", columnTree);
        out.writeFields();
        out.writeObject(isotopeSystem.get());
        out.writeObject(unctFormat.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        varMap = (HashBiMap<Variable<?>, DataColumn<?>>) fields.get("varMap", null);
        columnTree = (ColumnTree) fields.get("columnTree", null);
        isotopeSystem.set((IsotopeSystem) in.readObject());
        unctFormat.set((Uncertainty) in.readObject());
    }

}
