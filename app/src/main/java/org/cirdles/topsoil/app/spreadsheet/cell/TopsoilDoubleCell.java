package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

/**
 * @author marottajb
 */
public class TopsoilDoubleCell extends SpreadsheetCellBase {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String DATA_CELL = "data-cell";
    private static final String DATA_CELL_DEACTIVATED = "data-cell-deactivated";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    // @TODO Generalize Topsoil's custom cells with an abstract class
    private final TopsoilSpreadsheetView spreadsheet;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private BooleanProperty selected = new SimpleBooleanProperty();
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
            if (! getStyleClass().contains(DATA_CELL_DEACTIVATED)) {
                getStyleClass().add(DATA_CELL_DEACTIVATED);
            }
        }
        selectedProperty().set(selected);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilDoubleCell(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                             final int rowSpan, final int colSpan, final Double item) {
        super(row, col, rowSpan, colSpan, SpreadsheetCellType.DOUBLE);
        this.setItem(item);
        this.spreadsheet = spreadsheet;
        this.getStyleClass().add(DATA_CELL);

        this.itemProperty().addListener(
                (observable, oldValue, newValue) -> spreadsheet.updateDataValue(this, (Double) newValue)
        );
    }
}
