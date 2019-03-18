package org.cirdles.topsoil.app.data.column;

import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.cirdles.topsoil.app.data.composite.DataLeaf;
import org.cirdles.topsoil.app.util.NumberColumnStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a column of data, and acts as a leaf in a {@link ColumnRoot}.
 *
 * @param <T>   the type of data for this DataColumn
 *
 * @author marottajb
 *
 * @see org.cirdles.topsoil.app.data.composite.DataComponent
 */
public class DataColumn<T> extends DataLeaf {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    protected Class<T> type;
    private StringConverter<T> converter;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataColumn(String label, Class<T> type, StringConverter<T> converter) {
        super(label);
        this.type = type;
        this.converter = converter;
    }

    public static DataColumn<String> stringColumn(String label) {
        return new DataColumn<>(label, String.class, new DefaultStringConverter());
    }

    public static DataColumn<Number> numberColumn(String label) {
        return new DataColumn<>(label, Number.class, new NumberColumnStringConverter());
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Class<T> getType() {
        return type;
    }

    public StringConverter<T> getStringConverter() {
        return converter;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataColumn) {
            DataColumn<?> other = (DataColumn<?>) object;
            if (! other.getLabel().equals(this.getLabel())) {
                return false;
            }
            if (other.isSelected() != this.isSelected()) {
                return false;
            }
            if (! other.getType().equals(this.getType())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        List<Object> objects = new ArrayList<>();
        objects.add(getLabel());
        objects.add(isSelected());
        objects.add(getType());
        return Objects.hash(objects.toArray());
    }

}
