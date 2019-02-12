package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.model.composite.DataLeaf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents a column of data, and acts as a leaf in a {@link ColumnTree}.
 *
 * @param <T>   the type of data for this DataColumn
 *
 * @author marottajb
 *
 * @see org.cirdles.topsoil.app.model.composite.DataComponent
 */
public class DataColumn<T> extends DataLeaf {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = -1370769600447020972L;

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
            if (((DataColumn) object).getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("type", type);
        out.writeFields();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        type = (Class<T>) fields.get("type", null);
    }

}
