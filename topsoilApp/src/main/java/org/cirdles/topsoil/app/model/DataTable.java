package org.cirdles.topsoil.app.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.model.generic.BranchNode;
import org.cirdles.topsoil.app.model.generic.DataNode;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * @author marottajb
 */
public class DataTable extends BranchNode<DataSegment> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 2353637336965993167L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

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
        super(label);
        setIsotopeSystem(IsotopeSystem.GENERIC);
        setUnctFormat(Uncertainty.ONE_SIGMA_ABSOLUTE);
        this.columnTree = columnTree;
        this.children.addAll(dataSegments);
    }

    public DataTable(String label, IsotopeSystem isotopeSystem, Uncertainty unctFormat, List<DataNode> categories,
                     List<DataSegment> dataSegments) {
        super(label);
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
        this.columnTree = new ColumnTree(categories);
        this.children.addAll(dataSegments);
    }

    public DataTable(String label, IsotopeSystem isotopeSystem, Uncertainty unctFormat, ColumnTree columnTree,
                     List<DataSegment> dataSegments) {
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

    public void setColumnsForAllVariables(Map<Variable<?>, DataColumn<?>> map) {
        varMap.clear();
        varMap.putAll(map);
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
