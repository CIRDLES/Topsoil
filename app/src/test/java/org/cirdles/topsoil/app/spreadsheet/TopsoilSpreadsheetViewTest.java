package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.property.Property;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilSpreadsheetCell;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.file.ExampleDataTable;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.testfx.assertions.api.Assertions.assertThat;

public class TopsoilSpreadsheetViewTest extends ApplicationTest {

    static final String DATA_DELIM = ",";

    ObservableTableData data;
    TopsoilSpreadsheetView spreadsheet;

    @Override
    public void start(Stage stage) throws IOException {

        // 1. Setup data model object
        String content = new ExampleDataTable().getUPbSampleData();

        Double[][] rows = FileParser.parseData(content, DATA_DELIM);
        boolean rowFormat = true;
        String[] headers = FileParser.parseHeaders(content, DATA_DELIM);
        IsotopeSystem isoSys = IsotopeSystem.UPB;
        UncertaintyFormat unctFormat = UncertaintyFormat.TWO_SIGMA_PERCENT;

        data = new ObservableTableData(
                rows,
                rowFormat,
                headers,
                isoSys,
                unctFormat
        );

        // 2. Setup spreadsheet
        spreadsheet = new TopsoilSpreadsheetView(data);

        // 3. Setup stage
        Scene scene = new Scene(spreadsheet, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    //**********************************************//
    //                     TESTS                    //
    //**********************************************//

    @Test
    public void test_addRowToData() {
        List<Double> row = new ArrayList<>();
        for (double d = 0.0; d < data.colCount(); d++) {
            row.add(d);
        }
        data.addRow(row);

        checkDataCellSourceProperties();
    }

    @Test
    public void test_removeRowFromData() {
        data.removeRow(0);

        checkDataCellSourceProperties();
    }

    @Test
    public void test_addColumnToData() {
        List<Double> column = new ArrayList<>();
        for (double d = 0.0; d < data.colCount(); d++) {
            column.add(d);
        }
        data.addColumn(column);

        checkDataCellSourceProperties();
    }

    @Test
    public void test_removeColumnFromData() {
        data.removeColumn(0);

        checkDataCellSourceProperties();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Tests that each TopsoilSpreadsheetCell containing a data value has the correct data source property.
     */
    private void checkDataCellSourceProperties() {
        Grid grid = spreadsheet.getGrid();
        TopsoilSpreadsheetCell<Number> topsoilCell;
        SpreadsheetCell gridCell;
        Property<Number> expectedSource;
        Property<Number> actualSource;
        for (int rowIndex = 1; rowIndex < grid.getRowCount(); rowIndex++) {
            for (int colIndex = 0; colIndex < grid.getColumnCount(); colIndex++) {
                gridCell = grid.getRows().get(rowIndex).get(colIndex);

                // Cell's parameterized type should be Number
                assertTrue("Cell does not contain item of type Number.", gridCell.getItem() instanceof Number);

                topsoilCell = (TopsoilSpreadsheetCell<Number>) gridCell;
                expectedSource = data.getObservableRows().get(rowIndex).get(colIndex);
                actualSource = topsoilCell.getSource();

                // Cell's source should be the correct data property
                assertSame("Listening to wrong source property." +
                           "\nExpected: " + expectedSource +
                           "\n\tvalue: " + expectedSource.getValue() +
                           "\nActual: " + actualSource +
                           "\n\tvalue: " + actualSource.getValue(),

                           topsoilCell.getSource(),

                           data.getObservableRows().get(rowIndex).get(colIndex)
                );
            }
        }
    }

}
