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
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataCategory(String label, DataComponent... children) {
        super(label, children);
    }

}
