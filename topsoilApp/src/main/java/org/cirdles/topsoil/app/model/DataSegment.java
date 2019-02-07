package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.node.BranchNode;

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

}
