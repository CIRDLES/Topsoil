package org.cirdles.topsoil.app.data.row;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.cirdles.topsoil.app.data.column.DataColumn.*;

public class DataRowTest {

    @Test
    public void equals_test() {

        DataColumn<Number> col1 = numberColumn("firstCol");
        DataColumn<String> col2 = stringColumn("secondCol");

        DoubleProperty row1prop1 = new SimpleDoubleProperty(1.0);
        StringProperty row1prop2 = new SimpleStringProperty("2.0");
        DoubleProperty row2prop1 = new SimpleDoubleProperty(1.0);
        StringProperty row2prop2 = new SimpleStringProperty("2.0");

        DataRow row1 = new DataRow("row");
        row1.setPropertyForColumn(col1, row1prop1);
        row1.setPropertyForColumn(col2, row1prop2);

        DataRow row2 = new DataRow("row");
        row2.setPropertyForColumn(col1, row2prop1);
        row2.setPropertyForColumn(col2, row2prop2);

        assertEquals(row1, row2);
    }

}
