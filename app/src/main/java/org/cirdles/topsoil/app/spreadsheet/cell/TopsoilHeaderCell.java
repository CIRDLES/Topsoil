package org.cirdles.topsoil.app.spreadsheet.cell;

import org.cirdles.topsoil.app.spreadsheet.TopsoilDataColumn;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.StringConverterWithFormat;

/**
 * @author marottajb
 */
public class TopsoilHeaderCell extends SpreadsheetCellBase {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String HEADER_CELL = "header-cell";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    // @TODO Generalize Topsoil's custom cells with an abstract class
    private final TopsoilSpreadsheetView spreadsheet;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilHeaderCell(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                             final int rowSpan, final int colSpan, TopsoilDataColumn column) {
        super(row, col, rowSpan, colSpan, SpreadsheetCellType.STRING);
        this.spreadsheet = spreadsheet;
        this.getStyleClass().add(HEADER_CELL);

        column.nameProperty().bindBidirectional(this.itemProperty(), new StringConverterWithFormat<Object>() {
            @Override
            public Object fromString(String arg0) {
                return arg0;
            }
            @Override
            public String toString(Object arg0) {
                return arg0 == null ? "" : arg0.toString();
            }
        });
    }
}
