package org.cirdles.topsoil.app.model;

import org.cirdles.topsoil.app.util.DecimalStringConverter;

/**
 * A {@code DataValue} for a {@code Double} object.
 *
 * @author marottajb
 */
public class DoubleValue extends DataValue<Double> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final long serialVersionUID = 5968808945671799686L;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DoubleValue(DataColumn<Double> column, Double value) {
        super(column, value, new DecimalStringConverter());
    }

    public DoubleValue(DataColumn<Double> column, Double value, String pattern) {
        super(column, value, new DecimalStringConverter(pattern));
    }

}
