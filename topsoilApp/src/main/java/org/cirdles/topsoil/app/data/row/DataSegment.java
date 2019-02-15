package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.ColumnTree;
import org.cirdles.topsoil.app.data.composite.DataComposite;

import static java.util.Arrays.asList;

/**
 * Represents a sub-dataset within a {@link DataTable}. All rows in each {@code DataSegment} in a table map to the same
 * {@code ColumnTree}.
 *
 * @author marottajb
 *
 * @see DataTable
 * @see ColumnTree
 */
public class DataSegment extends DataComposite<DataRow> {

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
