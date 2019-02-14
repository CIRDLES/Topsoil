package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.composite.DataComposite;
import org.cirdles.topsoil.app.model.composite.DataComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses {@code DataCategory} and {@code DataColumn} objects to model the structure of nested columns. Adheres to the
 * Composite pattern in that it implements the {@code DataComposite} interface, and is composed of {@code
 * DataComposite} objects.
 *
 * @author marottajb
 */
public class ColumnTree extends DataComposite<DataComponent> {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    private static final long serialVersionUID = 6635594781414924593L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnTree(List<? extends DataComponent> topLevel) {
        this.getChildren().addAll(topLevel);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public List<DataColumn<?>> getLeafNodes() {
        return leafHelper(super.getLeafNodes());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ColumnTree) {
            ColumnTree other = (ColumnTree) object;
            for (int i = 0; i < this.getChildren().size(); i++) {
                if (! this.getChildren().get(i).equals(other.getChildren().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
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
    private <T> List<DataColumn<?>> leafHelper(List<T> leaves) {
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
