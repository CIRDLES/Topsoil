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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author marottajb
 */
public class DataTable extends BranchNode<DataSegment> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private final BranchNode<DataNode> rootColumnNode = new BranchNode<>("");
    private final BiMap<Variable, DataColumn> variableColumnBiMap = HashBiMap.create();

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /**
     * The {@code isotope system} of the data provided.
     */
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

    /**
     * The {@code UncertaintyFormat} of provided uncertainty values.
     */
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

    public DataTable(String title, IsotopeSystem isotopeSystem, UncertaintyFormat unctFormat, List<DataNode> categories,
                     List<DataSegment> dataSegments) {
        super(title);
        setIsotopeSystem(isotopeSystem);
        setUnctFormat(unctFormat);
        rootColumnNode.getChildren().addAll(categories);
        this.children.addAll(dataSegments);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public BranchNode<DataNode> getRootColumnNode() {
        return rootColumnNode;
    }

    public Map<Variable, DataColumn> getVariableColumnMap() {
        return new HashMap<>(variableColumnBiMap);
    }

    public DataColumn setColumnForVariable(Variable var, DataColumn col) {
        return variableColumnBiMap.putIfAbsent(var, col);
    }

    public void setColumnsForAllVariables(Map<Variable, DataColumn> map) {
        variableColumnBiMap.clear();
        variableColumnBiMap.putAll(map);
    }

    public int colCount() {
        return rootColumnNode.countTotalLeafNodes();
    }

    public int rowCount() {
        return this.countTotalLeafNodes();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

}
