package org.cirdles.topsoil.app.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.cirdles.topsoil.variable.Variable;

/**
 * @author marottajb
 */
public class ObservableDataColumn extends TopsoilDataList {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String DEFAULT_HEADER = "Untitled Column";

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /**
     * The {@code String} header for the data column.
     */
    private StringProperty header = new SimpleStringProperty(null);
    public StringProperty headerProperty() {
        return header;
    }
    public final String getHeader() {
        return header.get();
    }
    public final void setHeader(String s) {
        header.set(s);
    }

    /**
     * The {@code Variable} that is set to this column, if one exists.
     */
    private ObjectProperty<Variable> variable;
    public ObjectProperty<Variable> variableProperty() {
        if (variable == null) {
            variable = new SimpleObjectProperty<>(null);
            variable.addListener(c -> {
                if (variable.get() == null) {
                    hasVariable.set(false);
                } else {
                    hasVariable.set(true);
                }
            });
        }
        return variable;
    }
    public final Variable getVariable() {
        return variableProperty().get();
    }
    public final void setVariable(Variable v) {
        variableProperty().set(v);
    }

    /**
     * True if {@code variable} contains a valid variable. False if it contains null.
     */
    private BooleanProperty hasVariable = new SimpleBooleanProperty(variableProperty().get() != null);
    public BooleanProperty hasVariableProperty() {
        return hasVariable;
    }
    public final boolean hasVariable() {
        return hasVariable.get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs an empty data column.
     */
    public ObservableDataColumn() {
        this(DEFAULT_HEADER);
    }

    /**
     * Constructs a new data column containing the provided {@code Double} values.
     *
     * @param values    sequence of Double values
     */
    public ObservableDataColumn(Double... values) {
        this(DEFAULT_HEADER, values);
    }

    /**
     * Constructs a new data row with the provided {@code String} header and {@code Double} values.
     *
     * @param header    String header
     * @param values    sequence of Double values
     */
    public ObservableDataColumn(String header, Double... values) {
        super(values);
        setHeader(header);
    }

}
