package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.composite.DataComposite;
import org.cirdles.topsoil.app.model.composite.DataComponent;

/**
 * Represents a category of data columns. Can be composed of {@link DataColumn}s as well as other {@code DataCategory}s.
 *
 * @author marottajb
 */
public class DataCategory extends DataComposite<DataComponent> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 372814634643053288L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataCategory(String label, DataComponent... children) {
        super(label, children);
    }

}
