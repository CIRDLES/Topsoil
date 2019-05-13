package org.cirdles.topsoil.app.data.row;

import org.cirdles.topsoil.app.data.composite.DataComponentComparator;
import org.cirdles.topsoil.app.data.composite.DataComposite;

import static java.util.Arrays.asList;

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
        children.sort(new DataComponentComparator());
    }

}
