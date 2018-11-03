package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.property.StringProperty;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

/**
 * @author marottajb
 */
public class TopsoilHeaderCell extends TopsoilSpreadsheetCellBase<String> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String HEADER_CELL = "header-cell";

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilHeaderCell(TopsoilSpreadsheetView spreadsheet, final int row, final int col, StringProperty property) {
        super(spreadsheet, row, col, SpreadsheetCellType.STRING, property);
        this.getStyleClass().add(HEADER_CELL);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc} */
    void onItemUpdated(Object oldValue, Object newValue) {
        if (! newValue.equals(getSource().getValue())) {
            getSource().setValue(String.valueOf(newValue));
        }
    }

    /** {@inheritDoc} */
    void onSourceUpdated(Object oldValue, Object newValue) {
        if (! newValue.equals(getItem())) {
            this.spreadsheet.getGrid().setCellValue(getRow(), getColumn(), String.valueOf(newValue));
        }
    }
}
