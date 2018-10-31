package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.cirdles.topsoil.variable.Variables;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

/**
 * @author marottajb
 */
public class TopsoilDoubleCell extends TopsoilSpreadsheetCellBase<Number> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String DATA_CELL = "data-cell";
    private static final String DATA_CELL_DEACTIVATED = "data-cell-deactivated";

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private BooleanProperty selected = new SimpleBooleanProperty(true);
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public boolean isSelected() {
        return selectedProperty().get();
    }
    public void setSelected(boolean selected) {
        if (selected) {
            getStyleClass().remove(DATA_CELL_DEACTIVATED);
        } else {
            getStyleClass().add(DATA_CELL_DEACTIVATED);
        }
        selectedProperty().set(selected);
    }

    private BooleanProperty uncertain = new SimpleBooleanProperty(false);
    public BooleanProperty uncertainProperty() {
        return uncertain;
    }
    public boolean isUncertain() {
        return uncertain.get();
    }
    public void setUncertain(boolean val) {
        uncertain.set(val);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilDoubleCell(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                             final int rowSpan, final int colSpan, final DoubleProperty source) {
        super(spreadsheet, row, col, rowSpan, colSpan, SpreadsheetCellType.DOUBLE, source);
        this.getStyleClass().add(DATA_CELL);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc} */
    void onItemUpdated(Number oldValue, Number newValue) {
        ObservableDataColumn column = spreadsheet.getData().getColumns().get(getColumn());
        double dataValue = (double) newValue;
        if (Variables.UNCERTAINTY_VARIABLES.contains(column.getVariable())) {
            dataValue /= spreadsheet.getData().getUnctFormat().getValue();
        }
        if (Double.compare(dataValue, (Double) getSource().getValue()) != 0) {
            spreadsheet.updateDataValue(getRow(), getColumn(), dataValue);
        }
    }

    /** {@inheritDoc} */
    void onSourceUpdated(Number oldValue, Number newValue) {
        ObservableDataColumn column = spreadsheet.getData().getColumns().get(getColumn());
        double cellValue = (double) newValue;
        if (Variables.UNCERTAINTY_VARIABLES.contains(column.getVariable())) {
            cellValue *= spreadsheet.getData().getUnctFormat().getValue();
        }
        if (Double.compare(cellValue, (Double) getItem()) != 0) {
            this.spreadsheet.getGrid().setCellValue(getRow(), getColumn(), cellValue);
        }
    }
}
