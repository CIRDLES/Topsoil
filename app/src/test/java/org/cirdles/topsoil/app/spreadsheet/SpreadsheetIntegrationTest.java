package org.cirdles.topsoil.app.spreadsheet;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.data.ExampleDataTable;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.data.ObservableDataRow;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilDoubleCell;
import org.cirdles.topsoil.app.spreadsheet.picker.ColumnVariablePicker;
import org.cirdles.topsoil.app.spreadsheet.picker.DataRowPicker;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.controlsfx.control.spreadsheet.Grid;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author marottajb
 */
public class SpreadsheetIntegrationTest extends ApplicationTest {

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
    public void test_dataOperationInsertRow() {
        // 1. Insert row at beginning of data
        ObservableDataRow rowAtBeginning = new ObservableDataRow(zeroRow());
        Platform.runLater(() -> {
            data.addRow(0, rowAtBeginning);
            assertSame(rowAtBeginning, data.getRow(0));
            assertEquals(0, data.getRows().indexOf(rowAtBeginning));
            assertDataCellsMatchModel();
        });

        // 2. Insert row at end of data
        ObservableDataRow rowAtEnd = new ObservableDataRow(zeroRow());
        Platform.runLater(() -> {
            data.addRow(rowAtEnd);
            assertSame(rowAtEnd, data.getRow(data.rowCount() - 1));
            assertEquals(data.rowCount() - 1, data.getRows().indexOf(rowAtEnd));
            assertDataCellsMatchModel();
        });

        // 3. Insert row in middle of data
        ObservableDataRow rowInMiddle = new ObservableDataRow(zeroRow());
        int index = data.rowCount() / 2;
        Platform.runLater(() -> {
            data.addRow(index, rowInMiddle);
            assertSame(rowInMiddle, data.getRow(index));
            assertEquals(index, data.getRows().indexOf(rowInMiddle));
            assertDataCellsMatchModel();
        });
    }

    @Test
    public void test_dataOperationDeleteRow() {
        // 1. Delete row at beginning of data
        ObservableDataRow rowAtBeginning = new ObservableDataRow(zeroRow());
        Platform.runLater(() -> {
            data.addRow(0, rowAtBeginning);      // Add dummy row at beginning
            assertDataCellsMatchModel();

            data.removeRow(0);                   // Remove the row at the beginning of the data
            assertFalse(data.getRows().contains(rowAtBeginning));
            assertDataCellsMatchModel();
        });

        // 2. Delete row at end of data
        ObservableDataRow rowAtEnd = new ObservableDataRow(zeroRow());
        Platform.runLater(() -> {
            data.addRow(data.rowCount(), rowAtEnd);         // Add dummy row to end
            assertDataCellsMatchModel();

            data.removeRow(data.rowCount() - 1);      // Remove the row at the beginning of the data
            assertFalse(data.getRows().contains(rowAtEnd));
            assertDataCellsMatchModel();
        });

        // 3. Delete row in middle of data
        ObservableDataRow rowInMiddle = new ObservableDataRow(zeroRow());
        int index = data.rowCount() / 2;
        Platform.runLater(() -> {
            data.addRow(index, rowInMiddle);    // Add dummy row at middle index
            assertDataCellsMatchModel();

            data.removeRow(index);              // Remove the row at the middle index
            assertFalse(data.getRows().contains(rowInMiddle));
            assertDataCellsMatchModel();
        });
    }

    @Test
    public void test_dataOperationInsertColumn() {
        // 1. Insert column at beginning of data
        ObservableDataColumn columnAtBeginning = new ObservableDataColumn(zeroColumn());
        Platform.runLater(() -> {
            data.addColumn(0, columnAtBeginning);
            assertEquals(columnAtBeginning, data.getColumns().get(0));
            assertEquals(0, data.getColumns().indexOf(columnAtBeginning));
            assertDataCellsMatchModel();
        });

        // 2. Insert column at end of data
        ObservableDataColumn columnAtEnd = new ObservableDataColumn(zeroColumn());
        Platform.runLater(() -> {
            data.addColumn(data.colCount(), columnAtEnd);
            assertEquals(columnAtEnd, data.getColumns().get(data.colCount() - 1));
            assertEquals(data.colCount() - 1, data.getColumns().indexOf(columnAtEnd));
            assertDataCellsMatchModel();
        });

        // 3. Insert column in middle of data
        ObservableDataColumn columnInMiddle = new ObservableDataColumn(zeroColumn());
        int index = data.colCount() / 2;
        Platform.runLater(() -> {
            data.addColumn(index, columnInMiddle);
            assertEquals(columnInMiddle, data.getColumns().get(index));
            assertEquals(index, data.getColumns().indexOf(columnInMiddle));
            assertDataCellsMatchModel();
        });
    }

    @Test
    public void test_dataOperationDeleteColumn() {
        // 1. Delete column at beginning of data
        ObservableDataColumn columnAtBeginning = new ObservableDataColumn(zeroColumn());
        Platform.runLater(() -> {
            data.addColumn(0, columnAtBeginning);   // Add dummy column to beginning
            assertDataCellsMatchModel();

            data.removeColumn(0);                   // Remove the column at the beginning of the data
            assertFalse(data.getColumns().contains(columnAtBeginning));
            assertDataCellsMatchModel();
        });

        // 2. Delete column at end of data
        ObservableDataColumn columnAtEnd = new ObservableDataColumn(zeroColumn());
        Platform.runLater(() -> {
            data.addColumn(data.colCount(), columnAtEnd);      // Add dummy column to end
            assertDataCellsMatchModel();

            data.removeColumn(data.colCount() - 1);     // Remove the column at the end of the data
            assertFalse(data.getColumns().contains(columnAtEnd));
            assertDataCellsMatchModel();
        });

        // 3. Delete column in middle of data
        ObservableDataColumn columnInMiddle = new ObservableDataColumn(zeroColumn());
        int index = data.colCount() / 2;
        Platform.runLater(() -> {
            data.addColumn(index, columnInMiddle);      // Add dummy column at middle index
            assertDataCellsMatchModel();

            data.removeColumn(index);                   // Remove the column at the middle index
            assertFalse(data.getColumns().contains(columnInMiddle));
            assertDataCellsMatchModel();
        });
    }

    @Test
    public void test_dataOperationRowSelected() {
        int index = 0;
        ObservableDataRow row = data.getRow(index);
        Platform.runLater(() -> {
            row.setSelected(false);     // Ensure row is deselected
            row.setSelected(true);
            assertTrue("Corresponding DataRowPicker was not selected.",
                       ((DataRowPicker) spreadsheet.getRowPickers().get(index + 1)).isSelected());
        });
    }

    @Test
    public void test_dataOperationRowDeselected() {
        int index = 0;
        ObservableDataRow row = data.getRow(index);
        Platform.runLater(() -> {
            row.setSelected(true);      // Ensure row is selected
            row.setSelected(false);
            assertFalse("Corresponding DataRowPicker was not deselected.",
                        ((DataRowPicker) spreadsheet.getRowPickers().get(index + 1)).isSelected());
        });
    }

    @Test
    public void test_dataOperationUpdateVariables() {
        Map<Integer, Variable<Number>> assignments;
        ObservableList<ObservableDataColumn> columns = data.getColumns();
        final int xIndex = 0;
        final int sigmaXIndex = 1;
        final int yIndex = 2;
        final int sigmaYIndex = 3;
        final int rhoIndex = 4;

        Platform.runLater(() -> data.clearVariableAssignments());

        // 1. Add single variable assignment
        Platform.runLater(() -> {
            data.setVariableForColumn(xIndex, Variables.X);
            Variable pickerVar = ((ColumnVariablePicker) spreadsheet.getColumnPickers().get(xIndex)).getVariable();
            Variable columnVar = columns.get(xIndex).getVariable();
            assertEquals("Incorrect variable for ColumnVariablePicker at index " + xIndex + ". " +
                         "Expected: " + Variables.X.getAbbreviation() +
                         ", Actual: " + pickerVar.getAbbreviation(), Variables.X, pickerVar);
            assertEquals("Incorrect number of variable assignments. " +
                         "Expected: " + 1 + ", Actual: " + data.getVarMap().size(),
                         1, data.getVarMap().size());
            assertEquals("Incorrect variable for ObservableDataColumn at index " + xIndex + ". " +
                         "Expected: " + Variables.X.getAbbreviation() +
                         ", Actual: " + columnVar.getAbbreviation(), Variables.X, columns.get(xIndex).getVariable());
        });

        // 2. Add multiple variable assignments
        assignments = new HashMap<>();
        assignments.put(sigmaXIndex, Variables.SIGMA_X);
        assignments.put(yIndex, Variables.Y);
        assignments.put(sigmaYIndex, Variables.SIGMA_Y);
        assignments.put(rhoIndex, Variables.RHO);
        Platform.runLater(() -> {
            data.setVariablesForColumns(assignments);

            checkColumnVariables(sigmaXIndex, Variables.SIGMA_X);
            checkColumnVariables(yIndex, Variables.Y);
            checkColumnVariables(sigmaYIndex, Variables.SIGMA_Y);
            checkColumnVariables(rhoIndex, Variables.RHO);
        });

        // 3. Update existing variable assignment
        Platform.runLater(() -> {
            data.setVariableForColumn(xIndex, Variables.Y);
            checkColumnVariables(xIndex, Variables.Y);
            assertFalse("Unexpected key in data.varMap: " + Variables.X.getAbbreviation(),
                        data.getVarMap().containsKey(Variables.X));
            checkColumnVariables(yIndex, null);
        });

        // 4. Remove variable assignment
        Platform.runLater(() -> {
            data.setVariableForColumn(xIndex, null);
            checkColumnVariables(xIndex, null);
            assertFalse("Unexpected value in data.varMap: " + columns.get(xIndex),
                        data.getVarMap().containsValue(columns.get(xIndex)));
        });
    }

    @Test
    public void test_dataOperationUpdateProperty() {
        DoubleProperty newProp = new SimpleDoubleProperty(-1.0);
        int row = 0;
        int col = 0;
        TopsoilDoubleCell cell = ((TopsoilDoubleCell) spreadsheet.getGrid().getRows().get(row + 1).get(col));

        Platform.runLater(() -> {
            data.set(row, col, newProp);
            assertSame("Source property was not successfully updated. " +
                       "Expected: " + newProp + ", Actual: " + cell.getSource(),
                       newProp, cell.getSource());
        });
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private Double[] zeroRow() {
        return zeroes(data.colCount());
    }

    private Double[] zeroColumn() {
        return zeroes(data.rowCount());
    }

    private static Double[] zeroes(int n) {
        Double[] zeroes = new Double[n];
        Arrays.fill(zeroes, 0, n, 0.0);
        return zeroes;
    }

    private static void checkColumnVariables(int index, Variable variable) {
        ObservableList<ObservableDataColumn> columns = data.getColumns();
        Variable pickerVar = ((ColumnVariablePicker) spreadsheet.getColumnPickers().get(index)).getVariable();
        Variable columnVar = columns.get(index).getVariable();
        String varText = (variable != null ? variable.getAbbreviation() : "null");
        String pickerVarText = (pickerVar != null ? pickerVar.getAbbreviation() : "null");
        String columnVarText = (columnVar != null ? columnVar.getAbbreviation() : "null");
        assertSame("Incorrect variable for ColumnVariablePicker at index " + index + ". " +
                   "Expected: " + varText +
                   ", Actual: " + pickerVarText, variable, pickerVar);
        assertSame("Incorrect variable for ObservableDataColumn at index " + index + ". " +
                   "Expected: " + varText +
                   ", Actual: " + columnVarText, variable, columns.get(index).getVariable());
    }

    private static void assertDataCellsMatchModel() {
        Grid grid = spreadsheet.getGrid();

        TopsoilDoubleCell cell;
        Property<Number> expectedSource;
        Property<Number> actualSource;
        for (int rowIndex = 1; rowIndex < grid.getRowCount(); rowIndex++) {
            for (int colIndex = 0; colIndex < grid.getColumnCount(); colIndex++) {
                cell = (TopsoilDoubleCell) grid.getRows().get(rowIndex).get(colIndex);
                expectedSource = data.get(rowIndex - 1, colIndex);
                actualSource = cell.getSource();

                // Cell's source should be the correct data property
                assertSame("Listening to wrong source property." +
                           "\nExpected: " + expectedSource +
                           "\nActual: " + actualSource,
                           actualSource,
                           expectedSource
                );
            }
        }
    }
}
