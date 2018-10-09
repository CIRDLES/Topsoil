package org.cirdles.topsoil.app.spreadsheet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private int row;

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
        selectedProperty().set(selected);
        spreadsheet.getData().setRowSelected(row, isSelected());
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataRowPicker(TopsoilSpreadsheetView spreadsheet, int row) {
        this.spreadsheet = spreadsheet;
        this.row = row;
        getStyleClass().addAll(PICKER_ACTIVATED);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void onClick() {
        this.setSelected( ! this.isSelected() );
    }

    public int getRow() {
        return row;
    }

    public void setRow(int index) {
        row = index;
    }

}
