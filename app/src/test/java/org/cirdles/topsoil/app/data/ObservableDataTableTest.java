package org.cirdles.topsoil.app.data;

import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.junit.*;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class ObservableDataTableTest {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String TEST_DATA_STRING = ExampleDataTable.UPB_DATA;
    private static final IsotopeSystem ISOTOPE_SYSTEM = IsotopeSystem.UPB;
    private static final UncertaintyFormat UNCT_FORMAT = UncertaintyFormat.TWO_SIGMA_PERCENT;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static ObservableDataTable data;

    //**********************************************//
    //                     SETUP                    //
    //**********************************************//

    @BeforeClass
    public static void setUp() {
        try {
            String delim = FileParser.getDelimiter(TEST_DATA_STRING);
            String[] headers = FileParser.parseHeaders(TEST_DATA_STRING, delim);
            Double[][] data = FileParser.parseData(TEST_DATA_STRING, delim);

            ObservableDataTableTest.data = new ObservableDataTable(data, true, headers, ISOTOPE_SYSTEM, UNCT_FORMAT);
        } catch (IOException e) {
            fail("Unable to parse test data.");
        }
    }

    @After
    public void afterEachTest() {
        data.clearVariableAssignments();
    }

    //**********************************************//
    //                     TESTS                    //
    //**********************************************//

    @Test
    public void test_setVariableForColumn() {
        Variable<Number> variable = Variables.X;
        int colIndex = 2;
        ObservableDataColumn column = data.getColumns().get(colIndex);

        data.setVariableForColumn(colIndex, variable);

        // The column's hasVariable property has been updated
        assertTrue(column.hasVariable());

        // The column's variable property has been updated
        assertSame(column.getVariable(), Variables.X);

        // The data table's varMap should have only one entry
        assertEquals(1, data.getVarMap().size());

        // The single entry in the varMap is correct
        assertSame(data.getVarMap().get(variable), column);
    }

    @Test
    public void test_clearVariableAssignments() {
        data.setVariableForColumn(0, Variables.X);
        data.setVariableForColumn(1, Variables.SIGMA_X);
        data.setVariableForColumn(2, Variables.Y);
        data.setVariableForColumn(3, Variables.SIGMA_Y);
        data.setVariableForColumn(4, Variables.RHO);

        data.clearVariableAssignments();

        // The data table's varMap should not have any entries
        assertEquals(0, data.getVarMap().size());

        for (ObservableDataColumn column : data.getColumns()) {
            // The column's hasVariable property has been updated
            assertFalse(column.hasVariable());

            // The column's variable property has been setValue to null
            assertNull(column.getVariable());
        }
    }

}
