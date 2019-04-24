package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.composite.DataComposite;

import java.util.ArrayList;
import java.util.List;

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
        try {
            return leavesAsDataRows(super.getLeafNodes());
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
     * @return          List of DataRows
     *
     * @throws          Exception if a leaf is not an instance of DataRow
     */
    private <T> List<DataRow> leavesAsDataRows(List<T> leaves) throws Exception {
        List<DataRow> rows = new ArrayList<>();
        for (T leaf : leaves) {
            if (leaf instanceof DataRow) {
                rows.add((DataRow) leaf);
            } else {
                // this should not happen
                throw new Exception("Leaf is not an instance of DataRow.");
            }
        }
        return rows;
    }

}
