package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.composite.DataComponent;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final long serialVersionUID = -3676485771872253255L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ColumnTree(DataComponent... topLevel) {
        for (DataComponent component : topLevel) {
            this.getChildren().add(component);
        }
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
            if (! this.getLabel().equals(other.getLabel())) {
                return false;
            }

            for (int i = 0; i < this.getChildren().size(); i++) {
                DataComponent thisChild = this.getChildren().get(i);
                DataComponent otherChild = other.getChildren().get(i);
                if (! thisChild.equals(otherChild)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ColumnTree" + Arrays.toString(children.toArray());
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
