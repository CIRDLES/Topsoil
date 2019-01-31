package org.cirdles.topsoil.app.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.node.BranchNode;
import org.cirdles.topsoil.app.data.node.DataNode;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;

import java.util.*;

/**
 * @author marottajb
 */
public class DataTable extends BranchNode<DataSegment> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final BiMap<Variable, DataColumn> variableColumnBiMap = HashBiMap.create();
    private ColumnTree columnTree;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private final ObjectProperty<IsotopeSystem> isotopeSystem = new SimpleObjectProperty<>(IsotopeSystem.GENERIC);
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
        return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() {
        return isotopeSystem.get();
    }
    public final void setIsotopeSystem(IsotopeSystem type ) {
        isotopeSystem.set(type);
    }

    private final ObjectProperty<UncertaintyFormat> unctFormat = new SimpleObjectProperty<>(UncertaintyFormat.ONE_SIGMA_ABSOLUTE);
    public ObjectProperty<UncertaintyFormat> unctFormatProperty() {
        return unctFormat;
    }
    public final UncertaintyFormat getUnctFormat() {
        return unctFormat.get();
    }
    public final void setUnctFormat(UncertaintyFormat format) {
        unctFormat.set(format);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataTable() { }

    public DataTable(String label, IsotopeSystem isotopeSystem, UncertaintyFormat unctFormat, List<DataNode> categories,
                     List<DataSegment> dataSegments) {
        super(label);
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
        this.columnTree = new ColumnTree(categories);
        this.children.addAll(dataSegments);
    }

    public DataTable(String label, IsotopeSystem isotopeSystem, UncertaintyFormat unctFormat, ColumnTree columnTree,
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

    public BiMap<Variable, DataColumn> getVariableColumnMap() {
        return HashBiMap.create(variableColumnBiMap);
    }

    public DataColumn setColumnForVariable(Variable var, DataColumn col) {
        return variableColumnBiMap.putIfAbsent(var, col);
    }

    public void setColumnsForAllVariables(Map<Variable, DataColumn> map) {
        variableColumnBiMap.clear();
        variableColumnBiMap.putAll(map);
    }

}
