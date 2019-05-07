package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses {@code DataCategory} and {@code DataColumn} objects to model the structure of nested columns. Adheres to the
 * Composite pattern in that it implements the {@code DataComposite} interface, and is composed of {@code
 * DataComposite} objects.
 *
 * @author marottajb
 */
public class ColumnRoot extends DataComposite<DataComponent> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnRoot(DataComponent... topLevel) {
        super("root", topLevel);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public List<DataColumn<?>> getLeafNodes() {
        try {
            return leavesAsDataColumns(super.getLeafNodes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Helper method to capture wildcards from getLeafNodes().
     *
     * @param leaves    List of leaves in this tree
     * @param <T>       the type of the leaves in this tree
     *
     * @return          List of DataColumns
     *
     * @throws          Exception if a leaf is not an instance of DataColumn
     */
    private <T> List<DataColumn<?>> leavesAsDataColumns(List<T> leaves) throws Exception {
        List<DataColumn<?>> columns = new ArrayList<>();
        for (T leaf : leaves) {
            if (leaf instanceof DataColumn) {
                columns.add((DataColumn) leaf);
            } else {
                // this should not happen
                throw new Exception("Leaf is not an instance of DataColumn.");
            }
        }
        return columns;
    }


}
