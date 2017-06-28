package org.cirdles.topsoil.app.table;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.cirdles.topsoil.app.plot.variable.Variable;

/**
 * A custom {@code ArrayList} for storing data columns. The main purpose is to provide the ability to associate a data
 * column with a {@code Variable}.
 *
 * @author Jake Marotta
 */
public class TopsoilDataColumn extends SimpleListProperty<DoubleProperty> {

    //***********************
    // Properties
    //***********************

    private StringProperty name;
    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(null);
        }
        return name;
    }
    @Override public String getName() {
        return nameProperty().get();
    }
    public void setName(String name) {
        nameProperty().set(name);
    }

    /**
     * An {@code ObjectProperty} containing the {@code Variable} that is set to this column, if one exists.
     */
    private ObjectProperty<Variable> variable;
    public ObjectProperty<Variable> variableProperty() {
        if (variable == null) {
            variable = new SimpleObjectProperty<>(null);
            variable.addListener(c -> {
                if (variable.get() == null) {
                    hasVariableProperty().set(false);
                } else {
                    hasVariableProperty().set(true);
                }
            });
        }

        return variable;
    }
    public Variable getVariable() {
        return variableProperty().get();
    }
    public void setVariable(Variable v) {
        variableProperty().set(v);
    }

    /**
     * A {@code BooleanProperty} tracking whether or not a {@code Variable} is set to this column.
     */
    private BooleanProperty hasVariable;
    public BooleanProperty hasVariableProperty() {
        if (hasVariable == null) {
            hasVariable = new SimpleBooleanProperty(variableProperty().get() != null);
        }
        return hasVariable;
    }
    public Boolean hasVariable() {
        return hasVariableProperty().get();
    }

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs an empty {@code TopsoilDataColumn}.
     */
    public TopsoilDataColumn() {
        this("Untitled Column");
    }

    /**
     * Constructs a new {@code TopsoilDataColumn} with the specified {@code DoubleProperty}s as contents.
     *
     * @param properties    DoubleProperties
     */
    public TopsoilDataColumn(DoubleProperty... properties) {
        this("Untitled Column", properties);
    }

    public TopsoilDataColumn(String name, DoubleProperty... properties) {
        super(FXCollections.observableArrayList());
        setName(name);
        this.addAll(properties);
    }
}