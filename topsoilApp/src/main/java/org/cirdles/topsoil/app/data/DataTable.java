package org.cirdles.topsoil.app.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.app.data.value.DataValue;
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

    private static final long serialVersionUID = -2011274290514367222L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    /**
     * A bi-directional map of {@code Variable}s to the columns that are assigned to them for the table.
     */
    private HashBiMap<Variable<?>, DataColumn<?>> varMap = HashBiMap.create();
    private ColumnTree columnTree;
    private DataTemplate template;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private transient ObjectProperty<IsotopeSystem> isotopeSystem;
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        if (isotopeSystem == null) {
            isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
        }
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() {
        return isotopeSystemProperty().get();
    }
    public final void setIsotopeSystem(IsotopeSystem type ) {
        isotopeSystemProperty().set(type);
    }

    private transient ObjectProperty<Uncertainty> unctFormat;
    public ObjectProperty<Uncertainty> unctFormatProperty() {
        if (unctFormat == null) {
            unctFormat = new SimpleObjectProperty<>(Uncertainty.ONE_SIGMA_ABSOLUTE);
        }
        return unctFormat;
    }
    public final Uncertainty getUnctFormat() {
        return unctFormatProperty().get();
    }
    public final void setUnctFormat(Uncertainty format) {
        unctFormatProperty().set(format);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataTable(DataTemplate template, String label, ColumnTree columnTree, List<DataSegment> dataSegments) {
        this(template, label, columnTree, dataSegments, IsotopeSystem.GENERIC, Uncertainty.ONE_SIGMA_ABSOLUTE);
    }

    public DataTable(DataTemplate template, String label, ColumnTree columnTree, List<DataSegment> dataSegments,
                     IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
        super(label);
        this.template = template;
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
        // TODO Check that columnTree and dataSegments match columns
        this.columnTree = columnTree;
        this.children.addAll(dataSegments);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public ColumnTree getColumnTree() {
        return columnTree;
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
        for (int segIndex = 0; segIndex < this.getChildren().size(); segIndex++) {
            segment = getChildren().get(segIndex);
            if (index < rowCount + segment.getChildren().size()) {
                return segment.getChildren().get(index - rowCount);
            }
            rowCount += segment.getChildren().size();
        }
        return null;
    }

    public List<DataRow> getDataRows() {
        List<DataRow> rows = new ArrayList<>();
        for (DataSegment segment : getChildren()) {
            rows.addAll(segment.getChildren());
        }
        return rows;
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
        out.defaultWriteObject();
        out.writeObject(varMap);
        out.writeObject(columnTree);
        out.writeObject(getIsotopeSystem());
        out.writeObject(getUnctFormat());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        varMap = (HashBiMap<Variable<?>, DataColumn<?>>) in.readObject();
        columnTree = (ColumnTree) in.readObject();
        setIsotopeSystem((IsotopeSystem) in.readObject());
        setUnctFormat((Uncertainty) in.readObject());
    }

    /**
     * Helper method to capture wildcards from getLeafNodes().
     *
     * @param leaves    List of leaves in this tree
     * @param <T>       the type of the leaves in this tree
     * @return          List o
     */
    private <T> List<DataRow> leafHelper(List<T> leaves) {
        List<DataRow> rows = new ArrayList<>();
        for (T leaf : leaves) {
            if (leaf instanceof DataRow) {
                rows.add((DataRow) leaf);
            } else {
                // @TODO Probably better to throw an exception here
                return null;
            }
        }
        return rows;
    }

}
