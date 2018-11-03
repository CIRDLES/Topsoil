package org.cirdles.topsoil.app.spreadsheet.picker;

import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.controlsfx.control.spreadsheet.Picker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author marottajb
 */
public class ColumnVariablePicker extends Picker {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String COLUMN_PICKER = "column-variable-picker";

    private static final String NO_VAR = "no-var-picker";
    private static final String X = "x-picker";
    private static final String SIGMA_X = "sigma-x-picker";
    private static final String Y = "y-picker";
    private static final String SIGMA_Y = "sigma-y-picker";
    private static final String RHO = "rho-picker";
    private static final Map<Variable, String> variableClasses;
    static {
        variableClasses = new HashMap<>();
        variableClasses.put(Variables.X, X);
        variableClasses.put(Variables.SIGMA_X, SIGMA_X);
        variableClasses.put(Variables.Y, Y);
        variableClasses.put(Variables.SIGMA_Y, SIGMA_Y);
        variableClasses.put(Variables.RHO, RHO);
        variableClasses.put(null, NO_VAR);
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private int colIndex;
    private Variable variable;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a {@code ColumnVariablePicker} for the provided {@code TopsoilSpreadsheetView} at the specified
     * column index.
     *
     * @param   colIndex
     *          the index of the column that this picker describes
     */
    public ColumnVariablePicker(int colIndex) {
        super(COLUMN_PICKER, NO_VAR);
        this.colIndex = colIndex;
        this.variable = null;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void onClick() {
        // do nothing
    }

    public int getColIndex() {
        return colIndex;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable v) {
        if (v != variable) {
//            String oldAbbr = (variable != null ? variable.getAbbreviation() : "null");
//            String newAbbr = (v != null ? v.getAbbreviation() : "null");
            getStyleClass().add(variableClasses.get(v));         // adds new styleclass
            getStyleClass().remove(variableClasses.get(variable));  // removes old styleclass
            variable = v;
        }
    }
}
