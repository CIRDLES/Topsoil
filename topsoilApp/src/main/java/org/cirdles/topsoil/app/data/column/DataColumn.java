package org.cirdles.topsoil.app.data.column;

import org.cirdles.topsoil.app.data.composite.DataLeaf;

import java.io.Serializable;

/**
 * Represents a column of data, and acts as a leaf in a {@link ColumnTree}.
 *
 * @param <T>   the type of data for this DataColumn
 *
 * @author marottajb
 *
 * @see org.cirdles.topsoil.app.data.composite.DataComponent
 */
public class DataColumn<T extends Serializable> extends DataLeaf {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 455246801295932422L;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Class<T> type;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataColumn(String label, Class<T> type) {
        super(label);
        this.type = type;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DataColumn) {
            DataColumn<?> other = (DataColumn<?>) object;
            if (! super.equals(other)) {
                return false;
            }
            if (! type.equals(other.getType())) {
                return false;
            }
            return true;
        }
        return false;
    }
}
