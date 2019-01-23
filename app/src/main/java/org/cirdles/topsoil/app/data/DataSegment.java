package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.node.BranchNode;

/**
 * @author marottajb
 */
public class DataSegment extends BranchNode<DataRow> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataSegment(String label) {
        super(label);
    }

    public DataSegment(String label, DataRow... rows) {
        this(label);
        children.addAll(rows);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public DataRow getRow(int index) {
        return children.get(index);
    }

    public DataRow getRow(String label) {
        DataRow rtnval = null;
        for (DataRow row : children) {
            if (label.equals(row.getLabel())) {
                rtnval = row;
            }
        }
        return rtnval;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

}
