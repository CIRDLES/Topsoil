package org.cirdles.topsoil.app.data;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 * A common superclass for Topsoil's data lists that need to be observable.
 *
 * @author marottajb
 *
 * @see ObservableDataColumn
 * @see ObservableDataRow
 */
public abstract class TopsoilDataList extends SimpleListProperty<DoubleProperty> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    TopsoilDataList(Double... values) {
        super(FXCollections.observableArrayList());
        for (Double val : values) {
            this.add(new SimpleDoubleProperty(val));
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the data values as an array of {@code Double}s instead of the list's actual properties.
     *
     * @return  Double[] of list values
     */
    public Double[] getData() {
        Double[] data = new Double[size()];
        for (int i = 0; i < size(); i++) {
            data[i] = this.get(i).get();
        }
        return data;
    }

}
