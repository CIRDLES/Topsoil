package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.composite.DataComposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DataRoot extends DataComposite<DataSegment> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRoot(DataSegment... segments) {
        super("root", segments);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public List<DataRow> getLeafNodes() {
        return leavesAsDataRows(super.getLeafNodes());
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
    private <T> List<DataRow> leavesAsDataRows(List<T> leaves) {
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
