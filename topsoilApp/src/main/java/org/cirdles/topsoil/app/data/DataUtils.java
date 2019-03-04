package org.cirdles.topsoil.app.data;

import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.util.NumberColumnStringConverter;

public class DataUtils {

    public static StringConverter<?> stringConverterForDataColumn(DataTable table, DataColumn<?> dataColumn) {
        StringConverter<?> converter;
        if (dataColumn.getType() == Number.class) {
            converter = new NumberColumnStringConverter();
            int maxFractionDigits = 0;
            for (Number n : table.getValuesForColumn((DataColumn<Number>) dataColumn)) {
                maxFractionDigits = Math.max(maxFractionDigits, NumberColumnStringConverter.countFractionDigits(n));
            }
            ((NumberColumnStringConverter) converter).setNumFractionDigits(maxFractionDigits);
        } else {
            converter = new DefaultStringConverter();
        }
        return converter;
    }

}
