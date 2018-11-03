package org.cirdles.topsoil.app.spreadsheet.picker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.cirdles.topsoil.app.spreadsheet.cell.TopsoilNumberCell;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView.DATA_ROW_OFFSET;

/**
 * @author marottajb
 */
public class DataRowPicker extends Picker {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String PICKER_DEACTIVATED = "data-picker-deactivated";
    private static final String PICKER_ACTIVATED = "data-picker-activated";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private TopsoilSpreadsheetView spreadsheet;
    private int rowIndex;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /**
     * Whether or not the picker's state is "activated".
     */
    private final BooleanProperty selected = new SimpleBooleanProperty(true);
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public final boolean isSelected() {
        return selected.get();
    }
    public final void setSelected(boolean b) {
        if (b) {
            if (! getStyleClass().contains(PICKER_ACTIVATED)) {
                getStyleClass().add(PICKER_ACTIVATED);
            }
            getStyleClass().remove(PICKER_DEACTIVATED);
        } else {
            if (! getStyleClass().contains(PICKER_DEACTIVATED)) {
                getStyleClass().add(PICKER_DEACTIVATED);
            }
            getStyleClass().remove(PICKER_ACTIVATED);
        }
        selected.set(b);
        spreadsheet.getData().getRow(rowIndex - DATA_ROW_OFFSET).setSelected(b);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a {@code DataRowPicker} for the provided {@code TopsoilSpreadsheetView} at the specified row index.
     *
     * @param   spreadsheet
     *          TopsoilSpreadsheetView
     * @param   rowIndex
     *          the index of the row that this picker controls
     */
    public DataRowPicker(TopsoilSpreadsheetView spreadsheet, int rowIndex) {
        super(PICKER_ACTIVATED);
        this.spreadsheet = spreadsheet;
        this.rowIndex = rowIndex;

        // Update cells in row when "selected" property is changed
        selectedProperty().addListener(((observable, oldValue, newValue) -> {
            ObservableList<SpreadsheetCell> cellRow = spreadsheet.getGrid().getRows().get(rowIndex);
            for (SpreadsheetCell cell : cellRow) {
                if (cell instanceof TopsoilNumberCell) {
                    ((TopsoilNumberCell) cell).setSelected(newValue);
                }
            }
        }));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void onClick() {
        this.setSelected( ! this.isSelected() );
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int index) {
        rowIndex = index;
    }

}
