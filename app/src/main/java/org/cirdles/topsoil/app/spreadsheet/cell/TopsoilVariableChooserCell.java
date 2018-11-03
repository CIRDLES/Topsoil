package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.binding.Binding;
import javafx.beans.property.Property;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marottajb
 */
public class TopsoilVariableChooserCell extends TopsoilSpreadsheetCellBase<String> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String VAR_CHOOSER_CELL = "var-chooser-cell";
    private static final String WITH_VAR = "var-chooser-with-var";
    private static final String NO_VAR = "var-chooser-no-var";

    private static final String BLANK_ABBR = "[variable]";
    private static List<String> VARIABLE_ABBREVIATIONS = new ArrayList<>();
    static {
        VARIABLE_ABBREVIATIONS.add(BLANK_ABBR);
        Variables.VARIABLE_LIST.forEach(variable -> VARIABLE_ABBREVIATIONS.add(variable.getAbbreviation()));
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public TopsoilVariableChooserCell(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                                      Binding<String> binding) {
        super(spreadsheet, row, col, SpreadsheetCellType.LIST(VARIABLE_ABBREVIATIONS), binding);

        getStyleClass().add(VAR_CHOOSER_CELL);
        getStyleClass().add(NO_VAR);

        itemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue instanceof String) {
                if (newValue.equals(BLANK_ABBR)) {
                    if (! getStyleClass().contains(NO_VAR)) {
                        getStyleClass().add(NO_VAR);
                    }
                    getStyleClass().remove(WITH_VAR);
                } else {
                    if (! getStyleClass().contains(WITH_VAR)) {
                        getStyleClass().add(WITH_VAR);
                    }
                    getStyleClass().remove(NO_VAR);
                }
            }
        }));
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc} */
    void onItemUpdated(Object oldValue, Object newValue) {
        String newVal = String.valueOf(newValue);
        if (BLANK_ABBR.equals(newVal)) {
            spreadsheet.getData().setVariableForColumn(getColumn(), null);
        } else {
            Variable<Number> variable = Variables.fromAbbreviation(newVal);
            spreadsheet.getData().setVariableForColumn(getColumn(), variable);
        }
    }

    /** {@inheritDoc} */
    void onSourceUpdated(Object oldValue, Object newValue) {
        if (newValue == null || newValue.equals("")) {
            spreadsheet.getGrid().setCellValue(getRow(), getColumn(), BLANK_ABBR);
        } else {
            spreadsheet.getGrid().setCellValue(getRow(), getColumn(), String.valueOf(newValue));
        }
    }

}
