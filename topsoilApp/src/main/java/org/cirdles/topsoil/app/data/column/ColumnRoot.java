package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        return leavesAsDataColumns(super.getLeafNodes());
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
     * @return          List o
     */
    private <T> List<DataColumn<?>> leavesAsDataColumns(List<T> leaves) {
        List<DataColumn<?>> columns = new ArrayList<>();
        for (T leaf : leaves) {
            if (leaf instanceof DataColumn) {
                columns.add((DataColumn) leaf);
            } else {
                // @TODO Probably better to throw an exception here
                return null;
            }
        }
        return columns;
    }


}
