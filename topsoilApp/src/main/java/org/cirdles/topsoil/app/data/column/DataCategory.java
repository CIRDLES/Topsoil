package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.data.composite.DataComposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return getLabel();
    }

}
