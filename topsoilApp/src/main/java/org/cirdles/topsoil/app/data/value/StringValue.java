package org.cirdles.topsoil.app.data.value;

import javafx.util.converter.DefaultStringConverter;
import org.cirdles.topsoil.app.data.column.DataColumn;

/**
 * A {@code DataValue} for a {@code String} object.
 *
 * @author marottajb
 */
public class StringValue extends DataValue<String> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 1416736277609076650L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public StringValue(DataColumn<String> column, String value) {
        super(column, value, new DefaultStringConverter());
    }

}
