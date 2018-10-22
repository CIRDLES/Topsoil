package org.cirdles.topsoil.app.spreadsheet.picker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.controlsfx.control.spreadsheet.Picker;

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
        spreadsheet.getData().getRow(rowIndex).setSelected(b);
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
        this.spreadsheet = spreadsheet;
        this.rowIndex = rowIndex;
        getStyleClass().addAll(PICKER_ACTIVATED);
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
