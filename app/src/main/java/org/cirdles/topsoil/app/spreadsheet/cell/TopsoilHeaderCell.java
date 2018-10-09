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

    public TopsoilHeaderCell(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                             final int rowSpan, final int colSpan, StringProperty source) {
        super(spreadsheet, row, col, rowSpan, colSpan, SpreadsheetCellType.STRING, source);
        this.getStyleClass().add(HEADER_CELL);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc} */
    void onItemUpdated(String oldValue, String newValue) {
        if (! newValue.equals(getSource().getValue())) {
            getSource().setValue(newValue);
        }
    }

    /** {@inheritDoc} */
    void onSourceUpdated(String oldValue, String newValue) {
        if (! newValue.equals(getItem())) {
            this.spreadsheet.getGrid().setCellValue(getRow(), getColumn(), newValue);
        }
    }
}
