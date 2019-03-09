package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.composite.DataComposite;

import static java.util.Arrays.asList;

/**
 * Represents a sub-dataset within a {@link DataTable}. All rows in each {@code DataSegment} in a table map to the same
 * {@code ColumnRoot}.
 *
 * @author marottajb
 *
 * @see DataTable
 * @see ColumnRoot
 */
public class DataSegment extends DataComposite<DataRow> {

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

    @Override
    public String toString() {
        return getLabel();
    }

}
