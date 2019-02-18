package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComposite;
import org.cirdles.topsoil.app.data.composite.DataComponent;

/**
 * Represents a category of data columns. Can be composed of {@link DataColumn}s as well as other {@code DataCategory}s.
 *
 * @author marottajb
 */
public class DataCategory extends DataComposite<DataComponent> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -2907499123449897179L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataCategory(String label, DataComponent... children) {
        super(label, children);
    }

}