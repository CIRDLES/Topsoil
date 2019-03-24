package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.data.column.ColumnRoot;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.row.DataRoot;
import org.cirdles.topsoil.app.data.row.DataRow;
import org.cirdles.topsoil.app.data.row.DataSegment;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.variable.TextVariable;
import org.cirdles.topsoil.variable.Variable;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DataTableTest {

    private static DataColumn<String> columnOne = DataColumn.stringColumn("colOne");
    private static DataColumn<String> columnTwo = DataColumn.stringColumn("colTwo");
    private static DataColumn<String> columnThree = DataColumn.stringColumn("colThree");
    private static DataColumn<String> columnFour = DataColumn.stringColumn("colFour");

    private static DataCategory categoryOne = new DataCategory("catOne", columnOne, columnTwo);
    private static DataCategory categoryTwo = new DataCategory("catTwo", columnTwo, columnThree);

    private static DataRow rowOne = new DataRow("rowOne");
    private static DataRow rowTwo = new DataRow("rowTwo");
    private static DataRow rowThree = new DataRow("rowThree");

    private static DataSegment segmentOne = new DataSegment("segOne", rowOne);
    private static DataSegment segmentTwo = new DataSegment("segTwo", rowTwo);

    private static DataTable table;

    @BeforeClass
    public static void setup() {
        rowOne.setValueForColumn(columnOne, "1,1");
        rowOne.setValueForColumn(columnTwo, "1,2");

        rowTwo.setValueForColumn(columnOne, "2,1");
        rowTwo.setValueForColumn(columnTwo, "2,2");

        ColumnRoot columnRoot = new ColumnRoot(categoryOne, columnThree);
        DataRoot dataRoot = new DataRoot(segmentOne, segmentTwo);
        table = new DataTable(
                DataTemplate.DEFAULT,
                "table",
                columnRoot,
                dataRoot,
                IsotopeSystem.GENERIC,
                Uncertainty.ONE_SIGMA_ABSOLUTE
        );
    }

    @After
    public void clearTable() {
        table.setLabel("table");
        table.setColumnsForAllVariables(null);
        table.setIsotopeSystem(IsotopeSystem.GENERIC);
        table.setUncertainty(Uncertainty.ONE_SIGMA_ABSOLUTE);
    }


    @Test
    public void getValuesForColumn_test() {
        List<String> valuesOne = table.getValuesForColumn(columnOne);
        assertEquals("1,1", valuesOne.get(0));
        assertEquals("2,1", valuesOne.get(1));

        List<String> valuesTwo = table.getValuesForColumn(columnTwo);
        assertEquals("1,2", valuesTwo.get(0));
        assertEquals("2,2", valuesTwo.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getValuesForColumn_nullArgument_test() {
        table.getValuesForColumn(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getValuesForColumn_columnNotInTable_test() {
        table.getValuesForColumn(columnFour);
    }

    @Test
    public void setColumnForVariable_test() {
        table.setColumnForVariable(TextVariable.LABEL, columnOne);
        assertEquals(columnOne, table.getVariableColumnMap().get(TextVariable.LABEL));
    }

    @Test
    public void setColumnsForAllVariables_test() {
        Map<Variable<?>, DataColumn<?>> assignments = new HashMap<>();
        assignments.put(TextVariable.ALIQUOT, columnOne);
        table.setColumnsForAllVariables(assignments);
        assertEquals(1, table.getVariableColumnMap().size());
        assertEquals(columnOne, table.getVariableColumnMap().get(TextVariable.ALIQUOT));
    }

    @Test
    public void equals_test() {
        assertEquals(table, table);
        assertNotEquals(table, "table");

        DataTable tableCopy = new DataTable(
                table.getTemplate(),
                table.getLabel(),
                table.getColumnRoot(),
                table.getDataRoot(),
                table.getIsotopeSystem(),
                table.getUncertainty()
        );
        assertEquals(table, tableCopy);

        DataTable diffTemplate = new DataTable(
                DataTemplate.SQUID_3,
                table.getLabel(),
                table.getColumnRoot(),
                table.getDataRoot(),
                table.getIsotopeSystem(),
                table.getUncertainty()
        );
        assertNotEquals(table, diffTemplate);

        DataTable diffLabel = new DataTable(
                table.getTemplate(),
                "TABLE",
                table.getColumnRoot(),
                table.getDataRoot(),
                table.getIsotopeSystem(),
                table.getUncertainty()
        );
        assertNotEquals(table, diffLabel);

        // @TODO diffColumnRoot

        // @TODO diffDataRoot

        DataTable diffIsoSystem = new DataTable(
                table.getTemplate(),
                table.getLabel(),
                table.getColumnRoot(),
                table.getDataRoot(),
                IsotopeSystem.UPB,
                table.getUncertainty()
        );
        assertNotEquals(table, diffIsoSystem);

        DataTable diffUncertainty = new DataTable(
                table.getTemplate(),
                table.getLabel(),
                table.getColumnRoot(),
                table.getDataRoot(),
                table.getIsotopeSystem(),
                Uncertainty.TWO_SIGMA_ABSOLUTE
        );
        assertNotEquals(table, diffUncertainty);

    }

}
