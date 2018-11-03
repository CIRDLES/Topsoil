package org.cirdles.topsoil.app.spreadsheet;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.data.ExampleDataTable;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.controlsfx.control.spreadsheet.Grid;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView.DATA_ROW_OFFSET;
import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class DataCellFormatterTest extends ApplicationTest {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static ObservableDataTable data = ExampleDataTable.getUPb();
    private static TopsoilSpreadsheetView spreadsheet = null;
    private static Scene scene = null;

    //**********************************************//
    //                     SETUP                    //
    //**********************************************//

    @Override
    public void start(Stage stage) {
        if (spreadsheet == null) {
            spreadsheet = new TopsoilSpreadsheetView(data);
        }
        if (scene == null) {
            scene = new Scene(spreadsheet, 600, 400);
        }
        stage.setScene(scene);
        stage.show();
    }

    //**********************************************//
    //                     TESTS                    //
    //**********************************************//

    @Test
    public void test_formatColumn() {
        int index = 0;
        assertDecimalsAligned(index);
    }
    // @TODO Do more testing

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void assertDecimalsAligned(int col) {
        Grid grid = spreadsheet.getGrid();
        int columnDecimalPlaces = -1;
        int cellDecimalPlaces;
        String text;
        for (int row = DATA_ROW_OFFSET; row < grid.getRowCount(); row++) {
            text = grid.getRows().get(row).get(col).getText();
            cellDecimalPlaces = text.substring(text.lastIndexOf(".") + 1).length();
            if (columnDecimalPlaces < 0) {
                columnDecimalPlaces = cellDecimalPlaces;
            }
            assertEquals("Incorrect number of decimal places in cell text. " +
                         "Expected: " + columnDecimalPlaces + ", " + "Actual: " + cellDecimalPlaces,
                         columnDecimalPlaces,
                         cellDecimalPlaces);
        }
    }

}
