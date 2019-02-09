package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.generic.BranchNode;

import static java.util.Arrays.asList;

/**
 * @author marottajb
 */
public class DataSegment extends BranchNode<DataRow> {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    private static final long serialVersionUID = 5831958152495283128L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataSegment(String label) {
        super(label);
    }

    public DataSegment(String label, DataRow... rows) {
        this(label);
        children.addAll(asList(rows));
    }

}
